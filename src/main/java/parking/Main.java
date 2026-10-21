package parking;

import parking.store.*;
import parking.gate.*;
import parking.space.*;
import parking.payment.*;
import parking.facade.*;
import parking.pricing.*;
import parking.spaces.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        DiskStore disk = new DiskStore(Path.of("parking-data"));
        GateService gate = new GateService(new GateRepository(disk), new PlateValidator());
        SpaceService space = new SpaceService(new SpaceRepository(disk), new SpacePolicy(), 3);
        PaymentService payment = new PaymentService(new PaymentRepository(disk), new PaymentPolicy());
        ChainRepository chains = new ChainRepository(disk);
        ParkingFacade facade = new ParkingFacade(gate, space, payment, chains);
        AdminFacade admin = new AdminFacade(gate, space, payment, chains, facade);
        EntryFacade entry = new EntryFacade(gate, space, chains, facade);

        OccupancySource occupancy = new FixedOccupancySource(85);
        PriceSelector priceSelector = new PriceSelector(occupancy);
        SpotSelector spotSelector = new SpotSelector();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nAssignment 4: 1 Park | 2 Fail B | 3 Fail C | 4 Inspect | 5 All | 6 Rollback | 7 Resume | 8 Demo restart | 9 Entry A+B");
            System.out.println("Assignment 5: 10 All prices | 11 Set price | 12 Auto price | 13 All spots | 14 Set spot | 15 Auto spot | 0 Exit");
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) break;
            try {
                switch (choice) {
                    case "1", "2", "3" -> {
                        System.out.print("ID: "); String id = sc.nextLine().trim();
                        System.out.print("Plate: "); String plate = sc.nextLine().trim();
                        System.out.print("Hours (1-24): "); int hours = Integer.parseInt(sc.nextLine());
                        System.out.println(facade.park(id, plate, hours, choice.equals("2"), choice.equals("3")));
                    }
                    case "4" -> { System.out.print("ID: "); System.out.println(admin.inspect(sc.nextLine().trim())); }
                    case "5" -> System.out.println(admin.all());
                    case "6" -> { System.out.print("ID: "); admin.manualCompensate(sc.nextLine().trim()); System.out.println("Compensated"); }
                    case "7" -> {
                        System.out.print("ID: "); String id = sc.nextLine().trim();
                        System.out.print("Hours: "); int hours = Integer.parseInt(sc.nextLine());
                        System.out.println(facade.continueChain(id, hours, false, false));
                    }
                    case "8" -> {
                        System.out.print("ID: "); String id = sc.nextLine().trim();
                        System.out.print("Plate: "); String plate = sc.nextLine().trim();
                        if (chains.get(id) != null) throw new IllegalArgumentException("Use a new ID");
                        gate.enter(id, plate);
                        chains.save(id, ChainRepository.Stage.PENDING_B);
                        System.out.println("Step A saved. Restart program and choose 7.");
                    }
                    case "9" -> {
                        System.out.print("ID: "); String id = sc.nextLine().trim();
                        System.out.print("Plate: "); String plate = sc.nextLine().trim();
                        System.out.println(entry.enterAndReserve(id, plate));
                    }
                    case "10" -> {
                        System.out.print("Hours: "); int hours = Integer.parseInt(sc.nextLine());
                        for (PriceStrategy strategy : List.of(new HourlyPrice(), new DayPassPrice(), new BusyHoursPrice(occupancy))) {
                            payment.setPriceStrategy(strategy);
                            System.out.println(payment.priceStrategyName() + ": " + payment.previewPrice(hours) + " KZT");
                        }
                    }
                    case "11" -> {
                        System.out.print("Price strategy (1 Hourly, 2 DayPass, 3 Busy): ");
                        PriceStrategy strategy = switch (sc.nextLine().trim()) {
                            case "1" -> new HourlyPrice();
                            case "2" -> new DayPassPrice();
                            case "3" -> new BusyHoursPrice(occupancy);
                            default -> throw new IllegalArgumentException("Unknown price strategy");
                        };
                        payment.setPriceStrategy(strategy);
                        System.out.println("Active: " + payment.priceStrategyName());
                    }
                    case "12" -> {
                        System.out.print("Hours: "); int hours = Integer.parseInt(sc.nextLine());
                        payment.setPriceStrategy(priceSelector.choose(hours));
                        System.out.println("Auto: " + payment.priceStrategyName() + ", " + payment.previewPrice(hours) + " KZT");
                    }
                    case "13" -> {
                        for (SpotStrategy strategy : List.of(new FirstFreeSpot(), new ClosestSpot(), new CoveredSpot())) {
                            space.setSpotStrategy(strategy);
                            System.out.println(space.spotStrategyName() + ": " + strategy.select(space.availableSpots()).id());
                        }
                    }
                    case "14" -> {
                        System.out.print("Spot strategy (1 First, 2 Closest, 3 Covered): ");
                        SpotStrategy strategy = switch (sc.nextLine().trim()) {
                            case "1" -> new FirstFreeSpot();
                            case "2" -> new ClosestSpot();
                            case "3" -> new CoveredSpot();
                            default -> throw new IllegalArgumentException("Unknown spot strategy");
                        };
                        space.setSpotStrategy(strategy);
                        System.out.println("Active: " + space.spotStrategyName());
                    }
                    case "15" -> {
                        System.out.print("Covered place? y/n: "); boolean covered = sc.nextLine().equalsIgnoreCase("y");
                        System.out.print("Limited mobility? y/n: "); boolean limited = sc.nextLine().equalsIgnoreCase("y");
                        space.setSpotStrategy(spotSelector.choose(covered, limited));
                        System.out.println("Auto: " + space.spotStrategyName());
                    }
                    default -> System.out.println("Unknown option");
                }
            } catch (RuntimeException ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
        }
    }
}
