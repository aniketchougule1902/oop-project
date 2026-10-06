# Mini Project Summary — Sports Court & Turf Booking System

## Problem Statement
Manual booking of sports courts and turfs can lead to scheduling conflicts, poor availability tracking, and difficulty maintaining customer and booking records. This project provides a simple console-based system to manage these activities digitally.

## Proposed Solution
The application stores different types of sports facilities, customers, bookings, and payment methods as Java objects. A booking service applies business rules such as sport compatibility, date validation, and slot-conflict detection. Users interact with the system through a menu-driven console interface.

## Major Modules
1. **Facility Management** — Courts and turfs with sport, rate, location, and type-specific details.
2. **Customer Management** — Registration and reward points.
3. **Availability Management** — Shows bookable time slots and prevents overlaps.
4. **Booking Management** — Creates, displays, and cancels bookings.
5. **Payment Module** — Demonstrates interface-based Cash and UPI payment strategies.
6. **Report Export** — Writes a booking report to a text file.

## OOP Design
The project uses abstract classes for shared concepts (`User`, `SportsFacility`), inheritance for specialized entities, interfaces for interchangeable payment behavior, encapsulation for validation and data safety, and runtime polymorphism throughout the facility and payment modules.

## Conclusion
The project demonstrates how core Java and OOP concepts can model a practical booking problem with maintainable, reusable, and extensible code.
