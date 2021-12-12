package cafe.mercury.anticheat.util.trackable.impl;

import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.util.trackable.ITrackable;
import lombok.Getter;

@Getter
public class Abilities implements ITrackable {

    private final PlayerData playerData;
    private final boolean canFly;
    private final boolean isFlying;
    private final double flySpeed;
    private final double moveSpeed;

    private int startTick = -1;

    public Abilities(PlayerData playerData, boolean canFly, boolean isFlying, double flySpeed, double moveSpeed) {
        this.playerData = playerData;
        this.canFly = canFly;
        this.isFlying = isFlying;
        this.flySpeed = flySpeed;
        this.moveSpeed = moveSpeed;
    }

    @Override
    public void start() {
        this.startTick = playerData.getTicksExisted();
    }

    @Override
    public boolean isCompleted() {
        return startTick != -1;
    }

}
