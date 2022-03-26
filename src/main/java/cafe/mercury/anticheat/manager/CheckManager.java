package cafe.mercury.anticheat.manager;

import cafe.mercury.anticheat.check.Check;
import cafe.mercury.anticheat.check.impl.badpackets.BadPacketsA;
import cafe.mercury.anticheat.check.impl.fly.FlyA;
import cafe.mercury.anticheat.check.impl.fly.FlyB;
import cafe.mercury.anticheat.check.impl.reach.ReachA;
import cafe.mercury.anticheat.check.impl.speed.SpeedA;
import cafe.mercury.anticheat.check.impl.velocity.VelocityA;
import cafe.mercury.anticheat.data.PlayerData;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckManager {

    private static final List<Constructor<? extends Check<?>>> CHECK_CONSTRUCTORS = Stream.of(
            BadPacketsA.class,

            FlyA.class, FlyB.class,

            ReachA.class,

            SpeedA.class,

            VelocityA.class
    ).map(clazz -> {
        try {
            return clazz.getDeclaredConstructor(PlayerData.class);
        } catch (Exception exc) {
            return null;
        }
    }).collect(Collectors.toList());

    public List<Check<?>> getChecks(PlayerData playerData) {
        return CHECK_CONSTRUCTORS.stream()
                .map(constructor -> {
                    try {
                        return constructor.newInstance(playerData);
                    } catch (Exception exc) {
                        return null;
                    }
                }).collect(Collectors.toList());
    }
}
