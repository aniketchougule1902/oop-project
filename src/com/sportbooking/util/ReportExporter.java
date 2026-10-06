package com.sportbooking.util;

import com.sportbooking.model.Booking;
import com.sportbooking.model.Customer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ReportExporter {
    private ReportExporter() {
    }

    public static Path exportBookingsCsv(List<Booking> bookings, Path directory) throws IOException {
        Files.createDirectories(directory);
        Path file = directory.resolve("bookings.csv");
        StringBuilder out = new StringBuilder();
        out.append("Booking ID,Customer,Facility,Sport,Date,Start Hour,Duration,Status,Amount,Payment Method,Payment Status\n");
        for (Booking b : bookings) {
            out.append(csv(b.getId())).append(',')
                    .append(csv(b.getCustomer().getName())).append(',')
                    .append(csv(b.getFacility().getName())).append(',')
                    .append(csv(b.getSport().name())).append(',')
                    .append(b.getBookingDate()).append(',')
                    .append(b.getStartHour()).append(',')
                    .append(b.getDurationHours()).append(',')
                    .append(b.getStatus()).append(',')
                    .append(String.format("%.2f", b.getTotalAmount())).append(',')
                    .append(csv(b.getPayment().getMethod())).append(',')
                    .append(b.getPayment().getStatus()).append('\n');
        }
        Files.writeString(file, out.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return file.toAbsolutePath();
    }

    public static Path exportReceipt(Booking booking, Path directory) throws IOException {
        Files.createDirectories(directory);
        Path file = directory.resolve("receipt-" + booking.getId() + ".txt");
        Customer customer = booking.getCustomer();
        String content = """
                ========================================
                   SPORTS BOOKING PAYMENT RECEIPT
                ========================================
                Booking ID   : %s
                Customer     : %s (%s)
                Facility     : %s
                Sport        : %s
                Date         : %s
                Time         : %02d:00 - %02d:00
                Duration     : %d hour(s)
                Membership   : %s
                Amount Paid  : Rs. %.2f
                Payment      : %s
                Transaction  : %s
                Payment Time : %s
                Status       : %s
                ========================================
                """.formatted(
                booking.getId(), customer.getName(), customer.getId(), booking.getFacility().getName(),
                booking.getSport(), booking.getBookingDate(), booking.getStartHour(), booking.getEndHour(),
                booking.getDurationHours(), customer.getMembershipLevel(), booking.getTotalAmount(),
                booking.getPayment().getMethod(), booking.getPayment().getTransactionId(),
                booking.getPayment().getPaidAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                booking.getStatus());
        Files.writeString(file, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return file.toAbsolutePath();
    }

    private static String csv(String value) {
        String safe = value == null ? "" : value.replace(""", """");
        return """ + safe + """;
    }
}
