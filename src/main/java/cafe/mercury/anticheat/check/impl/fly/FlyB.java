package cafe.mercury.anticheat.check.impl.fly;

import cafe.mercury.anticheat.check.annotation.CheckData;
import cafe.mercury.anticheat.check.type.PositionUpdateCheck;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.event.PositionUpdateEvent;
import cafe.mercury.anticheat.tracker.impl.CollisionTracker;
import cafe.mercury.anticheat.tracker.impl.MovementTracker;
import cafe.mercury.anticheat.util.collision.CollisionResult;
import cafe.mercury.anticheat.violation.Violation;
import cafe.mercury.anticheat.violation.handler.ViolationHandler;

@CheckData(
        name = "Fly",
        type = "B",
        description = "Checks for vertical movement not following Minecraft's logic"
)
public class FlyB extends PositionUpdateCheck {

    private static final double GRAVITY = 0.98D; //we're just going to use 0.98 instead of the full value

    private final CollisionTracker collisionTracker = data.getCollisionTracker();
    private final MovementTracker movementTracker = data.getMovementTracker();

    private double buffer;
    private double lastOffsetV;

    public FlyB(PlayerData playerData) {
        super(playerData, new ViolationHandler(10));
    }

    @Override
    public void handle(PositionUpdateEvent event) {
        CollisionResult collisions = collisionTracker.getCollisions();
        double offsetV = event.getTo().getY() - event.getFrom().getY();

        if (!collisions.isOnGround() && !collisions.isInCobweb() && !collisions.isInLiquid()) {
            double calculatedOffsetV = (lastOffsetV - 0.08) * GRAVITY;

            if (Math.abs(offsetV - calculatedOffsetV) > 0.01 && !movementTracker.isTeleporting()) {
                if (++buffer > 5) {
                    fail(new Violation("offsetV", offsetV, "calculatedOffsetV", calculatedOffsetV));
                }
            } else {
                buffer = Math.max(buffer - 0.75, 0);
            }
        }

        this.lastOffsetV = offsetV;
    }
}
