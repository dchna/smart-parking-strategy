package parking.gate;
public class PlateValidator {
    public String validate(String plate) {
        if(plate==null||!plate.trim().toUpperCase().matches("[A-Z0-9-]{3,12}"))throw new IllegalArgumentException("Plate must contain 3-12 letters, digits or hyphens");
        return plate.trim().toUpperCase();
    }
}
