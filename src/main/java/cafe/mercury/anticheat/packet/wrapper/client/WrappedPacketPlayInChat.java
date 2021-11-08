package cafe.mercury.anticheat.packet.wrapper.client;

import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInChat extends WrappedPacket {

    private final String length;

    public WrappedPacketPlayInChat(PacketContainer packetContainer) {
        this.length = packetContainer.getStrings().read(0);
    }
}
