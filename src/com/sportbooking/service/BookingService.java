package com.sportbooking.service;

import com.sportbooking.exception.FacilityUnavailableException;
import com.sportbooking.exception.NotFoundException;
import com.sportbooking.exception.SlotUnavailableException;
import com.sportbooking.model.*;
import com.sportbooking.payment.PaymentMethod;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class BookingService implements Serializable {
    private static final long serialVersionUID = 2L;

    private final List<SportsFacility> facilities = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private int customerSequence = 1001;
    private int bookingSequence = 5001;
    private int facilitySequence = 301;

    public void seedDemoData() {
        if (!facilities.isEmpty() || !customers.isEmpty()) {
            return;
        }

        addFacility(new Court("F101", "Smash Arena", "Pune", 450,
                EnumSet.of(SportType.BADMINTON), 6, 23, true, "Synthetic"));
        addFacility(new Court("F102", "Ace Tennis Court", "Pimpri", 700,
                EnumSet.of(SportType.TENNIS), 6, 22, false, "Hard Court"));
        addFacility(new Turf("F201", "GreenGoal Turf", "Akurdi", 1200,
                EnumSet.of(SportType.FOOTBALL, SportType.CRICKET), 6, 23, 14, true));
        addFacility(new Turf("F202", "Champions Box", "Nigdi", 1000,
                EnumSet.of(SportType.CRICKET, SportType.FOOTBALL), 7, 23, 12, true));

        registerCustomer("Demo Student", "student@example.com", "9876543210", MembershipLevel.SILVER);
        registerCustomer("Demo Player", "player@example.com", "9123456780", MembershipLevel.STANDARD);
    }

    public void addFacility(SportsFacility facility) {
        boolean duplicate = facilities.stream().anyMatch(f -> f.getId().equalsIgnoreCase(facility.getId()));
        if (duplicate) {
            throw new IllegalArgumentException("A facility with id " + facility.getId() + " already exists.");
        }
        facilities.add(facility);
    }

    public String nextFacilityId() {
        return "F" + facilitySequence++;
    }

    public void updateFacilityStatus(String facilityId, FacilityStatus status) {
        findFacility(facilityId).setStatus(status);
    }

    public List<SportsFacility> getFacilities() {
        return facilities.stream()
                .sorted(Comparator.comparing(SportsFacility::getName))
                .toList();
    }

    public List<SportsFacility> searchFacilities(SportType sport) {
        return facilities.stream()
                .filter(f -> f.getStatus() == FacilityStatus.ACTIVE)
                .filter(f -> f.supports(sport))
                .sorted(Comparator.comparingDouble(SportsFacility::getHourlyRate))
                .toList();
    }

    // Method overloading: same operation with an additional location filter.
    public List<SportsFacility> searchFacilities(SportType sport, String location) {
        String term = location == null ? "" : location.trim().toLowerCase(Locale.ROOT);
        return searchFacilities(sport).stream()
                .filter(f -> term.isBlank() || f.getLocation().toLowerCase(Locale.ROOT).contains(term))
                .toList();
    }

    public Customer registerCustomer(String name, String email, String phone, MembershipLevel membership) {
        Optional<Customer> existing = customers.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email) || c.getPhone().equals(phone))
                .findFirst();
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A customer with the same email or phone already exists.");
        }
        Customer customer = new Customer("C" + customerSequence++, name, email, phone, membership);
        customers.add(customer);
        return customer;
    }

    public List<Customer> getCustomers() {
        return customers.stream().sorted(Comparator.comparing(Customer::getName)).toList();
    }

    public Booking createBooking(String customerId, String facilityId, SportType sport,
                                 LocalDate date, int startHour, int durationHours,
                                 PaymentMethod paymentMethod) {
        refreshPastBookings();
        validateBookingDate(date);
        Customer customer = findCustomer(customerId);
        SportsFacility facility = findFacility(facilityId);

        if (facility.getStatus() != FacilityStatus.ACTIVE) {
            throw new FacilityUnavailableException("Facility is currently " + facility.getStatus() + ".");
        }
        if (!facility.supports(sport)) {
            throw new IllegalArgumentException(facility.getName() + " does not support " + sport + ".");
        }
        validateTimeWindow(facility, startHour, durationHours);
        ensureSlotAvailable(facilityId, date, startHour, durationHours);

        double totalAmount = facility.calculatePrice(durationHours, startHour, customer);
        PaymentTransaction payment = paymentMethod.process(totalAmount);
        Booking booking = new Booking("B" + bookingSequence++, customer, facility, sport,
                date, startHour, durationHours, totalAmount, payment);
        bookings.add(booking);

        int basePoints = Math.max(1, (int) Math.floor(totalAmount / 100.0));
        customer.addRewardPoints(basePoints * customer.getMembershipLevel().getRewardMultiplier());
        return booking;
    }

    public void cancelBooking(String bookingId) {
        Booking booking = findBooking(bookingId);
        if (booking.getBookingDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Past bookings cannot be cancelled.");
        }
        booking.cancel();
    }

    public List<Booking> getBookings() {
        refreshPastBookings();
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getBookingDate)
                        .thenComparingInt(Booking::getStartHour))
                .toList();
    }

    public List<Booking> getBookingsForCustomer(String customerId) {
        findCustomer(customerId);
        refreshPastBookings();
        return bookings.stream()
                .filter(b -> b.getCustomer().getId().equalsIgnoreCase(customerId))
                .sorted(Comparator.comparing(Booking::getBookingDate)
                        .thenComparingInt(Booking::getStartHour))
                .toList();
    }

    public List<Integer> getAvailableStartHours(String facilityId, LocalDate date, int durationHours) {
        SportsFacility facility = findFacility(facilityId);
        validateBookingDate(date);
        if (durationHours < 1 || durationHours > 4) {
            throw new IllegalArgumentException("Duration must be between 1 and 4 hours.");
        }
        if (facility.getStatus() != FacilityStatus.ACTIVE) {
            return List.of();
        }

        List<Integer> slots = new ArrayList<>();
        for (int hour = facility.getOpeningHour(); hour + durationHours <= facility.getClosingHour(); hour++) {
            if (isSlotAvailable(facilityId, date, hour, durationHours)) {
                slots.add(hour);
            }
        }
        return slots;
    }

    public DashboardSummary getDashboardSummary() {
        refreshPastBookings();
        int active = (int) facilities.stream().filter(f -> f.getStatus() == FacilityStatus.ACTIVE).count();
        int confirmed = (int) bookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
        int completed = (int) bookings.stream().filter(b -> b.getStatus() == BookingStatus.COMPLETED).count();
        int cancelled = (int) bookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();
        double revenue = bookings.stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .mapToDouble(Booking::getTotalAmount)
                .sum();
        return new DashboardSummary(facilities.size(), active, customers.size(), confirmed, completed, cancelled,
                Math.round(revenue * 100.0) / 100.0);
    }

    public Customer findCustomer(String customerId) {
        return customers.stream()
                .filter(c -> c.getId().equalsIgnoreCase(customerId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Customer not found: " + customerId));
    }

    public SportsFacility findFacility(String facilityId) {
        return facilities.stream()
                .filter(f -> f.getId().equalsIgnoreCase(facilityId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Facility not found: " + facilityId));
    }

    public Booking findBooking(String bookingId) {
        return bookings.stream()
                .filter(b -> b.getId().equalsIgnoreCase(bookingId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
    }

    private void validateBookingDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Booking date is required.");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Booking date cannot be in the past.");
        }
    }

    private void validateTimeWindow(SportsFacility facility, int startHour, int durationHours) {
        if (durationHours < 1 || durationHours > 4) {
            throw new IllegalArgumentException("Duration must be between 1 and 4 hours.");
        }
        if (startHour < facility.getOpeningHour() || startHour + durationHours > facility.getClosingHour()) {
            throw new IllegalArgumentException(String.format(
                    "Booking must fit within facility hours %02d:00-%02d:00.",
                    facility.getOpeningHour(), facility.getClosingHour()));
        }
    }

    private void ensureSlotAvailable(String facilityId, LocalDate date, int startHour, int durationHours) {
        if (!isSlotAvailable(facilityId, date, startHour, durationHours)) {
            throw new SlotUnavailableException("Selected slot overlaps an existing booking.");
        }
    }

    private boolean isSlotAvailable(String facilityId, LocalDate date, int startHour, int durationHours) {
        return bookings.stream()
                .filter(b -> b.getFacility().getId().equalsIgnoreCase(facilityId))
                .noneMatch(b -> b.overlaps(date, startHour, durationHours));
    }

    private void refreshPastBookings() {
        LocalDate today = LocalDate.now();
        bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .filter(b -> b.getBookingDate().isBefore(today))
                .forEach(Booking::markCompleted);
    }
}
