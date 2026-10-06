# Mini Project Report — Sports Court & Turf Booking System

## 1. Title
**Sports Court and Turf Booking System using Java Object Oriented Programming**

## 2. Problem Statement
Sports facilities are frequently booked using phone calls, registers, or messaging. This can create double bookings, poor slot visibility, inconsistent customer records, and difficulty generating reports. The project models a simple digital booking workflow using core Java and Object Oriented Programming.

## 3. Objective
To apply OOP concepts to a real-world problem by building a menu-driven Java application that can manage customers, sports courts/turfs, slot availability, bookings, simulated payments, cancellations, reports, and persisted application data.

## 4. Scope
The system provides separate customer and admin flows. Customers can register, search facilities, check availability, book, cancel, and generate receipts. The admin can add facilities, change facility status, inspect bookings/customers, view dashboard metrics, and export a CSV report.

## 5. OOP Concepts Demonstrated
- **Classes and objects:** all domain entities are represented as Java classes.
- **Encapsulation:** fields are private and changed through validated methods.
- **Inheritance:** `Customer` and `Admin` inherit `User`; `Court` and `Turf` inherit `SportsFacility`.
- **Abstraction:** `User` and `SportsFacility` are abstract classes.
- **Polymorphism:** a `List<SportsFacility>` contains both courts and turfs; overridden methods execute according to runtime type.
- **Interface:** `PaymentMethod` is implemented by Cash, UPI, and Card payment classes.
- **Method overriding:** facility-specific surcharge/details and role behavior.
- **Method overloading:** facility search is available with sport-only and sport+location signatures.
- **Association/composition:** a booking associates a customer and facility and owns a payment transaction.
- **Exception handling:** domain-specific exceptions protect invalid booking operations.
- **Collections and enums:** lists, sets, enum sets, and enums model system state.
- **File handling:** Java serialization stores state; CSV/text exports generate reports and receipts.

## 6. Main Modules
1. User and customer management
2. Court/turf facility management
3. Availability and conflict detection
4. Booking and cancellation
5. Payment strategy module
6. Membership discounts and rewards
7. Persistent storage
8. Admin dashboard and report export
9. Console user interface

## 7. Business Rules
- A facility must be ACTIVE to accept a booking.
- The selected sport must be supported by the facility.
- Bookings must fit inside facility operating hours.
- Duration is restricted to 1–4 hours.
- Overlapping bookings for the same facility/date are rejected.
- Cancelled slots become bookable again.
- SILVER and GOLD customers receive membership discounts.
- Turf floodlights and indoor courts can add facility-specific charges through polymorphic calculation.
- Payments are simulated; no real transaction is performed.

## 8. Testing
The repository includes a dependency-free self-test that checks seed data, search, customer registration, booking, overlap rejection, cancellation/refund behavior, maintenance blocking, persistence, CSV generation, receipt generation, and availability calculation.

## 9. Conclusion
The project demonstrates how OOP makes a real-world booking system easier to organize, validate, reuse, test, and extend. Future versions could replace console input with JavaFX/Swing and replace file persistence with a database without changing the core domain design substantially.
