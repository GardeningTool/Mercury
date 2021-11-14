package cafe.mercury.anticheat.check;

import cafe.mercury.anticheat.Mercury;
import cafe.mercury.anticheat.check.annotation.CheckData;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.violation.Violation;
import cafe.mercury.anticheat.violation.handler.ViolationHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public abstract class Check<T> {

    protected static final Mercury MERCURY = Mercury.getInstance();

    private final CheckData checkData;

    protected final PlayerData data;
    protected final ViolationHandler violationHandler;

    public Check(PlayerData playerData, ViolationHandler violationHandler) {
        Class<?> clazz = getClass();

        if (!clazz.isAnnotationPresent(CheckData.class)) {
            throw new RuntimeException("No CheckData annotation found for class " + clazz.getSimpleName() + "!");
        }

        this.checkData = clazz.getDeclaredAnnotation(CheckData.class);
        this.violationHandler = violationHandler;
        this.data = playerData;
    }

    public void debug(String info, Object... formatted) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.format(info, formatted)));
    }

    public abstract void handle(T event);

    public void fail(Violation violation) {
        this.violationHandler.handleViolation(data, checkData, violation);
    }

}
