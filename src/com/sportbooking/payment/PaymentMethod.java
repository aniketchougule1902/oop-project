package com.sportbooking.payment;

import com.sportbooking.model.PaymentTransaction;

public interface PaymentMethod {
    PaymentTransaction process(double amount);
    String getMethodName();
}
