package com.sportbooking.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final Customer customer;
    private final SportsFacility facility;
    private final SportType sport;
    private final LocalDate bookingDate;
    private final int startHour;
    private final int durationHours;
    private final double totalAmount;
    private final LocalDateTime createdAt;
    private final PaymentTransaction payment;
    private BookingStatus status;

    public Booking(String id, Customer customer, SportsFacility facility, SportType sport,
                   LocalDate bookingDate, int startHour, int durationHours,
                   double totalAmount, PaymentTransaction payment) {
        if (id == null || id.isBlank() || customer == null || facility == null || sport == null ||
                bookingDate == null || payment == null) {
            throw new IllegalArgumentException("Booking details cannot be null or blank.");
        }
        this.id = id;
        this.customer = customer;
        this.facility = facility;
        this.sport = sport;
        this.bookingDate = bookingDate;
        this.startHour = startHour;
        this.durationHours = durationHours;
        this.totalAmount = totalAmount;
        this.payment = payment;
        this.createdAt = LocalDateTime.now();
        this.status = BookingStatus.CONFIRMED;
    }

    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public SportsFacility getFacility() { return facility; }
    public SportType getSport() { return sport; }
    public LocalDate getBookingDate() { return bookingDate; }
    public int getStartHour() { return startHour; }
    public int getDurationHours() { return durationHours; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public PaymentTransaction getPayment() { return payment; }
    public BookingStatus getStatus() { return status; }

    public int getEndHour() {
        return startHour + durationHours;
    }

    public void cancel() {
        if (status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled.");
        }
        status = BookingStatus.CANCELLED;
        payment.markRefunded();
    }

    public void markCompleted() {
        if (status == BookingStatus.CONFIRMED) {
            status = BookingStatus.COMPLETED;
        }
    }

    public boolean overlaps(LocalDate date, int proposedStart, int proposedDuration) {
        if (status == BookingStatus.CANCELLED || !bookingDate.equals(date)) {
            return false;
        }
        int proposedEnd = proposedStart + proposedDuration;
        return proposedStart < getEndHour() && proposedEnd > startHour;
    }

    @Override
    public String toString() {
        return String.format("%s | %-18s | %-18s | %-10s | %s | %02d:00-%02d:00 | %-9s | Rs. %.2f | %s",
                id, customer.getName(), facility.getName(), sport, bookingDate, startHour, getEndHour(),
                status, totalAmount, payment.getMethod());
    }
}
