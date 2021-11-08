package cafe.mercury.anticheat.packet.wrapper.server;

import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import lombok.Getter;

@Getter
public class WrappedPacketPlayOutEntityEffect extends WrappedPacket {

    private final int entityId;
    private final byte effectId;
    private final byte amplifier;
    private final int duration;

    public WrappedPacketPlayOutEntityEffect(PacketContainer packetContainer) {
        StructureModifier<Integer> integers = packetContainer.getIntegers();
        StructureModifier<Byte> bytes = packetContainer.getBytes();

        this.entityId = integers.read(0);
        this.effectId = bytes.read(0);
        this.amplifier = bytes.read(1);
        this.duration = integers.read(1);
    }
}
