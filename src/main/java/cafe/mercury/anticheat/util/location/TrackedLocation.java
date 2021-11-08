package cafe.mercury.anticheat.util.location;

import cafe.mercury.anticheat.util.mcp.AxisAlignedBB;
import org.bukkit.util.Vector;

public class TrackedLocation {

    private final Vector vector;
    private final int owner;
    private boolean interpolated = false;
    private int increment = 3;

    public TrackedLocation(Vector vector, int owner) {
        this.vector = vector;
        this.owner = owner;
    }

    public TrackedLocation(Vector vector, int owner, boolean interpolated) {
        this.interpolated = interpolated;
        this.vector = vector;
        this.owner = owner;
    }

    public Vector getVector() {
        return vector;
    }

    public void decrement() {
        increment--;
    }

    public int getIncrement() {
        return increment;
    }

    public boolean isInterpolated() {
        return interpolated;
    }

    public int getOwner() {
        return owner;
    }

    public AxisAlignedBB getBoundingBox() {
        return new AxisAlignedBB(vector.getX() - 0.3, vector.getY(), vector.getZ() - 0.3,
                vector.getX() + 0.3, vector.getY() + 1.8, vector.getZ() + 0.3);
    }
}
