package cafe.mercury.anticheat.check.impl.reach;

import cafe.mercury.anticheat.check.annotation.CheckData;
import cafe.mercury.anticheat.check.type.PacketCheck;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInUseEntity;
import cafe.mercury.anticheat.tracker.impl.EntityTracker;
import cafe.mercury.anticheat.tracker.impl.MovementTracker;
import cafe.mercury.anticheat.tracker.impl.PingTracker;
import cafe.mercury.anticheat.util.location.CustomLocation;
import cafe.mercury.anticheat.util.mcp.AxisAlignedBB;
import cafe.mercury.anticheat.util.mcp.MathHelper;
import cafe.mercury.anticheat.util.mcp.MovingObjectPosition;
import cafe.mercury.anticheat.util.mcp.Vec3;
import cafe.mercury.anticheat.violation.handler.ViolationHandler;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Bukkit;

@CheckData(
        name = "Reach",
        description = "Checks for excessive distance between attacks",
        type = "A"
)
public class ReachA extends PacketCheck {

    private final EntityTracker entityTracker = data.getEntityTracker();
    private final MovementTracker movementTracker = data.getMovementTracker();
    private final PingTracker pingTracker = data.getPingTracker();

    private Integer targetId;

    public ReachA(PlayerData playerData) {
        super(playerData, new ViolationHandler(10));
    }

    @Override
    public void handle(WrappedPacket paramPacket) {
    }
}
