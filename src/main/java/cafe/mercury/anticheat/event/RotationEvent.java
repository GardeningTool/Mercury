package cafe.mercury.anticheat.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class RotationEvent {

    private final float yaw;
    private final float pitch;
    private final float deltaYaw;
    private final float deltaPitch;
    private final float sensitivity;

    public RotationEvent(float yaw, float pitch, float deltaYaw, float deltaPitch, float sensitivity) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.deltaYaw = deltaYaw;
        this.deltaPitch = deltaPitch;
        this.sensitivity = sensitivity;
    }
}
