package com.sportbooking.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking {
    private final String id;
    private final Customer customer;
    private final SportsFacility facility;
    private final SportType sport;
    private final LocalDate bookingDate;
    private final int startHour;
    private final int durationHours;
    private final double totalAmount;
    private final LocalDateTime createdAt;
    private BookingStatus status;
    private String paymentReference;

    public Booking(String id, Customer customer, SportsFacility facility, SportType sport,
                   LocalDate bookingDate, int startHour, int durationHours) {
        if (id == null || id.isBlank() || customer == null || facility == null || sport == null || bookingDate == null) {
            throw new IllegalArgumentException("Booking details cannot be null or blank.");
        }
        if (startHour < 6 || startHour > 22) {
            throw new IllegalArgumentException("Start hour must be between 6 and 22.");
        }
        if (durationHours < 1 || durationHours > 4 || startHour + durationHours > 23) {
            throw new IllegalArgumentException("Duration must be 1-4 hours and finish by 23:00.");
        }
        this.id = id;
        this.customer = customer;
        this.facility = facility;
        this.sport = sport;
        this.bookingDate = bookingDate;
        this.startHour = startHour;
        this.durationHours = durationHours;
        this.totalAmount = facility.calculatePrice(durationHours);
        this.status = BookingStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
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
    public BookingStatus getStatus() { return status; }
    public String getPaymentReference() { return paymentReference; }

    public int getEndHour() {
        return startHour + durationHours;
    }

    public void cancel() {
        status = BookingStatus.CANCELLED;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
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
        return String.format(
                "%s | %s | %s | %s | %02d:00-%02d:00 | %s | Rs. %.2f | Payment: %s",
                id, customer.getName(), facility.getName(), bookingDate,
                startHour, getEndHour(), status, totalAmount,
                paymentReference == null ? "Pending" : paymentReference);
    }
}
