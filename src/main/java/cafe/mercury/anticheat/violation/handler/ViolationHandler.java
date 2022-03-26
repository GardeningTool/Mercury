package cafe.mercury.anticheat.violation.handler;

import cafe.mercury.anticheat.Mercury;
import cafe.mercury.anticheat.check.annotation.CheckData;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.util.location.CustomLocation;
import cafe.mercury.anticheat.violation.Violation;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@RequiredArgsConstructor
public class ViolationHandler {

    private static final String BAN_COMMAND = "ban %s Unfair Advantage -s";

    private final int maxViolations;
    private int violations;

    public void handleViolation(PlayerData playerData, CheckData checkData, Violation violation) {
        if (++violations >= maxViolations) {
            Bukkit.getScheduler().runTask(Mercury.getInstance(), () -> {
//                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(BAN_COMMAND, playerData.getPlayer().getName()));
            });
        }

        TextComponent textComponent = new TextComponent();
        StringBuilder textBuilder = new StringBuilder("&7(&6&l!&7) &e");

        textBuilder.append(playerData.getPlayer().getName()).append(" &7failed &e")
                .append(checkData.name()).append(" ").append(checkData.type())
                .append(" &7(&6").append(violations).append("&7/&6").append(maxViolations).append("&7)");

        textComponent.setText(ChatColor.translateAlternateColorCodes('&', textBuilder.toString()));

        if (checkData.experimental()) {
            textBuilder.append(" (Experimental)");
        }

        StringBuilder hoverComponentBuilder = new StringBuilder();

        if (violation.getViolationData().length > 0) {
            Object[] data = violation.getViolationData();

            for(int i = 0; i < data.length; i++) {
                hoverComponentBuilder.append("&6")
                        .append(data[i]).append(" &7(&e")
                        .append(data[++i]).append("&7)\n");
            }

            hoverComponentBuilder.append("\n&6Ping&7: &e").append(playerData.getPingTracker().getLastPing());
            hoverComponentBuilder.append("\n&7&l&m---------------------------------------&r");

            BaseComponent[] hoverComponents = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                    hoverComponentBuilder.toString())).create();

            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponents));
        }


        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> player.hasPermission("mercury.alerts"))
                .forEach(player -> {
                    player.spigot().sendMessage(textComponent);
                });
    }

    public void kick(PlayerData playerData, String reason) {
        Bukkit.getScheduler().runTask(Mercury.getInstance(), () -> {
            playerData.getPlayer().kickPlayer(reason);
        });
    }

    public void setback(PlayerData playerData, CustomLocation setback) {
        Bukkit.getScheduler().runTask(Mercury.getInstance(), () -> {
            playerData.getPlayer().teleport(new Location(playerData.getPlayer().getWorld(),
                    setback.getX(), setback.getY(), setback.getZ()));
        });
    }
}
