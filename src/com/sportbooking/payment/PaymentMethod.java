package com.sportbooking.payment;

public interface PaymentMethod {
    String pay(double amount);
    String getMethodName();
}
