package cafe.mercury.anticheat.packet.wrapper.server;

import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;

@Getter
public class WrappedPacketPlayOutKeepAlive extends WrappedPacket {

    private final int key;

    public WrappedPacketPlayOutKeepAlive(PacketContainer packetContainer) {
        this.key = packetContainer.getIntegers().read(0);
    }
}
