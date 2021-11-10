package cafe.mercury.anticheat.manager;

import cafe.mercury.anticheat.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class PlayerDataManager {

    private final Map<UUID, PlayerData> playerData = new WeakHashMap<>();
    
    public PlayerData getData(Player player) {
        return playerData.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData(player));
    }
}
