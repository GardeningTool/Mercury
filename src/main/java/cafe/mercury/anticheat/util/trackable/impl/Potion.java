package cafe.mercury.anticheat.util.trackable.impl;

import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.util.trackable.ITrackable;
import lombok.Getter;
import org.bukkit.potion.PotionEffectType;

@Getter
public class Potion implements ITrackable {

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

    public void start() {
        int tick = playerData.getTicksExisted();

        this.startTick = tick;
        this.endTick = tick + (duration * 20);
    }

    public boolean isCompleted() {
        return endTick > -1 && playerData.getTicksExisted() > endTick;
    }

}
