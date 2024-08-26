package co.killionrevival.killioncommons.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;

public class MessageUtil {
    private String prefix;

    public MessageUtil(
            final Plugin plugin
    ) {
        final InputStream jsonConfig = plugin.getResource("config.json");
        if (jsonConfig == null) {
            this.prefix = plugin.getConfig().getString("plugin-prefix");
            return;
        }
        final ConfigUtil configUtil = new ConfigUtil(plugin);
        this.prefix = configUtil.getJsonMember("plugin-prefix").getAsString();
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