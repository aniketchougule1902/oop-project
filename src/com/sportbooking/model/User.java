package com.sportbooking.model;

public abstract class User {
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
        if (!value.contains("@")) {
            throw new IllegalArgumentException("Email must contain @.");
        }
        this.email = value;
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
        return getRole() + "{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
