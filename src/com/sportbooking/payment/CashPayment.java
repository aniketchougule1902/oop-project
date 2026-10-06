package com.sportbooking.payment;

import com.sportbooking.model.PaymentTransaction;

import java.util.UUID;

public class CashPayment implements PaymentMethod {
    @Override
    public PaymentTransaction process(double amount) {
        return new PaymentTransaction("CASH-" + shortId(), getMethodName(), amount);
    }

    @Override
    public String getMethodName() {
        return "Cash";
    }

    private String shortId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
