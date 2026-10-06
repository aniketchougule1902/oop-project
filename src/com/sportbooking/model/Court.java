package com.sportbooking.model;

import java.util.Set;

public class Court extends SportsFacility {
    private static final long serialVersionUID = 1L;

    private final boolean indoor;
    private final String surfaceType;

    public Court(String id, String name, String location, double hourlyRate,
                 Set<SportType> supportedSports, int openingHour, int closingHour,
                 boolean indoor, String surfaceType) {
        super(id, name, location, hourlyRate, supportedSports, openingHour, closingHour);
        this.indoor = indoor;
        if (surfaceType == null || surfaceType.isBlank()) {
            throw new IllegalArgumentException("Surface type cannot be blank.");
        }
        this.surfaceType = surfaceType.trim();
    }

    public boolean isIndoor() { return indoor; }
    public String getSurfaceType() { return surfaceType; }

    @Override
    protected double calculateFacilitySurcharge(int durationHours, int startHour) {
        return indoor ? durationHours * 25.0 : 0.0;
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
