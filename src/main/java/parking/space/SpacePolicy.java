package parking.space;
public class SpacePolicy {
    public String chooseSpot(int occupied,int capacity) {
        if(occupied>=capacity)throw new IllegalStateException("Parking is full");
        return "P"+(occupied+1);
    }
}
