package com.sportbooking.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public final class PaymentTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String transactionId;
    private final String method;
    private final double amount;
    private PaymentStatus status;
    private final LocalDateTime paidAt;

    public PaymentTransaction(String transactionId, String method, double amount) {
        this.transactionId = transactionId;
        this.method = method;
        this.amount = amount;
        this.status = PaymentStatus.SUCCESS;
        this.paidAt = LocalDateTime.now();
    }

    public String getTransactionId() { return transactionId; }
    public String getMethod() { return method; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }

    public void markRefunded() {
        status = PaymentStatus.REFUNDED;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | Rs. %.2f | %s", transactionId, method, amount, status);
    }
}
