package cafe.mercury.anticheat.packet.wrapper.client;

import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInArmAnimation extends WrappedPacket {

    private final long timestamp;

    public WrappedPacketPlayInArmAnimation(PacketContainer packetContainer) {
        this.timestamp = packetContainer.getLongs().read(0);
    }
}
