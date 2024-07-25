package co.killionrevival.killioncommons.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BroadcastUtil {
    private final MessageUtil messageUtil;

    public BroadcastUtil(
            final Plugin plugin
    ) {
        this.messageUtil = new MessageUtil(plugin);
    }

    /**
     * Broadcasts a message to all online players.
     *
     * @param message The message to broadcast
     */
    public void broadcastMessage(String message) {
        String formattedMessage = messageUtil.formatMessage(message);
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(formattedMessage));
    }

    /**
     * Broadcasts a message to all online players with a specific permission.
     *
     * @param message The message to broadcast
     * @param permission The permission required to receive the broadcast
     */
    public void broadcastMessageWithPermission(String message, String permission) {
        String formattedMessage = messageUtil.formatMessage(message);
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission(permission))
                .forEach(player -> player.sendMessage(formattedMessage));
    }

    /**
     * Broadcasts a message to all online players except the specified player.
     *
     * @param message The message to broadcast
     * @param excludedPlayer The player to exclude from the broadcast
     */
    public void broadcastMessageExcept(String message, Player excludedPlayer) {
        String formattedMessage = messageUtil.formatMessage(message);
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.equals(excludedPlayer))
                .forEach(player -> player.sendMessage(formattedMessage));
    }
}