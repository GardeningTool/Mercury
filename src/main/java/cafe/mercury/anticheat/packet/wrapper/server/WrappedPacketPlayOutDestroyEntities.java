package cafe.mercury.anticheat.packet.wrapper.server;

import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;

@Getter
public class WrappedPacketPlayOutDestroyEntities extends WrappedPacket {

    private final int[] entities;

    public WrappedPacketPlayOutDestroyEntities(PacketContainer packetContainer) {
        this.entities = packetContainer.getIntegerArrays().read(0);
    }
}
