package parking.spaces;
public class SpotSelector {
    public SpotStrategy choose(boolean wantsCovered, boolean limitedMobility) {
        if (wantsCovered) return new CoveredSpot();
        if (limitedMobility) return new ClosestSpot();
        return new FirstFreeSpot();
    }
}
