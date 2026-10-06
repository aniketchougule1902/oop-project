package com.sportbooking.service;

public record DashboardSummary(
        int totalFacilities,
        int activeFacilities,
        int totalCustomers,
        int confirmedBookings,
        int completedBookings,
        int cancelledBookings,
        double realizedRevenue
) {
}
