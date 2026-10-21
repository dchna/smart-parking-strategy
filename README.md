# Smart Parking Garage — Assignment 4 (Facade Pattern)

A simple Java console application for parking entry, space reservation and payment. This is an individual-study starter project. Adapt it and understand each decision before submitting.

## Run

Requirements: Java 17+ and Maven.

```bash
mvn clean verify
mvn exec:java
```

In IntelliJ IDEA: open the `pom.xml` as a Maven project and run `parking.Main`.

For a clean demonstration, stop the application and delete the `parking-data` directory. Data is stored in `.properties` files and remains after restarting the application.

## Menu

1 = successful parking, 2 = B failure, 3 = C failure, 4 = inspect by ID, 5 = inspect all, 6 = manual compensation, 7 = resume an unfinished chain, 0 = exit. Use a NEW ID for each attempt. Capacity is 3 spaces. Parking costs 200 KZT/hour.

## Three independent subsystems

- A / Gate: `GateEntry` (state), `PlateValidator` (business rules), `GateRepository` (persistence), `GateService` (operations).
- B / Space: `ParkingReservation` (state), `SpacePolicy` (business rules), `SpaceRepository` (persistence), `SpaceService` (operations).
- C / Payment: `Payment` (state), `PaymentPolicy` (business rules), `PaymentRepository` (persistence), `PaymentService` (operations).

Each subsystem can be used directly via its service, without calling either facade. The test `directSubsystems` demonstrates this.

## Facades and persisted workflow

`ParkingFacade` runs A → B → C. `AdminFacade` inspects stored state and allows manual compensation. `ChainRepository` persists progress as PENDING_B, PENDING_C, COMPLETED or COMPENSATED.

B failure after A: cancel the gate entry. C failure after A and B: release the space, THEN cancel the gate entry. Failure at the first step (invalid plate/hours) is rejected before committing anything. The demo menu can trigger both failures.

Persistence is per-entity rather than one global database transaction. The persisted chain status is updated after each step. The restart tests demonstrate resumption after A and after B. For a production system, stronger crash consistency, retries and idempotency would be needed between each entity write and its chain-status write.

## Principle of Least Knowledge — before / after

Before Facade, a client coordinates every subsystem:

```java
GateEntry entry = gate.enter(id, plate);
ParkingReservation reservation = space.reserve(id);
Payment receipt = payment.charge(id, hours, false);
```

The client must also catch errors, reverse reservations and cancel gate access. After Facade, the client only calls:

```java
String result = facade.park(id, plate, hours, false, false);
```

## Facade vs Adapter (reflection)

The problem is coordination, not incompatible interfaces. Gate, Space and Payment already expose usable operations. The client should not know which subsystem must run first or how to undo earlier work when later work fails. Facade gives the client a single parking operation while leaving each subsystem independently usable. Adapter would instead translate one interface into another; it would not automatically orchestrate the whole chain. `AdminFacade` provides a second, narrower view for inspection and manual compensation.

## Tests and F.I.R.S.T.

`mvn clean verify` runs JUnit 5 tests and produces JaCoCo HTML at `target/site/jacoco/index.html`. JaCoCo checks a minimum **80% aggregate line coverage** (the assignment requires at least 80% for subsystems and facades; verify the per-package numbers in the report too). Tests use a separate temporary disk directory for every test (Fast, Independent, Repeatable), JUnit assertions (Self-validating), and the custom helper `parkingShouldBe(...)` (domain-specific test language). Commit tests together with each code change (Timely): Git history must be made by the student.

## Files for submission

Source code, tests, this README, `docs/uml.puml` and an exported UML PNG, coverage report and GitHub repository link. The instructor may require live changes during defense.
