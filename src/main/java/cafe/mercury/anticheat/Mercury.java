package cafe.mercury.anticheat;

import cafe.mercury.anticheat.manager.CheckManager;
import cafe.mercury.anticheat.manager.PlayerDataManager;
import cafe.mercury.anticheat.packet.handler.PacketHandler;
import com.comphenix.protocol.ProtocolLibrary;
import dev.thomazz.pledge.api.Pledge;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

@Getter
public class Mercury extends JavaPlugin {

    private final CheckManager checkManager = new CheckManager();
    private final PlayerDataManager playerDataManager = new PlayerDataManager();

    private static Mercury instance;

    private Pledge pledge;

    @Override
    public void onEnable() {
        instance = this;

        this.pledge = Pledge.build().range(Short.MIN_VALUE, (short) 0);
        this.pledge.start(this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketHandler(this));

//        Bukkit.getScheduler().scheduleSyncRepeatingTask(this,  () -> {
//            Bukkit.getOnlinePlayers().forEach(player -> {
//                player.setVelocity(player.getLocation().getDirection().clone().add(new Vector(0.3, 0.6, 0.3)));
//            });
//        }, 0, 100);
     }

     public static Mercury getInstance() {
        return instance;
     }
}
