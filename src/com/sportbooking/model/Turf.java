package com.sportbooking.model;

import java.util.Set;

public class Turf extends SportsFacility {
    private static final long serialVersionUID = 1L;

    private final int capacity;
    private final boolean floodLights;

    public Turf(String id, String name, String location, double hourlyRate,
                Set<SportType> supportedSports, int openingHour, int closingHour,
                int capacity, boolean floodLights) {
        super(id, name, location, hourlyRate, supportedSports, openingHour, closingHour);
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }
        this.capacity = capacity;
        this.floodLights = floodLights;
    }

    public int getCapacity() { return capacity; }
    public boolean hasFloodLights() { return floodLights; }

    @Override
    protected double calculateFacilitySurcharge(int durationHours, int startHour) {
        return floodLights && startHour >= 18 ? durationHours * 100.0 : 0.0;
    }

    @Override
    public String getFacilityType() {
        return "Turf";
    }

    @Override
    public String getExtraDetails() {
        return "Capacity: " + capacity + ", Flood lights: " + (floodLights ? "Yes" : "No");
    }
}
