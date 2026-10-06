package com.sportbooking.model;

import java.io.Serializable;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;
    private String email;
    private String phone;

    protected User(String id, String name, String email, String phone) {
        this.id = requireText(id, "User id");
        setName(name);
        setEmail(email);
        setPhone(phone);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = requireText(name, "Name");
    }

    public String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        String value = requireText(email, "Email");
        if (!value.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        this.email = value.toLowerCase();
    }

    public String getPhone() {
        return phone;
    }

    public final void setPhone(String phone) {
        String value = requireText(phone, "Phone");
        if (!value.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
        }
        this.phone = value;
    }

    public abstract String getRole();

    protected static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank.");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | %s", id, getRole(), name, email, phone);
    }
}
