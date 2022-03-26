package cafe.mercury.anticheat.check.type;

import cafe.mercury.anticheat.check.Check;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.event.RotationEvent;
import cafe.mercury.anticheat.violation.handler.ViolationHandler;

public abstract class RotationCheck extends Check<RotationEvent> {

    public RotationCheck(PlayerData playerData, ViolationHandler violationHandler) {
        super(playerData, violationHandler);
    }
}
