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

    private int buffer;

    public VelocityA(PlayerData playerData) {
        super(playerData, new ViolationHandler(10));
    }

    @Override
    public void handle(VelocityEvent event) {
        CollisionResult collisions = collisionTracker.getCollisions();

        /*
        Certain collisions, such as the ones accounted for below, interfere with
        the player's motion and shouldn't be checked
         */

        if (collisions.isInCobweb() || collisions.isInLiquid() || collisions.isUnderBlock()) {
            return;
        }

        CustomLocation previousLocation = event.getPreviousLocation();
        CustomLocation currentLocation = event.getCurrentLocation();

        double velocityV = event.getVelocityV();
        double offsetV = Math.abs(currentLocation.getY() - previousLocation.getY());

        if (Math.abs(offsetV - JUMP_HEIGHT) > 0.05 && offsetV < velocityV && velocityV > 0.1) {
            double ratio = offsetV / velocityV;

            /*
            On some occasions, I've found that the calculated ratio can sometimes be
            just under 1, so in order to compensate for that, we'll check if the ratio
            is just under that value
             */
            if (ratio < 0.99995 && !movementTracker.isTeleporting() && ++buffer > 3) {
                fail(new Violation("ratio", offsetV / velocityV));
            }
        } else {
            buffer = Math.max(buffer - 1, 0);
        }
    }
}
