package cafe.mercury.anticheat.util.velocity;

import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.util.math.MathUtil;
import lombok.Getter;

@Getter
public class Velocity {

    private final double velocityH;
    private final double velocityV;
    private final PlayerData playerData;
    private final short transaction;
    private int startTick = -1;
    private int completedTick = -1;

    public Velocity(PlayerData playerData, short transaction, double x, double y, double z) {
        this.playerData = playerData;
        this.velocityH = MathUtil.hypot(x, z);
        this.velocityV = y;
        this.transaction = transaction;
    }

    public void start() {
        int ticks = playerData.getTicksExisted();

        this.startTick = ticks;
        this.completedTick = (int) (ticks + ((velocityH / 2 + 2) * 15));
    }

    public boolean isCompleted(){
        return completedTick != -1 && playerData.getTicksExisted() > completedTick;
    }

}
