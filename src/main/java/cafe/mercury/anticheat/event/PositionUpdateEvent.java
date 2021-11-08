package cafe.mercury.anticheat.event;

import cafe.mercury.anticheat.util.location.CustomLocation;
import cafe.mercury.anticheat.util.math.MathUtil;
import lombok.Getter;

@Getter
public class PositionUpdateEvent {

    private final CustomLocation to;
    private final CustomLocation from;
    private final double offsetH;
    private final double offsetV;

    public PositionUpdateEvent(CustomLocation to, CustomLocation from) {
        this.to = to;
        this.from = from;
        this.offsetH = MathUtil.hypot(Math.abs(to.getX() - from.getX()), Math.abs(to.getZ() - from.getZ()));
        this.offsetV = Math.abs(to.getY() - from.getY());
    }
}
