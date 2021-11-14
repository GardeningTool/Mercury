package cafe.mercury.anticheat.data;

import cafe.mercury.anticheat.Mercury;
import cafe.mercury.anticheat.check.Check;
import cafe.mercury.anticheat.check.type.PacketCheck;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import cafe.mercury.anticheat.tracker.impl.*;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class PlayerData {

    private static final ExecutorService checkExecutor = Executors.newSingleThreadExecutor();

    private final List<Check<?>> checks = new ArrayList<>();

    private final Player player;

    private final ActionTracker actionTracker;
    private final CollisionTracker collisionTracker;
    private final MovementTracker movementTracker;
    private final PingTracker pingTracker;
    private final PotionTracker potionTracker;

    private int ticksExisted;

    public PlayerData(Player player) {
        this.player = player;

        this.actionTracker = new ActionTracker(this);
        this.collisionTracker = new CollisionTracker(this);
        this.movementTracker = new MovementTracker(this);
        this.pingTracker = new PingTracker(this);
        this.potionTracker = new PotionTracker(this);

        this.checks.addAll(Mercury.getInstance().getCheckManager().getChecks(this));
    }

    public void handle(WrappedPacket wrappedPacket) {
        checkExecutor.execute(() -> {
            this.actionTracker.handle(wrappedPacket);
            this.collisionTracker.handle(wrappedPacket);
            this.potionTracker.handle(wrappedPacket);
            this.pingTracker.handle(wrappedPacket);
            this.movementTracker.handle(wrappedPacket);

            if (wrappedPacket instanceof WrappedPacketPlayInFlying) {
                ++ticksExisted;
            }

            checks.stream()
                    .filter(check -> check instanceof PacketCheck)
                    .forEach(check -> ((PacketCheck) check).handle(wrappedPacket));
        });
    }
}
