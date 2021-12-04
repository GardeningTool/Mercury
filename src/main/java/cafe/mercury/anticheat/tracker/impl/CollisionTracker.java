package cafe.mercury.anticheat.tracker.impl;

import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import cafe.mercury.anticheat.tracker.Tracker;
import cafe.mercury.anticheat.util.CollisionUtil;
import cafe.mercury.anticheat.util.collision.CollisionResult;
import cafe.mercury.anticheat.util.location.CustomLocation;
import cafe.mercury.anticheat.util.mcp.AxisAlignedBB;
import lombok.Getter;
import org.bukkit.World;

@Getter
public class CollisionTracker extends Tracker {

    private CollisionResult previousCollisions = new CollisionResult();
    private CollisionResult collisions = new CollisionResult();

    public CollisionTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void handle(WrappedPacket paramPacket) {
        if (paramPacket instanceof WrappedPacketPlayInFlying) {
            World world = data.getPlayer().getWorld();

            CustomLocation location = data.getMovementTracker().getCurrentLocation();
            AxisAlignedBB boundingBox = location.toBoundingBox().expand(0.001, 0.001, 0.001);

            CollisionResult result = CollisionUtil.calculateCollisions(world, location, boundingBox);

            this.previousCollisions = this.collisions;
            this.collisions = result;
        }
    }


}
