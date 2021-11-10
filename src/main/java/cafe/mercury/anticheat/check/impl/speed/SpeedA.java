package cafe.mercury.anticheat.check.impl.speed;

import cafe.mercury.anticheat.check.annotation.CheckData;
import cafe.mercury.anticheat.check.type.PositionUpdateCheck;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.event.PositionUpdateEvent;
import cafe.mercury.anticheat.tracker.impl.CollisionTracker;
import cafe.mercury.anticheat.tracker.impl.MovementTracker;
import cafe.mercury.anticheat.violation.Violation;
import cafe.mercury.anticheat.violation.handler.ViolationHandler;

@CheckData(
        name = "Speed",
        type = "A",
        description = "Checks if friction is being applied properly"
)
public class SpeedA extends PositionUpdateCheck {

    private final CollisionTracker collisionTracker = data.getCollisionTracker();
    private final MovementTracker movementTracker = data.getMovementTracker();

    private double lastOffsetH;

    private int vl;

    public SpeedA(PlayerData playerData) {
        super(playerData, new ViolationHandler(10));
    }

    @Override
    public void handle(PositionUpdateEvent event) {
        double offsetH = event.getOffsetH();
        double aiMoveSpeed = movementTracker.getAiMoveSpeed();

        if (!collisionTracker.getCollisions().isOnGround() && event.getOffsetV() > 0.2) {
            aiMoveSpeed += 0.2;
        }

        double ratio = (offsetH - lastOffsetH) / aiMoveSpeed;

        if (ratio > 1 && !movementTracker.isTeleporting()) {
            fail(new Violation("ratio", ratio));
        }

        this.lastOffsetH = offsetH * collisionTracker.getCollisions().getFrictionFactor();
    }
}
