package com.sportbooking.model;

import java.util.Set;

public class Court extends SportsFacility {
    private final boolean indoor;
    private final String surfaceType;

    public Court(String id, String name, String location, double hourlyRate,
                 Set<SportType> supportedSports, boolean indoor, String surfaceType) {
        super(id, name, location, hourlyRate, supportedSports);
        this.indoor = indoor;
        if (surfaceType == null || surfaceType.isBlank()) {
            throw new IllegalArgumentException("Surface type cannot be blank.");
        }
        this.surfaceType = surfaceType.trim();
    }

    public boolean isIndoor() {
        return indoor;
    }

    public String getSurfaceType() {
        return surfaceType;
    }

    @Override
    public String getFacilityType() {
        return "Court";
    }

    @Override
    public String getExtraDetails() {
        return (indoor ? "Indoor" : "Outdoor") + ", Surface: " + surfaceType;
    }
}
