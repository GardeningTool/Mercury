package cafe.mercury.anticheat.packet.wrapper.server;

import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import lombok.Getter;

@Getter
public class WrappedPacketPlayOutEntityTeleport extends WrappedPacket {

    private final int entityId;
    private final byte posX;
    private final byte posY;
    private final byte posZ;
    private final byte yaw;
    private final byte pitch;

    public WrappedPacketPlayOutEntityTeleport(PacketContainer packetContainer) {
        StructureModifier<Byte> bytes = packetContainer.getBytes();

        this.entityId = packetContainer.getIntegers().read(0);
        this.posX = bytes.read(0);
        this.posY = bytes.read(1);
        this.posZ = bytes.read(2);
        this.yaw = bytes.read(3);
        this.pitch = bytes.read(4);
    }
}
