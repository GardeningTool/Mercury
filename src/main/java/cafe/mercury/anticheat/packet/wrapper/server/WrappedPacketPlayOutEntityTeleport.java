package cafe.mercury.anticheat.packet.wrapper.server;

import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import lombok.Getter;

@Getter
public class WrappedPacketPlayOutEntityTeleport extends WrappedPacket {

    private final int entityId;
    private final int posX;
    private final int posY;
    private final int posZ;
    private final byte yaw;
    private final byte pitch;

    public WrappedPacketPlayOutEntityTeleport(PacketContainer packetContainer) {
        StructureModifier<Byte> bytes = packetContainer.getBytes();
        StructureModifier<Integer> integers = packetContainer.getIntegers();

        this.entityId = integers.read(0);
        this.posX = integers.read(0);
        this.posY = integers.read(1);
        this.posZ = integers.read(2);
        this.yaw = bytes.read(0);
        this.pitch = bytes.read(1);
    }
}
