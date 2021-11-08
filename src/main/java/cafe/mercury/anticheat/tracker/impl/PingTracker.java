package cafe.mercury.anticheat.tracker.impl;

import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInTransaction;
import cafe.mercury.anticheat.packet.wrapper.server.WrappedPacketPlayOutTransaction;
import cafe.mercury.anticheat.tracker.Tracker;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PingTracker extends Tracker {

    private final Map<Short, Long> transactionMap = new HashMap<>();

    private short lastTransactionId;
    private long lastPing;

    public PingTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void handle(WrappedPacket paramPacket) {
        if (paramPacket instanceof WrappedPacketPlayOutTransaction) {
            WrappedPacketPlayOutTransaction packet = (WrappedPacketPlayOutTransaction) paramPacket;
            short transactionId = packet.getTransactionId();

            transactionMap.put(transactionId, System.currentTimeMillis());

            this.lastTransactionId = transactionId;
        } else if (paramPacket instanceof WrappedPacketPlayInTransaction) {
            WrappedPacketPlayInTransaction packet = (WrappedPacketPlayInTransaction) paramPacket;

            short id = packet.getTransactionId();

            Long sentTime = transactionMap.get(id);
            if (sentTime == null) { //the player is sending transactions that were never sent by the server
                return;
            }

            long timeElapsed = System.currentTimeMillis() - sentTime;

            this.transactionMap.remove(id);
            this.lastPing = timeElapsed;
        }
    }
}
