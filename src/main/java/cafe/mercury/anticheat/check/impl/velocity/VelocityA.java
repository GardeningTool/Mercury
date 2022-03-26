package cafe.mercury.anticheat.check.impl.velocity;

import cafe.mercury.anticheat.check.annotation.CheckData;
import cafe.mercury.anticheat.check.type.VelocityCheck;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.event.VelocityEvent;
import cafe.mercury.anticheat.tracker.impl.CollisionTracker;
import cafe.mercury.anticheat.tracker.impl.MovementTracker;
import cafe.mercury.anticheat.tracker.impl.PotionTracker;
import cafe.mercury.anticheat.util.collision.CollisionResult;
import cafe.mercury.anticheat.util.location.CustomLocation;
import cafe.mercury.anticheat.violation.Violation;
import cafe.mercury.anticheat.violation.handler.ViolationHandler;

@CheckData(
        name = "Velocity",
        type = "A",
        description = "Checks for vertical velocity modifications"
)
public class VelocityA extends VelocityCheck {

    private static final float JUMP_HEIGHT = .42F;

    private final CollisionTracker collisionTracker = data.getCollisionTracker();
    private final MovementTracker movementTracker = data.getMovementTracker();
    private final PotionTracker potionTracker = data.getPotionTracker();

    private int buffer;

    public VelocityA(PlayerData playerData) {
        super(playerData, new ViolationHandler(10));
    }

    @Override
    public void handle(VelocityEvent event) {
        CollisionResult collisions = collisionTracker.getCollisions();

        if (collisions.isInCobweb() || collisions.isInLiquid() || collisions.isCollidedVertically() || movementTracker.isTeleporting()) {
            return;
        }

        CustomLocation previousLocation = event.getPreviousLocation();
        CustomLocation currentLocation = event.getCurrentLocation();

        double previousY = previousLocation.getY();
        double currentY = currentLocation.getY();

        double velocityV = event.getVelocityV();
        double offsetV = Math.abs(currentY - previousY);

        if (Math.abs(offsetV - JUMP_HEIGHT) > 0.05 && offsetV < velocityV && offsetV > 0.1) {
            if (++buffer > 3) {
                fail(new Violation("ratio", offsetV / velocityV));
            }
        } else {
            buffer = Math.max(buffer - 1, 0);
        }
    }
}
