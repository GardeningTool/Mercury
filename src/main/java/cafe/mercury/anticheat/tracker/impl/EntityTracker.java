package cafe.mercury.anticheat.tracker.impl;

import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInTransaction;
import cafe.mercury.anticheat.packet.wrapper.server.*;
import cafe.mercury.anticheat.tracker.Tracker;
import cafe.mercury.anticheat.util.entity.TrackedData;
import cafe.mercury.anticheat.util.location.CustomLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EntityTracker extends Tracker {

    private final Map<Integer, TrackedData> entityTracker = new HashMap<>();

    private double lastPosX, lastPosY, lastPosZ;
    private float lastYaw, lastPitch;
    private short lastTransaction = -1;

    public EntityTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void handle(WrappedPacket paramPacket) {
        if (paramPacket instanceof WrappedPacketPlayOutNamedEntitySpawn) {
            WrappedPacketPlayOutNamedEntitySpawn packet = (WrappedPacketPlayOutNamedEntitySpawn) paramPacket;

            double posX = packet.getPosX();
            double posY = packet.getPosY();
            double posZ = packet.getPosZ();

            CustomLocation customLocation = new CustomLocation(posX, posY, posZ, 0F, 0F, true);

            TrackedData trackedData = new TrackedData();
            trackedData.addTarget(data.getPingTracker().getLastTransactionId(), customLocation);

            entityTracker.put(packet.getEntityId(), trackedData);
        } else if (paramPacket instanceof WrappedPacketPlayOutEntityTeleport) {
            WrappedPacketPlayOutEntityTeleport packet = (WrappedPacketPlayOutEntityTeleport) paramPacket;

            int id = packet.getEntityId();
            double posX = packet.getPosX();
            double posY = packet.getPosY();
            double posZ = packet.getPosZ();

            float yaw = packet.getYaw();
            float pitch = packet.getPitch();

            CustomLocation customLocation = new CustomLocation(posX, posY, posZ, yaw, pitch, true);
            TrackedData trackedData = entityTracker.get(id);

            trackedData.addTarget(data.getPingTracker().getLastTransactionId(), customLocation);
            entityTracker.put(id, trackedData);

            this.lastPosX = posX;
            this.lastPosY = posY;
            this.lastPosZ = posZ;

            this.lastYaw = yaw;
            this.lastPitch = pitch;
        }  else if (paramPacket instanceof WrappedPacketPlayOutDestroyEntities) {
            WrappedPacketPlayOutDestroyEntities packet = (WrappedPacketPlayOutDestroyEntities) paramPacket;

            Arrays.stream(packet.getEntities()).forEach(entityTracker::remove);
        } else if (paramPacket instanceof WrappedPacketPlayOutEntity) {
            WrappedPacketPlayOutEntity packet = (WrappedPacketPlayOutEntity) paramPacket;

            int id = packet.getEntityId();
            boolean hasPos = !(packet instanceof WrappedPacketPlayOutEntityLook);
            boolean hasLook = packet instanceof WrappedPacketPlayOutEntityLook ||
                    packet instanceof WrappedPacketPlayOutEntityLookMove;

            double posX;
            double posY;
            double posZ;

            float yaw;
            float pitch;

            if (hasPos) {
                posX = packet.getPosX();
                posY = packet.getPosY();
                posZ = packet.getPosZ();
            } else {
                posX = lastPosX;
                posY = lastPosY;
                posZ = lastPosZ;
            }

            if (hasLook) {
                yaw = packet.getYaw();
                pitch = packet.getPitch();
            } else {
                yaw = lastYaw;
                pitch = lastPitch;
            }

            CustomLocation location = new CustomLocation(posX, posY, posZ, yaw, pitch, true);
            TrackedData trackedData = entityTracker.get(id);

            trackedData.addTarget(data.getPingTracker().getLastTransactionId(), location);
            entityTracker.put(id, trackedData);
        } else if (paramPacket instanceof WrappedPacketPlayInTransaction) {
            WrappedPacketPlayInTransaction transaction = (WrappedPacketPlayInTransaction) paramPacket;

            short transactionId = transaction.getTransactionId();

            entityTracker.forEach((id, entity) -> {
                entity.removeTargetPostTransaction(lastTransaction);
            });

            this.lastTransaction = transactionId;
        }
    }

    public CustomLocation getTrackedLocation(int entityId, short tick) {
        TrackedData data = entityTracker.get(entityId);

        if (data == null) {
            return null;
        }

        return data.getAtTick(tick);
    }
}
