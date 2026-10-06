package com.sportbooking.model;

public class Admin extends User {
    private static final long serialVersionUID = 1L;

    private final String department;

    public Admin(String id, String name, String email, String phone, String department) {
        super(id, name, email, phone);
        this.department = requireText(department, "Department");
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String getRole() {
        return "Admin";
    }
}
