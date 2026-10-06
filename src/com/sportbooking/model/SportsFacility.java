package com.sportbooking.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public abstract class SportsFacility {
    private final String id;
    private String name;
    private String location;
    private double hourlyRate;
    private final Set<SportType> supportedSports;

    protected SportsFacility(String id, String name, String location, double hourlyRate,
                             Set<SportType> supportedSports) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Facility id cannot be blank.");
        }
        if (supportedSports == null || supportedSports.isEmpty()) {
            throw new IllegalArgumentException("At least one sport must be supported.");
        }
        this.id = id.trim();
        this.supportedSports = EnumSet.copyOf(supportedSports);
        setName(name);
        setLocation(location);
        setHourlyRate(hourlyRate);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Facility name cannot be blank.");
        }
        this.name = name.trim();
    }

    public String getLocation() {
        return location;
    }

    public final void setLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location cannot be blank.");
        }
        this.location = location.trim();
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public final void setHourlyRate(double hourlyRate) {
        if (hourlyRate <= 0) {
            throw new IllegalArgumentException("Hourly rate must be greater than zero.");
        }
        this.hourlyRate = hourlyRate;
    }

    public Set<SportType> getSupportedSports() {
        return Collections.unmodifiableSet(supportedSports);
    }

    public boolean supports(SportType sport) {
        return supportedSports.contains(sport);
    }

    public double calculatePrice(int durationHours) {
        if (durationHours <= 0) {
            throw new IllegalArgumentException("Duration must be positive.");
        }
        return hourlyRate * durationHours;
    }

    public abstract String getFacilityType();

    public abstract String getExtraDetails();

    @Override
    public String toString() {
        return String.format("%s | %s | %s | Rs. %.2f/hr | Sports: %s | %s",
                id, name, getFacilityType(), hourlyRate, supportedSports, getExtraDetails());
    }
}
