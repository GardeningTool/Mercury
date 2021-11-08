package cafe.mercury.anticheat.tracker;

import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;

public abstract class Tracker {

    protected final PlayerData data;

    public Tracker(PlayerData playerData) {
        this.data = playerData;
    }

    public abstract void handle(WrappedPacket paramPacket);

}
