package com.sportbooking.service;

import com.sportbooking.exception.NotFoundException;
import com.sportbooking.exception.SlotUnavailableException;
import com.sportbooking.model.Booking;
import com.sportbooking.model.BookingStatus;
import com.sportbooking.model.Customer;
import com.sportbooking.model.SportType;
import com.sportbooking.model.SportsFacility;
import com.sportbooking.payment.PaymentMethod;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class BookingService {
    private final List<SportsFacility> facilities = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private int customerSequence = 1001;
    private int bookingSequence = 5001;

    public void addFacility(SportsFacility facility) {
        boolean duplicate = facilities.stream().anyMatch(f -> f.getId().equalsIgnoreCase(facility.getId()));
        if (duplicate) {
            throw new IllegalArgumentException("A facility with id " + facility.getId() + " already exists.");
        }
        facilities.add(facility);
    }

    public List<SportsFacility> getFacilities() {
        return facilities.stream()
                .sorted(Comparator.comparing(SportsFacility::getName))
                .toList();
    }

    public List<SportsFacility> searchFacilities(SportType sport) {
        return facilities.stream()
                .filter(f -> f.supports(sport))
                .sorted(Comparator.comparingDouble(SportsFacility::getHourlyRate))
                .toList();
    }

    public Customer registerCustomer(String name, String email, String phone) {
        Optional<Customer> existing = customers.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email) || c.getPhone().equals(phone))
                .findFirst();
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A customer with the same email or phone already exists.");
        }
        Customer customer = new Customer("C" + customerSequence++, name, email.toLowerCase(Locale.ROOT), phone);
        customers.add(customer);
        return customer;
    }

    public List<Customer> getCustomers() {
        return List.copyOf(customers);
    }

    public Booking createBooking(String customerId, String facilityId, SportType sport,
                                 LocalDate date, int startHour, int durationHours,
                                 PaymentMethod paymentMethod) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Booking date cannot be in the past.");
        }

        Customer customer = findCustomer(customerId);
        SportsFacility facility = findFacility(facilityId);
        if (!facility.supports(sport)) {
            throw new IllegalArgumentException(facility.getName() + " does not support " + sport + ".");
        }
        validateSlot(facilityId, date, startHour, durationHours);

        Booking booking = new Booking("B" + bookingSequence++, customer, facility, sport, date, startHour, durationHours);
        String paymentReference = paymentMethod.pay(booking.getTotalAmount());
        booking.setPaymentReference(paymentMethod.getMethodName() + ": " + paymentReference);
        bookings.add(booking);
        customer.addRewardPoints((int) Math.round(booking.getTotalAmount() / 100.0));
        return booking;
    }

    public void cancelBooking(String bookingId) {
        Booking booking = findBooking(bookingId);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled.");
        }
        booking.cancel();
    }

    public List<Booking> getBookings() {
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getBookingDate)
                        .thenComparingInt(Booking::getStartHour))
                .toList();
    }

    public List<Booking> getBookingsForCustomer(String customerId) {
        return bookings.stream()
                .filter(b -> b.getCustomer().getId().equalsIgnoreCase(customerId))
                .sorted(Comparator.comparing(Booking::getBookingDate))
                .toList();
    }

    public List<Integer> getAvailableStartHours(String facilityId, LocalDate date, int durationHours) {
        findFacility(facilityId);
        List<Integer> slots = new ArrayList<>();
        for (int hour = 6; hour + durationHours <= 23; hour++) {
            if (isSlotAvailable(facilityId, date, hour, durationHours)) {
                slots.add(hour);
            }
        }
        return slots;
    }

    private void validateSlot(String facilityId, LocalDate date, int startHour, int durationHours) {
        if (!isSlotAvailable(facilityId, date, startHour, durationHours)) {
            throw new SlotUnavailableException("Selected slot overlaps an existing confirmed booking.");
        }
    }

    private boolean isSlotAvailable(String facilityId, LocalDate date, int startHour, int durationHours) {
        return bookings.stream()
                .filter(b -> b.getFacility().getId().equalsIgnoreCase(facilityId))
                .noneMatch(b -> b.overlaps(date, startHour, durationHours));
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
}
