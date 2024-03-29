package cafe.mercury.anticheat.tracker.impl;

import cafe.mercury.anticheat.check.type.PositionUpdateCheck;
import cafe.mercury.anticheat.check.type.RotationCheck;
import cafe.mercury.anticheat.check.type.VelocityCheck;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.event.PositionUpdateEvent;
import cafe.mercury.anticheat.event.RotationEvent;
import cafe.mercury.anticheat.event.VelocityEvent;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInTransaction;
import cafe.mercury.anticheat.packet.wrapper.server.WrappedPacketPlayOutAbilities;
import cafe.mercury.anticheat.packet.wrapper.server.WrappedPacketPlayOutEntityVelocity;
import cafe.mercury.anticheat.packet.wrapper.server.WrappedPacketPlayOutPosition;
import cafe.mercury.anticheat.tracker.Tracker;
import cafe.mercury.anticheat.util.collision.CollisionResult;
import cafe.mercury.anticheat.util.location.CustomLocation;
import cafe.mercury.anticheat.util.trackable.impl.Abilities;
import cafe.mercury.anticheat.util.trackable.impl.Velocity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MovementTracker extends Tracker {

    private final ActionTracker actionTracker = data.getActionTracker();
    private final CollisionTracker collisionTracker = data.getCollisionTracker();
    private final PotionTracker potionTracker = data.getPotionTracker();

    private final List<Vector> teleports = new ArrayList<>();
    private final List<Velocity> velocities = new ArrayList<>();
    private final List<Abilities> abilities = new ArrayList<>();
    private final List<Velocity> nextVelocities = new ArrayList<>();

    private CustomLocation previousLocation = new CustomLocation(0, 0, 0);
    private CustomLocation currentLocation = new CustomLocation(0, 0, 0);

    private boolean teleporting, smallMove;
    private double aiMoveSpeed;

    private boolean isFlying;
    private boolean canFly;

    public MovementTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void handle(WrappedPacket paramPacket) {
        if (paramPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying packet = (WrappedPacketPlayInFlying) paramPacket;

            teleporting = smallMove = false;

            if (!packet.isMoving() && !packet.isRotating()) {
                smallMove = true; //the client didn't send a position update either due to not moving or due to 0.03
                return;
            }

            double posX, posY, posZ;
            float yaw, pitch;

            if (packet.isMoving()) {
                posX = packet.getX();
                posY = packet.getY();
                posZ = packet.getZ();
            } else {
                posX = currentLocation.getX();
                posY = currentLocation.getY();
                posZ = currentLocation.getZ();
            }

            if (packet.isRotating()) {
                yaw = packet.getYaw();
                pitch = packet.getPitch();

                if (actionTracker.getAttackTicks() <= 1) {
                    RotationEvent event = new RotationEvent(yaw, pitch, Math.abs(yaw - currentLocation.getYaw()),
                            Math.abs(pitch - currentLocation.getPitch()), Math.abs(previousLocation.getYaw() - currentLocation.getYaw()),
                            Math.abs(previousLocation.getPitch() - currentLocation.getPitch()), 100F);

                    data.getChecks().stream()
                            .filter(check -> check instanceof RotationCheck)
                            .forEach(check -> ((RotationCheck) check).handle(event));
                }
            } else {
                yaw = currentLocation.getYaw();
                pitch = currentLocation.getPitch();
            }

            CustomLocation location = new CustomLocation(posX, posY, posZ, yaw, pitch, packet.isOnGround());

            /*
            If the list of teleport locations contains the player's current
            position update, checks will properly compensate for the teleport.
             */

            Vector vec = new Vector(posX, posY, posZ);

            if (teleports.contains(vec)) {
                teleports.remove(vec);
                teleporting = true;
            }

            CollisionResult collisions = collisionTracker.getPreviousCollisions();
            CollisionResult currentCollisions = collisionTracker.getCollisions();

            double offsetV = location.getY() - currentLocation.getY();

            //Calculate the move speed
            if (collisions.isOnGround()) {
                aiMoveSpeed = data.getPlayer().getWalkSpeed() / 2;

                if (actionTracker.isSprinting()) {
                    aiMoveSpeed *= 1.3F; //sprint modifier
                }

                aiMoveSpeed *= .16277136F / Math.pow(collisions.getFrictionFactor(), 3);
            } else {
                aiMoveSpeed = 0.026;
            }

            if (!(currentCollisions.isOnGround() && (currentCollisions.isUnderBlock() || collisions.isUnderBlock()) ||
                    (offsetV > 0.2 && offsetV < 0.42F))) {
                aiMoveSpeed += 0.2F;
            }

            /*
             * Since potion effects affect your movement speed,
             * we have to modify our movement speed as well
             */
            aiMoveSpeed += (0.2 * potionTracker.getAmplifier(PotionEffectType.SPEED));
            aiMoveSpeed -= (0.15 * potionTracker.getAmplifier(PotionEffectType.SLOW));

            PositionUpdateEvent event = new PositionUpdateEvent(location, currentLocation);

            data.getChecks().stream()
                    .filter(check -> check instanceof PositionUpdateCheck)
                    .forEach(check -> ((PositionUpdateCheck) check).handle(event));

            nextVelocities.forEach(velocity -> {
                VelocityEvent velocityEvent = new VelocityEvent(velocity.getVelocityH(), velocity.getVelocityV(),
                        previousLocation, currentLocation, location);
                data.getChecks().stream()
                        .filter(check -> check instanceof VelocityCheck)
                        .forEach(check -> ((VelocityCheck) check).handle(velocityEvent));
            });

            nextVelocities.clear();

            velocities.removeIf(Velocity::isCompleted);

            this.previousLocation = currentLocation;
            this.currentLocation = location;
        } else if (paramPacket instanceof WrappedPacketPlayOutPosition) {
            WrappedPacketPlayOutPosition packet = (WrappedPacketPlayOutPosition) paramPacket;

            double posX = packet.getX();
            double posY = packet.getY();
            double posZ = packet.getZ();

            Vector vec = new Vector(posX, posY, posZ);
            teleports.add(vec);
        } else if (paramPacket instanceof WrappedPacketPlayOutEntityVelocity) {
            WrappedPacketPlayOutEntityVelocity packet = (WrappedPacketPlayOutEntityVelocity) paramPacket;

            double x = packet.getVelocityX() / 8000D;
            double y = packet.getVelocityY() / 8000D;
            double z = packet.getVelocityZ() / 8000D;

            short lastSentTransaction = data.getPingTracker().getLastTransactionId();

            Velocity velocity = new Velocity(data, lastSentTransaction, x, y, z);

            velocities.add(velocity);
        } else if (paramPacket instanceof WrappedPacketPlayOutAbilities) {
            WrappedPacketPlayOutAbilities packet = (WrappedPacketPlayOutAbilities) paramPacket;

            Abilities packetAbilities = new Abilities(data, packet.isCanFly(), packet.isFlying(), packet.getFlySpeed(),  packet.getWalkSpeed());
            abilities.add(packetAbilities);
        } else if (paramPacket instanceof WrappedPacketPlayInTransaction) {
            WrappedPacketPlayInTransaction packet = (WrappedPacketPlayInTransaction) paramPacket;

            short id = packet.getTransactionId();

            velocities.stream()
                    .filter(velocity -> velocity.getTransaction() == id)
                    .forEach(nextVelocities::add);

            abilities.stream()
                    .filter(ability -> ability.getStartTick() == id)
                    .findFirst()
                    .ifPresent(ability -> {
                        this.isFlying = ability.isFlying();
                        this.canFly = ability.isCanFly();
                    });
        }
    }

    public double getVelocityH() {
        return velocities.stream()
                .mapToDouble(Velocity::getVelocityH)
                .sum();
    }

    public double getVelocityV() {
        return velocities.stream()
                .mapToDouble(Velocity::getVelocityV)
                .sum();
    }
}
