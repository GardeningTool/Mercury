package cafe.mercury.anticheat.util.potion;

import cafe.mercury.anticheat.data.PlayerData;
import lombok.Getter;
import org.bukkit.potion.PotionEffectType;

@Getter
public class Potion {

    private final PlayerData playerData;
    private final PotionEffectType potionEffectType;
    private final int amplifier;
    private final int duration;
    private final short transactionId;
    private int startTick = -1;
    private int endTick = -1;

    public Potion(PlayerData playerData, PotionEffectType potionEffectType, int amplifier, int duration, short transactionId) {
        this.playerData = playerData;
        this.potionEffectType = potionEffectType;
        this.amplifier = amplifier;
        this.duration = duration;
        this.transactionId = transactionId;
    }

    public boolean isActive() {
        int ticksExisted = playerData.getTicksExisted();
        return ticksExisted >= startTick && ticksExisted < endTick;
    }

    public void start() {
        int tick = playerData.getTicksExisted();

        this.startTick = tick;
        this.endTick = tick + (duration * 20);
    }

    public boolean hasCompleted() {
        return endTick > -1 && playerData.getTicksExisted() > endTick;
    }

}
