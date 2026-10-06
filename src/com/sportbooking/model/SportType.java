package com.sportbooking.model;

public enum SportType {
    BADMINTON("Badminton"),
    BASKETBALL("Basketball"),
    CRICKET("Cricket"),
    FOOTBALL("Football"),
    TENNIS("Tennis"),
    VOLLEYBALL("Volleyball");

    private final String displayName;

    SportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
