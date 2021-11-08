package cafe.mercury.anticheat.check.impl.badpackets;

import cafe.mercury.anticheat.check.annotation.CheckData;
import cafe.mercury.anticheat.check.type.PositionUpdateCheck;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.event.PositionUpdateEvent;
import cafe.mercury.anticheat.event.RotationEvent;
import cafe.mercury.anticheat.violation.Violation;
import cafe.mercury.anticheat.violation.handler.ViolationHandler;

@CheckData(
        name = "BadPackets",
        type = "A",
        description = "Checks for illegal pitch ranges"
)
public class BadPacketsA extends PositionUpdateCheck {

    public BadPacketsA(PlayerData playerData) {
        super(playerData, new ViolationHandler(1));
    }

    @Override
    public void handle(PositionUpdateEvent event) {
        if (Math.abs(event.getTo().getPitch()) > 90) {
            fail(new Violation("pitch", event.getTo().getPitch()));
        }
    }
}
