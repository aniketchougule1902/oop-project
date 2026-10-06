package com.sportbooking.app;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputReader {
    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public String text(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int integer(String prompt) {
        String value = text(prompt);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a valid whole number.");
        }
    }

    public double decimal(String prompt) {
        String value = text(prompt);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a valid numeric value.");
        }
    }

    public LocalDate date(String prompt) {
        String value = text(prompt);
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format.");
        }
    }
}
