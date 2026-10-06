package com.sportbooking.model;

public class Customer extends User {
    private int rewardPoints;

    public Customer(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void addRewardPoints(int points) {
        if (points > 0) {
            rewardPoints += points;
        }
    }

    @Override
    public String getRole() {
        return "Customer";
    }
}
