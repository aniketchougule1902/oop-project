package com.sportbooking.app;

import com.sportbooking.exception.BookingException;
import com.sportbooking.model.Booking;
import com.sportbooking.model.Court;
import com.sportbooking.model.Customer;
import com.sportbooking.model.SportType;
import com.sportbooking.model.SportsFacility;
import com.sportbooking.model.Turf;
import com.sportbooking.payment.CashPayment;
import com.sportbooking.payment.PaymentMethod;
import com.sportbooking.payment.UpiPayment;
import com.sportbooking.service.BookingService;
import com.sportbooking.util.DataExporter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;

public class ConsoleApplication {
    private final Scanner scanner = new Scanner(System.in);
    private final BookingService bookingService = new BookingService();

    public void run() {
        seedFacilities();
        seedCustomers();

        System.out.println("===========================================");
        System.out.println("   SPORTS COURT & TURF BOOKING SYSTEM");
        System.out.println("===========================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> listFacilities();
                    case "2" -> searchBySport();
                    case "3" -> registerCustomer();
                    case "4" -> showAvailableSlots();
                    case "5" -> bookFacility();
                    case "6" -> viewBookings();
                    case "7" -> cancelBooking();
                    case "8" -> addFacility();
                    case "9" -> exportBookings();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid option. Choose 0-9.");
                }
            } catch (BookingException | IllegalArgumentException | DateTimeParseException exception) {
                System.out.println("Error: " + exception.getMessage());
            } catch (IOException exception) {
                System.out.println("Could not export bookings: " + exception.getMessage());
            }
        }

        System.out.println("Thank you for using the booking system.");
    }

    private void printMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. View all courts and turfs");
        System.out.println("2. Search facilities by sport");
        System.out.println("3. Register customer");
        System.out.println("4. Check available time slots");
        System.out.println("5. Book a court/turf");
        System.out.println("6. View all bookings");
        System.out.println("7. Cancel booking");
        System.out.println("8. Admin: Add court/turf");
        System.out.println("9. Export booking report");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private void listFacilities() {
        printFacilities(bookingService.getFacilities());
    }

    private void searchBySport() {
        SportType sport = readSport();
        printFacilities(bookingService.searchFacilities(sport));
    }

    private void printFacilities(List<SportsFacility> facilities) {
        if (facilities.isEmpty()) {
            System.out.println("No matching facilities found.");
            return;
        }
        facilities.forEach(System.out::println);
    }

    private void registerCustomer() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("10-digit phone: ");
        String phone = scanner.nextLine();
        Customer customer = bookingService.registerCustomer(name, email, phone);
        System.out.println("Customer registered successfully. ID: " + customer.getId());
    }

    private void showAvailableSlots() {
        listFacilities();
        System.out.print("Facility ID: ");
        String facilityId = scanner.nextLine();
        LocalDate date = readDate();
        int duration = readInt("Required duration (1-4 hours): ");
        List<Integer> hours = bookingService.getAvailableStartHours(facilityId, date, duration);
        System.out.println(hours.isEmpty()
                ? "No slots available."
                : "Available start hours: " + hours.stream().map(h -> String.format("%02d:00", h)).toList());
    }

    private void bookFacility() {
        showCustomers();
        System.out.print("Customer ID: ");
        String customerId = scanner.nextLine();

        listFacilities();
        System.out.print("Facility ID: ");
        String facilityId = scanner.nextLine();

        SportType sport = readSport();
        LocalDate date = readDate();
        int duration = readInt("Duration in hours (1-4): ");
        System.out.println("Available start hours: " + bookingService.getAvailableStartHours(facilityId, date, duration));
        int startHour = readInt("Start hour (6-22, e.g. 18): ");
        PaymentMethod paymentMethod = readPaymentMethod();

        Booking booking = bookingService.createBooking(
                customerId, facilityId, sport, date, startHour, duration, paymentMethod);
        System.out.println("Booking confirmed successfully!");
        System.out.println(booking);
        System.out.println("Reward points: " + booking.getCustomer().getRewardPoints());
    }

    private void viewBookings() {
        List<Booking> bookings = bookingService.getBookings();
        if (bookings.isEmpty()) {
            System.out.println("No bookings available.");
        } else {
            bookings.forEach(System.out::println);
        }
    }

    private void cancelBooking() {
        viewBookings();
        System.out.print("Booking ID to cancel: ");
        bookingService.cancelBooking(scanner.nextLine());
        System.out.println("Booking cancelled successfully.");
    }

    private void addFacility() {
        System.out.print("Facility type (1 = Court, 2 = Turf): ");
        String type = scanner.nextLine().trim();
        System.out.print("Facility ID: ");
        String id = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Location: ");
        String location = scanner.nextLine();
        double rate = readDouble("Hourly rate: ");
        SportType sport = readSport();

        if ("1".equals(type)) {
            System.out.print("Indoor? (y/n): ");
            boolean indoor = scanner.nextLine().trim().equalsIgnoreCase("y");
            System.out.print("Surface type: ");
            String surface = scanner.nextLine();
            bookingService.addFacility(new Court(id, name, location, rate,
                    EnumSet.of(sport), indoor, surface));
        } else if ("2".equals(type)) {
            int capacity = readInt("Player capacity: ");
            System.out.print("Flood lights? (y/n): ");
            boolean lights = scanner.nextLine().trim().equalsIgnoreCase("y");
            bookingService.addFacility(new Turf(id, name, location, rate,
                    EnumSet.of(sport), capacity, lights));
        } else {
            throw new IllegalArgumentException("Unknown facility type.");
        }
        System.out.println("Facility added successfully.");
    }

    private void exportBookings() throws IOException {
        System.out.println("Report exported to: " + DataExporter.exportBookings(bookingService.getBookings()));
    }

    private void showCustomers() {
        System.out.println("--- Customers ---");
        bookingService.getCustomers().forEach(c ->
                System.out.printf("%s | %s | %s | Rewards: %d%n",
                        c.getId(), c.getName(), c.getPhone(), c.getRewardPoints()));
    }

    private SportType readSport() {
        System.out.println("Sports: " + List.of(SportType.values()));
        System.out.print("Sport: ");
        return SportType.valueOf(scanner.nextLine().trim().toUpperCase());
    }

    private LocalDate readDate() {
        System.out.print("Date (YYYY-MM-DD): ");
        return LocalDate.parse(scanner.nextLine().trim());
    }

    private PaymentMethod readPaymentMethod() {
        System.out.print("Payment method (1 = Cash, 2 = UPI): ");
        String choice = scanner.nextLine().trim();
        if ("1".equals(choice)) {
            return new CashPayment();
        }
        if ("2".equals(choice)) {
            System.out.print("UPI ID: ");
            return new UpiPayment(scanner.nextLine().trim());
        }
        throw new IllegalArgumentException("Invalid payment method.");
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a valid whole number.");
        }
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a valid numeric value.");
        }
    }

    private void seedFacilities() {
        bookingService.addFacility(new Court(
                "F101", "Smash Arena", "Pune", 450,
                EnumSet.of(SportType.BADMINTON), true, "Synthetic"));
        bookingService.addFacility(new Court(
                "F102", "Ace Tennis Court", "Pimpri", 700,
                EnumSet.of(SportType.TENNIS), false, "Hard Court"));
        bookingService.addFacility(new Turf(
                "F201", "GreenGoal Turf", "Akurdi", 1200,
                EnumSet.of(SportType.FOOTBALL, SportType.CRICKET), 14, true));
        bookingService.addFacility(new Turf(
                "F202", "Champions Box", "Nigdi", 1000,
                EnumSet.of(SportType.CRICKET, SportType.FOOTBALL), 12, true));
    }

    private void seedCustomers() {
        bookingService.registerCustomer("Demo Student", "student@example.com", "9876543210");
    }
}
