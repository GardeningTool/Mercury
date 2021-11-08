package cafe.mercury.anticheat.packet.wrapper.server;

import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;

@Getter
public class WrappedPacketPlayOutRemoveEntityEffect extends WrappedPacket {

    private final int entityId;
    private final byte effectId;

    public WrappedPacketPlayOutRemoveEntityEffect(PacketContainer packetContainer) {
        this.entityId = packetContainer.getIntegers().read(0);
        this.effectId = packetContainer.getBytes().read(0);
    }
}
