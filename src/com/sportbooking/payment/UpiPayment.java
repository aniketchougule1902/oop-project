package com.sportbooking.payment;

import com.sportbooking.model.PaymentTransaction;

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
    public PaymentTransaction process(double amount) {
        return new PaymentTransaction("UPI-" + shortId(), getMethodName(), amount);
    }

    @Override
    public String getMethodName() {
        return "UPI (" + upiId + ")";
    }

    private String shortId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
