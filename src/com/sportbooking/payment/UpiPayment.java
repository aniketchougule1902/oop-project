package com.sportbooking.payment;

import java.util.UUID;

public class UpiPayment implements PaymentMethod {
    private final String upiId;

    public UpiPayment(String upiId) {
        if (upiId == null || !upiId.matches("^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Enter a valid UPI ID such as name@bank.");
        }
        this.upiId = upiId;
    }

    @Override
    public String pay(double amount) {
        return "UPI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public String getMethodName() {
        return "UPI (" + upiId + ")";
    }
}
