package cafe.mercury.anticheat.check.type;

import cafe.mercury.anticheat.check.Check;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.event.PositionUpdateEvent;
import cafe.mercury.anticheat.violation.handler.ViolationHandler;

public abstract class PositionUpdateCheck extends Check<PositionUpdateEvent> {

    public PositionUpdateCheck(PlayerData playerData, ViolationHandler violationHandler) {
        super(playerData, violationHandler);
    }

    @Override
    public abstract void handle(PositionUpdateEvent event);

}
