package com.sportbooking.payment;

import com.sportbooking.model.PaymentTransaction;

import java.util.UUID;

public class CardPayment implements PaymentMethod {
    private final String lastFourDigits;

    public CardPayment(String cardNumber) {
        String digits = cardNumber == null ? "" : cardNumber.replaceAll("\\s+", "");
        if (!digits.matches("\\d{12,19}")) {
            throw new IllegalArgumentException("Card number must contain 12-19 digits.");
        }
        this.lastFourDigits = digits.substring(digits.length() - 4);
    }

    @Override
    public PaymentTransaction process(double amount) {
        return new PaymentTransaction("CARD-" + shortId(), getMethodName(), amount);
    }

    @Override
    public String getMethodName() {
        return "Card ending " + lastFourDigits;
    }

    private String shortId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
