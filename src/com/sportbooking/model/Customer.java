package com.sportbooking.model;

public class Customer extends User {
    private static final long serialVersionUID = 1L;

    private MembershipLevel membershipLevel;
    private int rewardPoints;

    public Customer(String id, String name, String email, String phone, MembershipLevel membershipLevel) {
        super(id, name, email, phone);
        this.membershipLevel = membershipLevel == null ? MembershipLevel.STANDARD : membershipLevel;
    }

    public MembershipLevel getMembershipLevel() {
        return membershipLevel;
    }

    public void setMembershipLevel(MembershipLevel membershipLevel) {
        if (membershipLevel == null) {
            throw new IllegalArgumentException("Membership level cannot be null.");
        }
        this.membershipLevel = membershipLevel;
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

    @Override
    public String toString() {
        return super.toString() + String.format(" | %s | Rewards: %d", membershipLevel, rewardPoints);
    }
}
