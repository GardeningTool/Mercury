package cafe.mercury.anticheat;

import cafe.mercury.anticheat.manager.CheckManager;
import cafe.mercury.anticheat.manager.PlayerDataManager;
import cafe.mercury.anticheat.packet.handler.PacketHandler;
import com.comphenix.protocol.ProtocolLibrary;
import dev.thomazz.pledge.PledgeImpl;
import dev.thomazz.pledge.api.Pledge;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Mercury extends JavaPlugin {

    private final CheckManager checkManager = new CheckManager();
    private final PlayerDataManager playerDataManager = new PlayerDataManager();

    private static Mercury instance;

    private Pledge pledge;

    @Override
    public void onEnable() {
        instance = this;

        this.pledge = new PledgeImpl().range(Short.MIN_VALUE, (short) 0);
        this.pledge.start(this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketHandler(this));
     }

     public static Mercury getInstance() {
        return instance;
     }
}
