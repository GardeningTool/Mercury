package cafe.mercury.anticheat.check.impl.reach;

import cafe.mercury.anticheat.check.annotation.CheckData;
import cafe.mercury.anticheat.check.type.PacketCheck;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import cafe.mercury.anticheat.tracker.impl.MovementTracker;
import cafe.mercury.anticheat.tracker.impl.PingTracker;
import cafe.mercury.anticheat.violation.handler.ViolationHandler;

@CheckData(
        name = "Reach",
        description = "Checks for excessive distance between attacks",
        type = "A"
)
public class ReachA extends PacketCheck {

    private final MovementTracker movementTracker = data.getMovementTracker();
    private final PingTracker pingTracker = data.getPingTracker();

    private Integer targetId;

    public ReachA(PlayerData playerData) {
        super(playerData, new ViolationHandler(10));
    }

    @Override
    public void handle(WrappedPacket paramPacket) {
    }
}
