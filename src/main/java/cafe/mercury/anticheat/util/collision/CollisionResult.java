package cafe.mercury.anticheat.util.collision;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CollisionResult {

    private boolean isMathematicallyOnGround;
    private boolean isClimbing;
    private boolean isInCobweb;
    private boolean isInLava;
    private boolean isInWater;
    private boolean isUnderBlock;
    private boolean isCollidedHorizontally;
    private boolean isCollidedVertically;
    private float frictionFactor;
    private boolean onGround;

}
