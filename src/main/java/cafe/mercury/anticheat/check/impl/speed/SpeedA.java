package cafe.mercury.anticheat.check.impl.speed;

import cafe.mercury.anticheat.check.annotation.CheckData;
import cafe.mercury.anticheat.check.type.PositionUpdateCheck;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.event.PositionUpdateEvent;
import cafe.mercury.anticheat.tracker.impl.CollisionTracker;
import cafe.mercury.anticheat.tracker.impl.MovementTracker;
import cafe.mercury.anticheat.util.collision.CollisionResult;
import cafe.mercury.anticheat.violation.Violation;
import cafe.mercury.anticheat.violation.handler.ViolationHandler;
import org.bukkit.Bukkit;

@CheckData(
        name = "Speed",
        type = "A",
        description = "Checks if friction is being applied properly"
)
//TODO: Fix falses under blocks with ice
public class SpeedA extends PositionUpdateCheck {

    private final CollisionTracker collisionTracker = data.getCollisionTracker();
    private final MovementTracker movementTracker = data.getMovementTracker();

    private double lastOffsetH;
    private double buffer;

    public SpeedA(PlayerData playerData) {
        super(playerData, new ViolationHandler(10));
    }

    @Override
    public void handle(PositionUpdateEvent event) {
        double offsetH = event.getOffsetH();
        double aiMoveSpeed = movementTracker.getAiMoveSpeed();

        /*
         * We need to find the ratio at which the player is moving
         * compared to their expected movement speed following Minecraft's
         * logic and friction.
         */
        double ratio = (offsetH - lastOffsetH) / aiMoveSpeed;

        /*
         * If they're moving faster than Minecraft says they should be,
         * and we know they aren't teleporting, we can flag them for Speed
         */
        if (ratio > 1 && !movementTracker.isTeleporting()) {
            if (++buffer > 5) {
                fail(new Violation("ratio", ratio));
            }
        } else {
            buffer = Math.max(buffer - 0.5, 0);
        }

        this.lastOffsetH = offsetH * collisionTracker.getPreviousCollisions().getFrictionFactor();
    }
}
