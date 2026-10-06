# Sports Court & Turf Booking System (Java OOP Mini Project)

A console-based **Sports Court and Turf Booking System** developed in Java for an Object Oriented Programming mini project. The system models a real-world booking workflow where customers can explore sports facilities, check availability, reserve courts/turfs, make a simulated payment, cancel bookings, and export a booking report.

## Objective

The project demonstrates how Object Oriented Programming concepts can be applied to a real-world problem. It is intentionally dependency-free so it can be compiled with a normal JDK and demonstrated easily in a college lab or viva.

## Features

- View all available courts and turfs
- Search facilities by sport
- Register customers
- Check date-wise available time slots
- Book a court/turf for a selected sport and duration
- Prevent overlapping bookings for the same facility
- Simulated Cash and UPI payments
- Reward points for customers
- View and cancel bookings
- Admin option to add a new court or turf
- Export bookings to `data/bookings.txt`
- Input validation and custom exception handling

## OOP Concepts Used

| OOP concept | Where it is used |
| --- | --- |
| Classes & Objects | `Booking`, `Customer`, `Court`, `Turf`, etc. |
| Encapsulation | Private fields with controlled getters/setters |
| Inheritance | `Customer`/`Admin` extend `User`; `Court`/`Turf` extend `SportsFacility` |
| Abstraction | Abstract classes `User` and `SportsFacility` |
| Polymorphism | `SportsFacility` references work with both `Court` and `Turf` objects |
| Interface | `PaymentMethod` implemented by `CashPayment` and `UpiPayment` |
| Method Overriding | Facility type/details, user roles, payment behavior |
| Association / Composition | A `Booking` contains a `Customer` and a `SportsFacility` |
| Collections | `List`, `Set`, `EnumSet` used for application data |
| Exception Handling | Custom booking exceptions + validation errors |
| Enums | Sports and booking states |
| File Handling | Booking report export using Java NIO |

## Project Structure

```text
oop-project/
├── README.md
├── .gitignore
└── src/
    └── com/
        └── sportbooking/
            ├── app/
            │   ├── Main.java
            │   └── ConsoleApplication.java
            ├── exception/
            │   ├── BookingException.java
            │   ├── NotFoundException.java
            │   └── SlotUnavailableException.java
            ├── model/
            │   ├── Admin.java
            │   ├── Booking.java
            │   ├── BookingStatus.java
            │   ├── Court.java
            │   ├── Customer.java
            │   ├── SportsFacility.java
            │   ├── SportType.java
            │   ├── Turf.java
            │   └── User.java
            ├── payment/
            │   ├── CashPayment.java
            │   ├── PaymentMethod.java
            │   └── UpiPayment.java
            ├── service/
            │   └── BookingService.java
            └── util/
                └── DataExporter.java
```

## Requirements

- **JDK 17 or newer** recommended
- No database or third-party library is required

Check Java installation:

```bash
java -version
javac -version
```

## How to Run

### Option 1: Windows Command Prompt / PowerShell

Clone the repository and enter the project folder:

```bash
git clone https://github.com/aniketchougule1902/oop-project.git
cd oop-project
```

Create the output folder and compile all Java files:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName)
```

Run the application:

```bash
java -cp out com.sportbooking.app.Main
```

### Option 2: Windows Git Bash / Linux / macOS

```bash
git clone https://github.com/aniketchougule1902/oop-project.git
cd oop-project
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out com.sportbooking.app.Main
```

### Option 3: IntelliJ IDEA / Eclipse / VS Code

1. Clone or download this repository.
2. Open the `oop-project` folder in your IDE.
3. Configure a JDK (17+ recommended).
4. Mark `src` as the source root if the IDE does not detect it automatically.
5. Open `src/com/sportbooking/app/Main.java`.
6. Run the `Main` class.

## Main Menu

```text
1. View all courts and turfs
2. Search facilities by sport
3. Register customer
4. Check available time slots
5. Book a court/turf
6. View all bookings
7. Cancel booking
8. Admin: Add court/turf
9. Export booking report
0. Exit
```

A demo customer and four sample facilities are automatically loaded when the program starts, so the project can be demonstrated immediately.

Demo customer:

```text
Customer ID: C1001
Phone: 9876543210
```

## Example Booking Flow

1. Choose **1** to see facility IDs.
2. Choose **4** to check available slots for a facility and date.
3. Choose **5** to create a booking.
4. Select the demo customer `C1001` or register a new customer.
5. Enter a facility ID such as `F201`.
6. Select a supported sport such as `FOOTBALL`.
7. Enter a future/current date, duration, and start hour.
8. Choose Cash or UPI payment.
9. The system creates a booking ID such as `B5001` and prevents conflicting bookings.

## Suggested Viva Points

- `SportsFacility` is abstract because every facility shares common data, but a Court and Turf have different details.
- Runtime polymorphism is visible when a `List<SportsFacility>` stores both `Court` and `Turf` objects and Java calls the overridden methods.
- `PaymentMethod` demonstrates an interface and allows payment types to be swapped without changing `BookingService`.
- `BookingService` separates business rules from the console UI, which improves maintainability.
- Encapsulation prevents invalid direct changes to important fields such as rates, phone numbers, and booking state.
- Slot overlap logic prevents double-booking of the same facility.

## Future Enhancements

Possible extensions include a Swing/JavaFX GUI, database persistence, login/authentication, online payment gateway integration, email/SMS confirmation, and dynamic pricing.

## Academic Note

This project is designed as an educational OOP mini project. Payment handling is simulated and no real payment is processed.
