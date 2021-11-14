package cafe.mercury.anticheat.util.location;

import cafe.mercury.anticheat.util.mcp.AxisAlignedBB;
import lombok.Getter;

@Getter
public class CustomLocation {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final boolean clientGround;

    public CustomLocation(double x, double y, double z, float yaw, float pitch, boolean clientGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.clientGround = clientGround;
    }

    public CustomLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
        this.clientGround = false;
    }

    public AxisAlignedBB toBoundingBox() {
        return new AxisAlignedBB(x - 0.3, y, z - 0.3, x + 0.3, y + 1.8, z + 0.3);
    }
}
