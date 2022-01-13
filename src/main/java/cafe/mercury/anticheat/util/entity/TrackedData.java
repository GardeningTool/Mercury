package cafe.mercury.anticheat.util.entity;

import cafe.mercury.anticheat.util.location.CustomLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class TrackedData {

    private final Queue<TrackedLocation> trackedLocations = new LinkedList<>();

    public CustomLocation getAtTick(short tick) {
        List<TrackedLocation> locations = trackedLocations.stream()
                .filter(location -> location.getTick() == tick)
                .collect(Collectors.toList());

        return locations.isEmpty() ? null : locations.get(0).getLocation();
    }

    public void addTarget(short tick, CustomLocation customLocation, boolean teleport) {
        TrackedLocation lastLocation = trackedLocations.peek();

        double posX = customLocation.getX();
        double posY = customLocation.getY();
        double posZ = customLocation.getZ();

        float yaw = customLocation.getYaw();
        float pitch = customLocation.getPitch();

        if (lastLocation != null && !teleport) {
            CustomLocation lastPosition = lastLocation.getLocation();

            posX += lastPosition.getX();
            posY += lastPosition.getY();
            posZ += lastPosition.getZ();

            yaw += lastPosition.getYaw();
            pitch += lastPosition.getPitch();
        }

        CustomLocation location = new CustomLocation(posX, posY, posZ, yaw, pitch, false);

        trackedLocations.add(new TrackedLocation(tick, location));
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
