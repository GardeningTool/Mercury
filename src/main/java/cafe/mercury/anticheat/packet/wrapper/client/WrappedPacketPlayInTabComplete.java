package cafe.mercury.anticheat.packet.wrapper.client;

import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInTabComplete extends WrappedPacket {

    private final String message;
    private final BlockPosition targetPosition;

    public WrappedPacketPlayInTabComplete(PacketContainer packetContainer) {
        this.message = packetContainer.getStrings().read(0);
        this.targetPosition = packetContainer.getBlockPositionModifier().read(0);
    }
}