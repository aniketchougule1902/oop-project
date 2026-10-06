package com.sportbooking.model;

public enum MembershipLevel {
    STANDARD(0.00, 1),
    SILVER(0.05, 2),
    GOLD(0.10, 3);

    private final double discountRate;
    private final int rewardMultiplier;

    MembershipLevel(double discountRate, int rewardMultiplier) {
        this.discountRate = discountRate;
        this.rewardMultiplier = rewardMultiplier;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public int getRewardMultiplier() {
        return rewardMultiplier;
    }
}
