package cafe.mercury.anticheat.event;

import cafe.mercury.anticheat.util.location.CustomLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class VelocityEvent {

    private final double velocityH;
    private final double velocityV;

    private final CustomLocation oldestLocation;
    private final CustomLocation previousLocation;
    private final CustomLocation currentLocation;
}
