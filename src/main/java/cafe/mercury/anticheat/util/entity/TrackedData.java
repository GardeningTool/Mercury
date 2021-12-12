package cafe.mercury.anticheat.util.entity;

import cafe.mercury.anticheat.util.location.CustomLocation;

import java.util.HashMap;
import java.util.Map;

public class TrackedData {

    private final Map<Short, CustomLocation> locationMap = new HashMap<>();

    public CustomLocation getAtTick(short tick) {
        return locationMap.get(tick);
    }

    public void addTarget(short tick, CustomLocation customLocation) {
        locationMap.put(tick, customLocation);
    }

    public void removeTargetPostTransaction(short transaction) {
        locationMap.remove(transaction);
    }
}
