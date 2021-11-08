package cafe.mercury.anticheat.packet.handler;

import cafe.mercury.anticheat.Mercury;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.manager.PlayerDataManager;
import cafe.mercury.anticheat.packet.ClassWrapper;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PacketHandler extends PacketAdapter {

    private final PlayerDataManager playerDataManager;

    public PacketHandler(Mercury mercury) {
        super(mercury, ListenerPriority.NORMAL, ClassWrapper.getProcessedPackets());

        this.playerDataManager = mercury.getPlayerDataManager();
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PlayerData data = playerDataManager.getData(event.getPlayer());
        WrappedPacket wrappedPacket = ClassWrapper.wrapPacket(event.getPacketType(), event.getPacket());

        data.handle(wrappedPacket);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PlayerData data = playerDataManager.getData(event.getPlayer());
        WrappedPacket wrappedPacket = ClassWrapper.wrapPacket(event.getPacketType(), event.getPacket());

        data.handle(wrappedPacket);
    }

}
