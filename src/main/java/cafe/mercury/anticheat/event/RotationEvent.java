package cafe.mercury.anticheat.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class RotationEvent {

    private final float yaw;
    private final float pitch;
    private final float deltaYaw;
    private final float deltaPitch;
    private final float previousDeltaYaw;
    private final float previousDeltaPitch;
    private final float sensitivity;
}
