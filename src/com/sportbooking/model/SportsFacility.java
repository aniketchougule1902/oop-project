package com.sportbooking.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public abstract class SportsFacility implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;
    private String location;
    private double hourlyRate;
    private final Set<SportType> supportedSports;
    private FacilityStatus status;
    private final int openingHour;
    private final int closingHour;

    protected SportsFacility(String id, String name, String location, double hourlyRate,
                             Set<SportType> supportedSports, int openingHour, int closingHour) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Facility id cannot be blank.");
        }
        if (supportedSports == null || supportedSports.isEmpty()) {
            throw new IllegalArgumentException("At least one sport must be supported.");
        }
        if (openingHour < 0 || closingHour > 24 || openingHour >= closingHour) {
            throw new IllegalArgumentException("Invalid facility operating hours.");
        }
        this.id = id.trim();
        this.supportedSports = EnumSet.copyOf(supportedSports);
        this.openingHour = openingHour;
        this.closingHour = closingHour;
        this.status = FacilityStatus.ACTIVE;
        setName(name);
        setLocation(location);
        setHourlyRate(hourlyRate);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public double getHourlyRate() { return hourlyRate; }
    public FacilityStatus getStatus() { return status; }
    public int getOpeningHour() { return openingHour; }
    public int getClosingHour() { return closingHour; }

    public final void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Facility name cannot be blank.");
        }
        this.name = name.trim();
    }

    public final void setLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location cannot be blank.");
        }
        this.location = location.trim();
    }

    public final void setHourlyRate(double hourlyRate) {
        if (hourlyRate <= 0) {
            throw new IllegalArgumentException("Hourly rate must be greater than zero.");
        }
        this.hourlyRate = hourlyRate;
    }

    public void setStatus(FacilityStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Facility status cannot be null.");
        }
        this.status = status;
    }

    public Set<SportType> getSupportedSports() {
        return Collections.unmodifiableSet(supportedSports);
    }

    public boolean supports(SportType sport) {
        return supportedSports.contains(sport);
    }

    public final double calculatePrice(int durationHours, int startHour, Customer customer) {
        if (durationHours <= 0) {
            throw new IllegalArgumentException("Duration must be positive.");
        }
        double gross = hourlyRate * durationHours + calculateFacilitySurcharge(durationHours, startHour);
        double discount = gross * customer.getMembershipLevel().getDiscountRate();
        return roundMoney(gross - discount);
    }

    protected abstract double calculateFacilitySurcharge(int durationHours, int startHour);

    public abstract String getFacilityType();

    public abstract String getExtraDetails();

    protected static double roundMoney(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s | %-5s | %-20s | %-10s | Rs. %.2f/hr | %02d:00-%02d:00 | %-11s | %s | %s",
                id, getFacilityType(), name, location, hourlyRate, openingHour, closingHour,
                status, supportedSports, getExtraDetails());
    }
}
