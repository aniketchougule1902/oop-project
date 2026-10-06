package com.sportbooking.model;

import java.util.Set;

public class Turf extends SportsFacility {
    private final int capacity;
    private final boolean floodLights;

    public Turf(String id, String name, String location, double hourlyRate,
                Set<SportType> supportedSports, int capacity, boolean floodLights) {
        super(id, name, location, hourlyRate, supportedSports);
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }
        this.capacity = capacity;
        this.floodLights = floodLights;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean hasFloodLights() {
        return floodLights;
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
