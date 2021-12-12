package cafe.mercury.anticheat.util.entity;

import cafe.mercury.anticheat.util.location.CustomLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

public class TrackedData {

    private final Queue<TrackedLocation> trackedLocations = new LinkedList<>();

    public CustomLocation getAtTick(short tick) {
        return trackedLocations
                .stream()
                .filter(location -> location.getTick() == tick)
                .map(TrackedLocation::getLocation)
                .findFirst()
                .orElse(Objects.requireNonNull(trackedLocations.peek()).getLocation());
    }

    public void addTarget(short tick, CustomLocation customLocation) {
        trackedLocations.add(new TrackedLocation(tick, customLocation));

        if (trackedLocations.size() > 20) {
            trackedLocations.poll();
        }
    }

    public void removeTargetPostTransaction(short transaction) {
        trackedLocations.removeIf(location -> location.getTick() == transaction);
    }

    @Getter @AllArgsConstructor
    private static class TrackedLocation {

        private final short tick;
        private final CustomLocation location;

    }
}
