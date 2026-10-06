# Sports Court & Turf Booking System

A complete **Java 17 Object Oriented Programming mini project** for managing sports court and turf bookings. It is dependency-free, menu-driven, persists data between runs, includes customer/admin flows, and is designed to be easy to demonstrate in a college practical or viva.

## Features

### Customer
- Register customer accounts
- STANDARD / SILVER / GOLD membership levels
- Browse and search courts/turfs by sport and location
- Check date-wise available time slots
- Book a facility for 1–4 hours
- Automatic overlap/double-booking prevention
- Simulated Cash, UPI, and Card payments
- Membership discounts and reward points
- View personal bookings
- Cancel future bookings with simulated refund status
- Export a text payment receipt

### Admin
- PIN-protected admin menu (`1234` for this academic demo)
- Dashboard with facilities, customers, booking counts, and revenue
- Add Court or Turf facilities
- Configure supported sports and operating hours
- Set facility status: `ACTIVE`, `MAINTENANCE`, `INACTIVE`
- View all customers and bookings
- Export bookings as CSV

### Technical
- Persistent local data using Java object serialization
- CSV report generation
- Text receipt generation
- Custom exceptions and input validation
- Dependency-free self-test
- Build/run scripts for Windows and Linux/macOS
- Mermaid class diagram in `docs/CLASS_DIAGRAM.md`

## OOP Concepts Used

| Concept | Implementation |
|---|---|
| Classes & Objects | `Booking`, `Customer`, `Court`, `Turf`, `PaymentTransaction` |
| Encapsulation | Private fields + validated methods/getters/setters |
| Inheritance | `Customer`/`Admin` extend `User`; `Court`/`Turf` extend `SportsFacility` |
| Abstraction | Abstract `User` and `SportsFacility` classes |
| Polymorphism | Court/Turf runtime behavior through `SportsFacility`; payment implementations through `PaymentMethod` |
| Interface | `PaymentMethod` |
| Overriding | Role, facility type/details, surcharge calculation |
| Overloading | `searchFacilities(sport)` and `searchFacilities(sport, location)` |
| Association/Composition | `Booking` connects Customer + Facility and contains PaymentTransaction |
| Exceptions | Custom booking/facility/slot exceptions |
| Collections | `List`, `Set`, `EnumSet` |
| Enums | Sports, membership, booking/payment/facility statuses |
| File Handling | Persistent serialized state, CSV reports, text receipts |

## Requirements

- **JDK 17 or newer**
- No Maven, Gradle, database, or third-party library is required.

Check Java:

```bash
java -version
javac -version
```

## Run on Windows

### Easiest method

```bat
git clone https://github.com/aniketchougule1902/oop-project.git
cd oop-project
run.bat
```

`run.bat` automatically builds the project the first time.

### Build only

```bat
build.bat
```

Generated runnable JAR:

```text
build\sports-booking.jar
```

Run the JAR directly:

```bat
java -jar build\sports-booking.jar
```

### Run self-test

```bat
test.bat
```

A successful test ends with:

```text
ALL SELF-TESTS PASSED
```

## Run on Git Bash / Linux / macOS

```bash
git clone https://github.com/aniketchougule1902/oop-project.git
cd oop-project
chmod +x build.sh run.sh test.sh
./run.sh
```

Test:

```bash
./test.sh
```

## Run manually with `javac`

### Linux/macOS/Git Bash

```bash
mkdir -p out
javac --release 17 -d out $(find src -name "*.java")
java -cp out com.sportbooking.app.Main
```

### PowerShell

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac --release 17 -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName)
java -cp out com.sportbooking.app.Main
```

## Demo Credentials / Data

The first run automatically creates sample facilities and customers.

```text
Demo Customer ID : C1001
Admin PIN         : 1234
```

Payments in this project are **simulated only**. Never enter a real card number or real payment credential; use dummy values for demonstration.

## Typical Demo Flow

1. Run the application.
2. Choose **Browse all courts and turfs**.
3. Open **Customer portal**.
4. Use customer `C1001` or register a new one.
5. Check available slots for a future/current date.
6. Book a facility and choose a simulated payment method.
7. Observe booking ID, payment transaction, reward points, and generated receipt.
8. Try booking an overlapping slot to demonstrate conflict prevention.
9. Enter **Admin portal** with PIN `1234` to show the dashboard and CSV export.

## Persistence and Generated Files

Runtime data is stored under `data/`:

```text
data/
├── booking-system.ser
├── receipts/
│   └── receipt-Bxxxx.txt
└── reports/
    └── bookings.csv
```

Delete the `data/` folder only when you intentionally want to reset local demo data.

## Project Structure

```text
.
├── README.md
├── PROJECT_REPORT.md
├── build.bat / build.sh
├── run.bat / run.sh
├── test.bat / test.sh
├── docs/
│   └── CLASS_DIAGRAM.md
├── src/com/sportbooking/
│   ├── app/
│   ├── exception/
│   ├── model/
│   ├── payment/
│   ├── service/
│   └── util/
└── test/com/sportbooking/
    └── ProjectSelfTest.java
```

## Viva Short Notes

**Why is `SportsFacility` abstract?** Courts and turfs share identity, location, pricing, status, sports, and operating hours, but their specific details and extra charges differ.

**Where is runtime polymorphism?** `BookingService` stores both `Court` and `Turf` as `SportsFacility`. Java calls the overridden surcharge/details methods based on the actual runtime object.

**Why use `PaymentMethod` interface?** Booking logic depends on a payment contract rather than a specific Cash/UPI/Card class, so new payment types can be added without rewriting booking logic.

**How is double booking prevented?** Before confirming a booking, the service checks all non-cancelled bookings for the same facility/date and rejects overlapping time ranges.

**Where is file handling used?** Application state is serialized to disk, booking reports are exported as CSV, and individual receipts are generated as text files.

## Future Enhancements

- JavaFX or Swing GUI
- MySQL/PostgreSQL persistence using JDBC
- User authentication with hashed passwords
- Online payment gateway integration
- Email/SMS notifications
- QR-based booking check-in
- Dynamic pricing and coupons
- REST API / web frontend

## Academic Note

This repository is an educational mini project. Payment processing is simulated and the admin PIN is intentionally simple for demonstration purposes.
