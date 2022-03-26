package cafe.mercury.anticheat.check.type;

import cafe.mercury.anticheat.check.Check;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.event.VelocityEvent;
import cafe.mercury.anticheat.util.location.CustomLocation;
import cafe.mercury.anticheat.violation.handler.ViolationHandler;

public abstract class VelocityCheck extends Check<VelocityEvent> {

    public VelocityCheck(PlayerData playerData, ViolationHandler violationHandler) {
        super(playerData, violationHandler);
    }

    @Override
    public abstract void handle(VelocityEvent event);

}
