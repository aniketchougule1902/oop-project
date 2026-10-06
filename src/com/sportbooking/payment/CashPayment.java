package com.sportbooking.payment;

public class CashPayment implements PaymentMethod {
    @Override
    public String pay(double amount) {
        return String.format("CASH-Rs%.2f", amount);
    }

    @Override
    public String getMethodName() {
        return "Cash";
    }
}
