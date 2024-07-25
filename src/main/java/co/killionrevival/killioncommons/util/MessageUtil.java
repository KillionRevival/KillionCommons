package co.killionrevival.killioncommons.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MessageUtil {
    private String prefix;

    public MessageUtil(
            final Plugin plugin
    ) {
        this.prefix = plugin.getConfig().getString("plugin-prefix");
    }

    public void sendMessage(Player player, String message) {
        String formattedMessage = formatMessage(message, false);
        player.sendMessage(formattedMessage);
    }

    public void sendPrefixMessage(Player player, String message) {
        String formattedMessage = formatMessage(message, true);
        player.sendMessage(formattedMessage);
    }

    /**
     * Formats the message with the prefix and color codes.
     *
     * @param message The message to format
     * @return The formatted message
     */
    public String formatMessage(String message) {
        return formatMessage(message, false);
    }

    public String formatMessage(String message, Boolean prefix_toggle) {
        if (prefix_toggle) {
            return ChatColor.translateAlternateColorCodes('&', prefix + message);
        } else {
            return ChatColor.translateAlternateColorCodes('&', message);
        }
    }
}