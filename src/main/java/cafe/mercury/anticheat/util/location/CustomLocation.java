package cafe.mercury.anticheat.util.location;

import cafe.mercury.anticheat.util.mcp.AxisAlignedBB;
import org.bukkit.util.Vector;

public class CustomLocation {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final float wrappedYaw;
    private final boolean clientGround;

    public CustomLocation(double x, double y, double z, float yaw, float pitch, boolean clientGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.clientGround = clientGround;
        this.wrappedYaw = yaw % 360;
    }

    public CustomLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
        this.clientGround = false;
        this.wrappedYaw = 0;
    }

    public CustomLocation(Vector vector) {
        this.x = vector.getX();
        this.y = vector.getY();
        this.z = vector.getZ();

        this.yaw = 0;
        this.pitch = 0;

        this.clientGround = true;
        this.wrappedYaw = 0;
    }

    public CustomLocation(AxisAlignedBB bb) {
        this.x = (bb.minX + bb.maxX) / 2.0D;
        this.y = bb.minY;
        this.z = (bb.minZ + bb.maxZ) / 2.0D;

        this.yaw = 0;
        this.pitch = 0;

        this.clientGround = true;
        this.wrappedYaw = 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getWrappedYaw() {
        return wrappedYaw;
    }

    public boolean isClientGround() {
        return clientGround;
    }

    public AxisAlignedBB toBoundingBox() {
        return new AxisAlignedBB(x - 0.3, y, z - 0.3, x + 0.3, y + 1.8, z + 0.3);
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }
}
