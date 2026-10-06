package com.sportbooking.util;

import com.sportbooking.model.Booking;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public final class DataExporter {
    private DataExporter() {
    }

    public static Path exportBookings(List<Booking> bookings) throws IOException {
        Path outputDirectory = Path.of("data");
        Files.createDirectories(outputDirectory);
        Path file = outputDirectory.resolve("bookings.txt");

        StringBuilder content = new StringBuilder("SPORTS COURT & TURF BOOKING REPORT\n");
        content.append("==================================\n");
        if (bookings.isEmpty()) {
            content.append("No bookings available.\n");
        } else {
            bookings.forEach(booking -> content.append(booking).append(System.lineSeparator()));
        }

        Files.writeString(file, content.toString(),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return file.toAbsolutePath();
    }
}
