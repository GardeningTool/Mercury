package cafe.mercury.anticheat.tracker.impl;

import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInTransaction;
import cafe.mercury.anticheat.packet.wrapper.server.WrappedPacketPlayOutEntityEffect;
import cafe.mercury.anticheat.packet.wrapper.server.WrappedPacketPlayOutRemoveEntityEffect;
import cafe.mercury.anticheat.tracker.Tracker;
import cafe.mercury.anticheat.util.potion.Potion;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PotionTracker extends Tracker {

    private static final List<Integer> MONITORED_EFFECTS = Arrays.asList(1, 2, 8);

    private final List<Potion> pendingPotions = new ArrayList<>();
    private final List<Potion> activePotions = new ArrayList<>();

    private final PingTracker pingTracker = data.getPingTracker();

    public PotionTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void handle(WrappedPacket paramPacket) {
        if (paramPacket instanceof WrappedPacketPlayOutEntityEffect) {
            WrappedPacketPlayOutEntityEffect packet = (WrappedPacketPlayOutEntityEffect) paramPacket;

            if (packet.getEntityId() != data.getPlayer().getEntityId()) {
                return;
            }

            int effect = packet.getEffectId();

            if (!MONITORED_EFFECTS.contains(effect)) {
                return;
            }

            short id = pingTracker.getLastTransactionId();

            Potion potion = new Potion(data, PotionEffectType.getById(effect), packet.getAmplifier(), packet.getDuration(), id);
            pendingPotions.add(potion);
        } else if (paramPacket instanceof WrappedPacketPlayInTransaction) {
            WrappedPacketPlayInTransaction packet = (WrappedPacketPlayInTransaction) paramPacket;

            short transactionId = packet.getTransactionId();

            pendingPotions.stream()
                    .filter(potion -> potion.getTransactionId() == transactionId)
                    .forEach(potion -> {
                        activePotions.removeIf(active -> active.getPotionEffectType() == potion.getPotionEffectType());
                        activePotions.add(potion);
                        potion.start();
                    });

            pendingPotions.removeIf(activePotions::contains);
        } else if (paramPacket instanceof WrappedPacketPlayInFlying) {
            activePotions.removeIf(Potion::hasCompleted);
        } else if (paramPacket instanceof WrappedPacketPlayOutRemoveEntityEffect) {
            WrappedPacketPlayOutRemoveEntityEffect packet = (WrappedPacketPlayOutRemoveEntityEffect) paramPacket;

            if (packet.getEntityId() != data.getPlayer().getEntityId()) {
                return;
            }

            int effect = packet.getEffectId();
            short id = pingTracker.getLastTransactionId();

            Potion potion = new Potion(data, PotionEffectType.getById(effect), 0, 99999, id);
            pendingPotions.add(potion);
        }
    }

    public int getAmplifier(PotionEffectType type) {
        return activePotions.stream()
                .filter(potion -> potion.getPotionEffectType() == type)
                .map(Potion::getAmplifier)
                .findFirst()
                .orElse(0);
    }

}
