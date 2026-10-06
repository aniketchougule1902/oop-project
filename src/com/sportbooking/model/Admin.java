package com.sportbooking.model;

public class Admin extends User {
    public Admin(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    @Override
    public String getRole() {
        return "Admin";
    }
}
