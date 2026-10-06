package com.sportbooking.util;

import com.sportbooking.service.BookingService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDataStore {
    private final Path file;

    public FileDataStore(Path file) {
        this.file = file;
    }

    public BookingService load() {
        if (!Files.exists(file)) {
            return new BookingService();
        }
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(file))) {
            Object object = input.readObject();
            if (object instanceof BookingService service) {
                return service;
            }
            throw new IllegalStateException("Saved data has an unexpected format.");
        } catch (IOException | ClassNotFoundException exception) {
            System.out.println("Warning: saved data could not be loaded. Starting with fresh data.");
            return new BookingService();
        }
    }

    public void save(BookingService service) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(file))) {
            output.writeObject(service);
        }
    }

    public Path getFile() {
        return file;
    }
}
