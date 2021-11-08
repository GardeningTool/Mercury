package cafe.mercury.anticheat.packet.wrapper.client;

import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInCustomPayload extends WrappedPacket {

    private final String payload;

    public WrappedPacketPlayInCustomPayload(PacketContainer packetContainer) {
        this.payload = packetContainer.getStrings().read(0);
    }
}
