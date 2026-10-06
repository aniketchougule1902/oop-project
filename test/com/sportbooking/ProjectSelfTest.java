package com.sportbooking;

import com.sportbooking.exception.FacilityUnavailableException;
import com.sportbooking.exception.SlotUnavailableException;
import com.sportbooking.model.*;
import com.sportbooking.payment.CashPayment;
import com.sportbooking.payment.UpiPayment;
import com.sportbooking.service.BookingService;
import com.sportbooking.util.FileDataStore;
import com.sportbooking.util.ReportExporter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class ProjectSelfTest {
    private static int checks = 0;

    public static void main(String[] args) throws Exception {
        BookingService service = new BookingService();
        service.seedDemoData();

        check(service.getFacilities().size() == 4, "demo facilities seeded");
        check(service.getCustomers().size() == 2, "demo customers seeded");
        check(service.searchFacilities(SportType.FOOTBALL, "Akurdi").size() == 1,
                "overloaded search filters sport and location");

        Customer gold = service.registerCustomer(
                "Test Gold Player", "gold@test.com", "9000000001", MembershipLevel.GOLD);
        check(gold.getMembershipLevel() == MembershipLevel.GOLD, "customer membership stored");

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Booking first = service.createBooking(gold.getId(), "F201", SportType.FOOTBALL,
                tomorrow, 19, 2, new UpiPayment("gold@upi"));
        check(first.getStatus() == BookingStatus.CONFIRMED, "booking created");
        check(first.getPayment().getStatus() == PaymentStatus.SUCCESS, "payment recorded");
        check(gold.getRewardPoints() > 0, "reward points awarded");

        boolean conflictBlocked = false;
        try {
            service.createBooking("C1001", "F201", SportType.CRICKET,
                    tomorrow, 20, 1, new CashPayment());
        } catch (SlotUnavailableException expected) {
            conflictBlocked = true;
        }
        check(conflictBlocked, "overlapping booking blocked");

        service.cancelBooking(first.getId());
        check(first.getStatus() == BookingStatus.CANCELLED, "booking cancelled");
        check(first.getPayment().getStatus() == PaymentStatus.REFUNDED, "cancelled payment marked refunded");
        check(service.getAvailableStartHours("F201", tomorrow, 2).contains(19),
                "cancelled slot becomes available again");

        service.updateFacilityStatus("F201", FacilityStatus.MAINTENANCE);
        boolean maintenanceBlocked = false;
        try {
            service.createBooking(gold.getId(), "F201", SportType.FOOTBALL,
                    tomorrow.plusDays(1), 18, 1, new CashPayment());
        } catch (FacilityUnavailableException expected) {
            maintenanceBlocked = true;
        }
        check(maintenanceBlocked, "maintenance facility cannot be booked");
        service.updateFacilityStatus("F201", FacilityStatus.ACTIVE);

        Path tempDir = Files.createTempDirectory("sports-booking-test-");
        Path storePath = tempDir.resolve("state.ser");
        FileDataStore store = new FileDataStore(storePath);
        store.save(service);
        BookingService loaded = store.load();
        check(loaded.getCustomers().size() == 3, "persistence round-trip retains customers");
        check(loaded.getBookings().size() == 1, "persistence round-trip retains bookings");

        Path report = ReportExporter.exportBookingsCsv(loaded.getBookings(), tempDir.resolve("reports"));
        check(Files.exists(report), "CSV report generated");
        Path receipt = ReportExporter.exportReceipt(loaded.getBookings().get(0), tempDir.resolve("receipts"));
        check(Files.exists(receipt), "receipt generated");

        List<Integer> slots = loaded.getAvailableStartHours("F102", tomorrow, 1);
        check(!slots.isEmpty(), "availability calculation returns slots");

        System.out.println("ALL SELF-TESTS PASSED (" + checks + " checks)");
    }

    private static void check(boolean condition, String description) {
        checks++;
        if (!condition) {
            throw new AssertionError("FAILED: " + description);
        }
        System.out.println("PASS: " + description);
    }
}
