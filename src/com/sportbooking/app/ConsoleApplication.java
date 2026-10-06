package com.sportbooking.app;

import com.sportbooking.exception.BookingException;
import com.sportbooking.model.*;
import com.sportbooking.payment.CardPayment;
import com.sportbooking.payment.CashPayment;
import com.sportbooking.payment.PaymentMethod;
import com.sportbooking.payment.UpiPayment;
import com.sportbooking.service.BookingService;
import com.sportbooking.service.DashboardSummary;
import com.sportbooking.util.FileDataStore;
import com.sportbooking.util.ReportExporter;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ConsoleApplication {
    private static final String ADMIN_PIN = "1234";

    private final Scanner scanner = new Scanner(System.in);
    private final InputReader input = new InputReader(scanner);
    private final FileDataStore dataStore = new FileDataStore(Path.of("data", "booking-system.ser"));
    private BookingService service;

    public void run() {
        service = dataStore.load();
        service.seedDemoData();
        saveQuietly();

        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = input.text("Choose an option: ");
            try {
                switch (choice) {
                    case "1" -> listFacilities();
                    case "2" -> searchFacilities();
                    case "3" -> customerPortal();
                    case "4" -> adminPortal();
                    case "5" -> showOopConcepts();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid option. Choose 0-5.");
                }
            } catch (BookingException | IllegalArgumentException exception) {
                printError(exception.getMessage());
            } catch (IOException exception) {
                printError("File operation failed: " + exception.getMessage());
            }
        }

        saveQuietly();
        System.out.println("\nData saved. Thank you for using SportsBook!");
    }

    private void printBanner() {
        System.out.println("\n============================================================");
        System.out.println("        SPORTS COURT & TURF BOOKING SYSTEM - JAVA OOP");
        System.out.println("============================================================");
        System.out.println("Demo customer: C1001 | Admin PIN: 1234");
        System.out.println("Persistent data file: " + dataStore.getFile());
    }

    private void printMainMenu() {
        System.out.println("\n---------------------- MAIN MENU ----------------------");
        System.out.println("1. Browse all courts and turfs");
        System.out.println("2. Search facilities");
        System.out.println("3. Customer portal");
        System.out.println("4. Admin portal");
        System.out.println("5. View OOP concepts used");
        System.out.println("0. Save and exit");
    }

    private void customerPortal() throws IOException {
        boolean inside = true;
        while (inside) {
            System.out.println("\n------------------- CUSTOMER PORTAL -------------------");
            System.out.println("1. Register new customer");
            System.out.println("2. View customer profile");
            System.out.println("3. Check available slots");
            System.out.println("4. Book a court/turf");
            System.out.println("5. View my bookings");
            System.out.println("6. Cancel booking");
            System.out.println("7. Export booking receipt");
            System.out.println("0. Back to main menu");

            try {
                switch (input.text("Choose an option: ")) {
                    case "1" -> registerCustomer();
                    case "2" -> viewCustomerProfile();
                    case "3" -> showAvailableSlots();
                    case "4" -> createBooking();
                    case "5" -> viewCustomerBookings();
                    case "6" -> cancelBooking();
                    case "7" -> exportReceipt();
                    case "0" -> inside = false;
                    default -> System.out.println("Invalid customer option.");
                }
            } catch (BookingException | IllegalArgumentException exception) {
                printError(exception.getMessage());
            }
        }
    }

    private void adminPortal() throws IOException {
        String pin = input.text("Enter admin PIN: ");
        if (!ADMIN_PIN.equals(pin)) {
            printError("Incorrect admin PIN.");
            return;
        }

        boolean inside = true;
        while (inside) {
            System.out.println("\n--------------------- ADMIN PORTAL ---------------------");
            System.out.println("1. Dashboard summary");
            System.out.println("2. View all customers");
            System.out.println("3. Add court/turf");
            System.out.println("4. Change facility status");
            System.out.println("5. View all bookings");
            System.out.println("6. Export booking CSV report");
            System.out.println("0. Back to main menu");

            try {
                switch (input.text("Choose an option: ")) {
                    case "1" -> showDashboard();
                    case "2" -> listCustomers();
                    case "3" -> addFacility();
                    case "4" -> changeFacilityStatus();
                    case "5" -> listBookings(service.getBookings());
                    case "6" -> exportBookingReport();
                    case "0" -> inside = false;
                    default -> System.out.println("Invalid admin option.");
                }
            } catch (BookingException | IllegalArgumentException exception) {
                printError(exception.getMessage());
            }
        }
    }

    private void listFacilities() {
        System.out.println("\n---------------------- FACILITIES ----------------------");
        List<SportsFacility> facilities = service.getFacilities();
        if (facilities.isEmpty()) {
            System.out.println("No facilities available.");
            return;
        }
        facilities.forEach(System.out::println);
    }

    private void searchFacilities() {
        SportType sport = readSport();
        String location = input.text("Location filter (press Enter for any): ");
        List<SportsFacility> results = service.searchFacilities(sport, location);
        System.out.println("\nSearch results:");
        if (results.isEmpty()) {
            System.out.println("No active facility matched your search.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private void registerCustomer() {
        String name = input.text("Name: ");
        String email = input.text("Email: ");
        String phone = input.text("10-digit phone: ");
        MembershipLevel membership = readMembership();
        Customer customer = service.registerCustomer(name, email, phone, membership);
        saveQuietly();
        System.out.println("Registered successfully. Your customer ID is " + customer.getId());
    }

    private void viewCustomerProfile() {
        Customer customer = service.findCustomer(input.text("Customer ID: "));
        System.out.println(customer);
        System.out.printf("Membership discount: %.0f%%%n", customer.getMembershipLevel().getDiscountRate() * 100);
    }

    private void showAvailableSlots() {
        listFacilities();
        String facilityId = input.text("Facility ID: ");
        LocalDate date = input.date("Date (YYYY-MM-DD): ");
        int duration = input.integer("Duration (1-4 hours): ");
        List<Integer> slots = service.getAvailableStartHours(facilityId, date, duration);
        if (slots.isEmpty()) {
            System.out.println("No slots available for that selection.");
        } else {
            System.out.println("Available start times: " +
                    slots.stream().map(h -> String.format("%02d:00", h)).toList());
        }
    }

    private void createBooking() throws IOException {
        listCustomersBrief();
        String customerId = input.text("Customer ID: ");
        Customer customer = service.findCustomer(customerId);

        searchFacilities();
        String facilityId = input.text("Facility ID to book: ");
        SportsFacility facility = service.findFacility(facilityId);
        SportType sport = readSport();
        LocalDate date = input.date("Booking date (YYYY-MM-DD): ");
        int duration = input.integer("Duration (1-4 hours): ");

        List<Integer> slots = service.getAvailableStartHours(facilityId, date, duration);
        System.out.println("Available start hours: " + slots);
        int startHour = input.integer("Start hour (e.g. 18): ");

        double preview = facility.calculatePrice(duration, startHour, customer);
        System.out.printf("Price after %s membership discount and facility charges: Rs. %.2f%n",
                customer.getMembershipLevel(), preview);

        PaymentMethod payment = readPaymentMethod();
        Booking booking = service.createBooking(customerId, facilityId, sport, date, startHour, duration, payment);
        saveQuietly();

        System.out.println("\nBooking confirmed successfully!");
        System.out.println(booking);
        System.out.println("Transaction: " + booking.getPayment());
        System.out.println("Reward points balance: " + customer.getRewardPoints());
        System.out.println("Receipt: " + ReportExporter.exportReceipt(booking, Path.of("data", "receipts")));
    }

    private void viewCustomerBookings() {
        String customerId = input.text("Customer ID: ");
        listBookings(service.getBookingsForCustomer(customerId));
    }

    private void cancelBooking() {
        String customerId = input.text("Customer ID: ");
        List<Booking> mine = service.getBookingsForCustomer(customerId);
        listBookings(mine);
        String bookingId = input.text("Booking ID to cancel: ");
        Booking booking = service.findBooking(bookingId);
        if (!booking.getCustomer().getId().equalsIgnoreCase(customerId)) {
            throw new IllegalArgumentException("That booking does not belong to this customer.");
        }
        service.cancelBooking(bookingId);
        saveQuietly();
        System.out.println("Booking cancelled. Simulated payment status: " + booking.getPayment().getStatus());
    }

    private void exportReceipt() throws IOException {
        String customerId = input.text("Customer ID: ");
        String bookingId = input.text("Booking ID: ");
        Booking booking = service.findBooking(bookingId);
        if (!booking.getCustomer().getId().equalsIgnoreCase(customerId)) {
            throw new IllegalArgumentException("That booking does not belong to this customer.");
        }
        Path receipt = ReportExporter.exportReceipt(booking, Path.of("data", "receipts"));
        System.out.println("Receipt exported to: " + receipt);
    }

    private void showDashboard() {
        DashboardSummary d = service.getDashboardSummary();
        System.out.println("\n-------------------- ADMIN DASHBOARD -------------------");
        System.out.println("Total facilities    : " + d.totalFacilities());
        System.out.println("Active facilities   : " + d.activeFacilities());
        System.out.println("Registered customers: " + d.totalCustomers());
        System.out.println("Confirmed bookings  : " + d.confirmedBookings());
        System.out.println("Completed bookings  : " + d.completedBookings());
        System.out.println("Cancelled bookings  : " + d.cancelledBookings());
        System.out.printf("Realized revenue     : Rs. %.2f%n", d.realizedRevenue());
    }

    private void listCustomers() {
        System.out.println("\n---------------------- CUSTOMERS -----------------------");
        service.getCustomers().forEach(System.out::println);
    }

    private void listCustomersBrief() {
        System.out.println("\nAvailable customer accounts:");
        service.getCustomers().forEach(c -> System.out.printf("%s | %-20s | %-8s | Rewards: %d%n",
                c.getId(), c.getName(), c.getMembershipLevel(), c.getRewardPoints()));
    }

    private void addFacility() {
        String type = input.text("Facility type (1 = Court, 2 = Turf): ");
        String id = input.text("Facility ID (Enter for auto-generated): ");
        if (id.isBlank()) {
            id = service.nextFacilityId();
        }
        String name = input.text("Name: ");
        String location = input.text("Location: ");
        double rate = input.decimal("Hourly rate: Rs. ");
        Set<SportType> sports = readSportSet();
        int opening = input.integer("Opening hour (0-23): ");
        int closing = input.integer("Closing hour (1-24): ");

        SportsFacility facility;
        if ("1".equals(type)) {
            boolean indoor = yesNo("Indoor court? (y/n): ");
            String surface = input.text("Surface type: ");
            facility = new Court(id, name, location, rate, sports, opening, closing, indoor, surface);
        } else if ("2".equals(type)) {
            int capacity = input.integer("Player capacity: ");
            boolean lights = yesNo("Flood lights available? (y/n): ");
            facility = new Turf(id, name, location, rate, sports, opening, closing, capacity, lights);
        } else {
            throw new IllegalArgumentException("Unknown facility type.");
        }

        service.addFacility(facility);
        saveQuietly();
        System.out.println("Facility added successfully: " + facility.getId());
    }

    private void changeFacilityStatus() {
        listFacilities();
        String id = input.text("Facility ID: ");
        System.out.println("Statuses: " + List.of(FacilityStatus.values()));
        FacilityStatus status = FacilityStatus.valueOf(input.text("New status: ").toUpperCase());
        service.updateFacilityStatus(id, status);
        saveQuietly();
        System.out.println("Facility status updated.");
    }

    private void exportBookingReport() throws IOException {
        Path report = ReportExporter.exportBookingsCsv(service.getBookings(), Path.of("data", "reports"));
        System.out.println("CSV report exported to: " + report);
    }

    private void listBookings(List<Booking> bookings) {
        System.out.println("\n----------------------- BOOKINGS -----------------------");
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        bookings.forEach(System.out::println);
    }

    private SportType readSport() {
        System.out.println("Sports: " + List.of(SportType.values()));
        String value = input.text("Sport: ").toUpperCase().replace(' ', '_');
        try {
            return SportType.valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown sport: " + value);
        }
    }

    private Set<SportType> readSportSet() {
        System.out.println("Available sports: " + List.of(SportType.values()));
        String value = input.text("Supported sports (comma-separated, e.g. CRICKET,FOOTBALL): ");
        if (value.isBlank()) {
            throw new IllegalArgumentException("At least one sport is required.");
        }
        EnumSet<SportType> result = EnumSet.noneOf(SportType.class);
        for (String item : value.split(",")) {
            result.add(SportType.valueOf(item.trim().toUpperCase().replace(' ', '_')));
        }
        return result;
    }

    private MembershipLevel readMembership() {
        System.out.println("Memberships: STANDARD (0%), SILVER (5%), GOLD (10%)");
        String value = input.text("Membership: ").toUpperCase();
        try {
            return MembershipLevel.valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown membership level.");
        }
    }

    private PaymentMethod readPaymentMethod() {
        System.out.println("1. Cash\n2. UPI\n3. Card (simulated)");
        return switch (input.text("Payment method: ")) {
            case "1" -> new CashPayment();
            case "2" -> new UpiPayment(input.text("UPI ID: "));
            case "3" -> new CardPayment(input.text("Card number (simulation only): "));
            default -> throw new IllegalArgumentException("Invalid payment method.");
        };
    }

    private boolean yesNo(String prompt) {
        String value = input.text(prompt);
        if (value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes")) {
            return true;
        }
        if (value.equalsIgnoreCase("n") || value.equalsIgnoreCase("no")) {
            return false;
        }
        throw new IllegalArgumentException("Enter y or n.");
    }

    private void showOopConcepts() {
        System.out.println("""
                \n------------------ OOP CONCEPTS USED ------------------
                1. Classes & Objects   : Booking, Customer, Court, Turf, PaymentTransaction
                2. Encapsulation       : private fields + validated methods/getters/setters
                3. Inheritance         : Customer/Admin -> User; Court/Turf -> SportsFacility
                4. Abstraction         : abstract User and SportsFacility classes
                5. Polymorphism        : List<SportsFacility> stores Court and Turf objects
                6. Interfaces          : PaymentMethod implemented by Cash/UPI/Card payments
                7. Method Overriding   : facility type/details/surcharge and user role methods
                8. Method Overloading  : searchFacilities(sport) and searchFacilities(sport, location)
                9. Association         : Booking links Customer, SportsFacility and PaymentTransaction
                10. Exception Handling : custom booking/facility/slot exceptions
                11. Collections/Enums  : List, Set, EnumSet and domain enums
                12. File Handling      : object persistence, CSV reports and text receipts
                ---------------------------------------------------------
                """);
    }

    private void saveQuietly() {
        try {
            dataStore.save(service);
        } catch (IOException exception) {
            printError("Warning: data could not be saved: " + exception.getMessage());
        }
    }

    private void printError(String message) {
        System.out.println("[ERROR] " + message);
    }
}
