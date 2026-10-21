# Assignment 5 — Strategy Pattern added to Assignment 4

This is the combined project. All original Gate / Space / Payment / Facade / ChainRepository / DiskStore code remains.

## What changed

* `PaymentService` now delegates price calculation to `PriceContext` and `PriceStrategy`.
* `SpaceService` now delegates spot selection to `SpotContext` and `SpotStrategy`.
* `Main` retains menu options 1–9 and adds options 10–15 for Strategy demos.
* `EntryFacade` is included for A+B without C.
* Pricing: `HourlyPrice`, `DayPassPrice`, `BusyHoursPrice` (reads `OccupancySource`).
* Space: `FirstFreeSpot`, `ClosestSpot`, `CoveredSpot`.
* `PriceSelector` and `SpotSelector` choose algorithms automatically.
* `StrategyIntegrationTest` verifies that selected strategies affect the saved parking and payment records.

## Run

`mvn test` and `mvn exec:java`.

Run A4 workflow using menu 1–9. Select A5 pricing with 11 or 12, then park with option 1 and inspect with 4. Select A5 spot selection with 14 or 15, then park and inspect. Tests create temporary stores.

The rate data source is simulated using `FixedOccupancySource(85)`. The occupied percentage is deliberately a fixed demo value, not actual live parking occupancy.

## Important academic requirement

The assignment also asks for two real Git commits demonstrating the before/after refactor using the same tests. These must be created and pushed through your own Git history; the archive cannot establish that history on your behalf. Update README to add commit links.
