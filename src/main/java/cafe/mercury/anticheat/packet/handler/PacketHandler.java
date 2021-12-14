package cafe.mercury.anticheat.packet.handler;

import cafe.mercury.anticheat.Mercury;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.manager.PlayerDataManager;
import cafe.mercury.anticheat.packet.ClassWrapper;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;

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

        handle(data, wrappedPacket);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PlayerData data = playerDataManager.getData(event.getPlayer());

        if (event.isPlayerTemporary()) {
            return;
        }

        WrappedPacket wrappedPacket = ClassWrapper.wrapPacket(event.getPacketType(), event.getPacket());
        
        handle(data, wrappedPacket);
    }

    public void handle(PlayerData data, WrappedPacket wrappedPacket) {
        data.handle(wrappedPacket);
    }

}
