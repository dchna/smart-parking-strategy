# Smart Parking Garage

Assignment 5 — Strategy Pattern

## About Project

This is my Smart Parking Garage project in Java. I used Facade Pattern in Assignment 4. In Assignment 5 I added Strategy Pattern.

Now my program can use different ways to calculate parking price and choose parking space.

## Strategy Families

I have two strategy families.

**1. Pricing Strategy**
- HourlyPrice — normal price, 200 KZT per hour
- DayPassPrice — price with daily limit
- BusyHoursPrice — adds extra price when parking is busy

**2. Parking Space Strategy**
- FirstFreeSpot — selects first free place
- ClosestSpot — selects closest place
- CoveredSpot — prefers covered place

## Classes

| Class | Responsibility |
|---|---|
| PriceStrategy | Interface for calculating price |
| HourlyPrice | Calculates hourly price |
| DayPassPrice | Calculates price with daily limit |
| BusyHoursPrice | Calculates price using parking occupancy |
| PriceContext | Uses selected pricing strategy |
| PriceSelector | Automatically selects pricing strategy |
| SpotStrategy | Interface for selecting parking place |
| FirstFreeSpot | Selects first available place |
| ClosestSpot | Selects nearest available place |
| CoveredSpot | Prefers covered parking place |
| SpotContext | Uses selected space strategy |
| SpotSelector | Automatically selects space strategy |
| OccupancySource | Provides parking occupancy data |
| FixedOccupancySource | Returns simulated occupancy data |
| ParkingFacade | Controls parking process |
| AdminFacade | Shows parking information |
| EntryFacade | Handles entry with space reservation |
| DiskStore | Saves data in files |

## Strategy vs State vs Template Method

I used Strategy because I need to change algorithms during program running.

Strategy allows me to choose different pricing and parking space algorithms. I can change them using setStrategy().

State Pattern changes behavior depending on object state. Template Method uses inheritance with fixed steps.

For my project Strategy is better because I want to choose algorithms without changing main logic.

## Testing

I used JUnit 5 for testing.

Tests check different strategies, selectors and switching strategies.

I also used ParameterizedTest to check different pricing strategies.

## Refactoring History

First I used if/else for calculating parking prices. Then I changed it to Strategy Pattern.

**Before Refactoring:**

https://github.com/dchna/smart-parking-strategy/commit/9bc79e5

**After Refactoring:**

https://github.com/dchna/smart-parking-strategy/commit/2248ae3

The same tests passed before and after refactoring.

## How to Run

Open the project in IntelliJ IDEA.

Run `Main.java` to open the console menu.

Run tests using Maven Lifecycle → test.

## Conclusion

In this assignment I learned how Strategy Pattern works. Now my parking system can change algorithms during running without changing the whole program.