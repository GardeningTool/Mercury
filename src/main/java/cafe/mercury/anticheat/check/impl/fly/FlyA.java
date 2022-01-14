package cafe.mercury.anticheat.check.impl.fly;

import cafe.mercury.anticheat.check.annotation.CheckData;
import cafe.mercury.anticheat.check.type.PositionUpdateCheck;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.event.PositionUpdateEvent;
import cafe.mercury.anticheat.tracker.impl.CollisionTracker;
import cafe.mercury.anticheat.tracker.impl.MovementTracker;
import cafe.mercury.anticheat.tracker.impl.PotionTracker;
import cafe.mercury.anticheat.util.collision.CollisionResult;
import cafe.mercury.anticheat.violation.Violation;
import cafe.mercury.anticheat.violation.handler.ViolationHandler;
import org.bukkit.potion.PotionEffectType;

@CheckData(
        name = "Fly",
        type = "A",
        description = "Checks for illegal vertical motion"
)
public class FlyA extends PositionUpdateCheck {

    private static final float DEFAULT_JUMP_HEIGHT = .42F; //has to be a float to be as exact as possible
    private static final float JUMP_BLOCK_INCREASE = 0.1F; //not the best fix for collisions with blocks, but it works
    private static final float COLLIDED_OFFSET_V = .2001F; //when you're collided with a block, the maximum is different

    private final CollisionTracker collisionTracker = data.getCollisionTracker();
    private final MovementTracker movementTracker = data.getMovementTracker();
    private final PotionTracker potionTracker = data.getPotionTracker();

    private double buffer;

    public FlyA(PlayerData playerData) {
        super(playerData, new ViolationHandler(10));
    }

    //TODO: Add slime detection
    @Override
    public void handle(PositionUpdateEvent event) {
        double offsetV = event.getOffsetV();
        /*
         * Once we have the player's vertical movement offset, we can begin to implement
         * a basic limit system to ensure that they're not jumping heigher than
         * a legitimate player is capable of doing
         */
        double maxOffsetV = DEFAULT_JUMP_HEIGHT; //set their maximum to the vanilla jump height before handling other conditions

        if (potionTracker.getAmplifier(PotionEffectType.JUMP) > 0) {
            /*
             * For reference, this math can be found in the jump() method of
             * EntityLivingBase in MCP 1.8.8. Horse jumping uses the same math
             */
            maxOffsetV += (potionTracker.getAmplifier(PotionEffectType.JUMP) + 1) * 0.1F;
        }

        boolean groundStateChange = false;
        if (collisionTracker.getCollisions().isMathematicallyOnGround()) {
            /*
            * Whether the player was mathematically on the ground in their last,
            * movement packet and was not on ground in this one.
            */
            groundStateChange = collisionTracker.getPreviousCollisions().isMathematicallyOnGround();
        }

        if (movementTracker.getVelocityV() > 0) {
            /*
             * When you're taking vertical velocity, you're obviously going to move
             * upwards. So, in order to compensate for that in this check, all we're
             * going to do is add their vertical velocity to their maximum velocity.
             */
            maxOffsetV += movementTracker.getVelocityV();
        }

        if ((collisionTracker.getCollisions().isCollidedVertically() && !collisionTracker.getCollisions().isOnGround()) || groundStateChange) {
            if (collisionTracker.getCollisions().isCollidedHorizontally()) {
                /*
                 * We want to handle collisions under certain situations where the
                 * collision tracker states that the player is not collided horizontally
                 * Doing so prevents against false flags while running up stairs and jumping.
                 */
                maxOffsetV += JUMP_BLOCK_INCREASE;
            } else {
                /*
                 * When the player is collided with a block above their head tries to jump,
                 * they won't be able to jump as high. Additionally, a number of other factors,
                 * such as potion effects, will not be considered.
                 */
                maxOffsetV = COLLIDED_OFFSET_V;
            }
        }

        //We want to only check if the jump size is too low when they aren't collided vertically
        boolean smallHop = offsetV > 0.03 && offsetV < DEFAULT_JUMP_HEIGHT &&
                Math.abs(offsetV - maxOffsetV) > 0.05 && maxOffsetV < 0.5;

        if (offsetV > maxOffsetV || (smallHop && groundStateChange)) {
            if (++buffer > 3) {
                fail(new Violation("offsetV", offsetV, "maxOffsetV", maxOffsetV));
            }
        } else if (groundStateChange && offsetV > 0) {
            buffer = Math.max(buffer - 0.3, 0);
        }
    }
}
