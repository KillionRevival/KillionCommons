package co.killionrevival.killioncommons.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;

public class MessageUtil {
    private String prefix;

    /**
    * @param plugin The plugin instance
    */
    public MessageUtil(final Plugin plugin) {
        final InputStream jsonConfig = plugin.getResource("config.json");
        if (jsonConfig == null) {
            this.prefix = plugin.getConfig().getString("plugin-prefix");
            return;
        }
        final ConfigUtil configUtil = new ConfigUtil(plugin);
        this.prefix = configUtil.getJsonMember("pluginPrefix").getAsString();
    }

    /**
    * Sends a message to a player after formatting it.
    *
    * @param player  The player to send the message to
    * @param message The message to send
    */
    public void sendMessage(Player player, String message) {
        String formattedMessage = formatMessage(message, false);
        player.sendMessage(formattedMessage);
    }

    /**
     * Sends a message to an audience after formatting it.
     *
     * @param audience The audience to send the message to
     * @param message The message to send
     */
    public void sendMessage(Audience audience, String message) {
        String formattedMessage = formatMessage(message, false);
        audience.sendMessage(LegacyComponentSerializer.legacySection().deserialize(formattedMessage));
    }

    /**
     * Sends a message with a prefix to a player after formatting it.
     *
     * @param player  The player to send the message to
     * @param message The message to send
     */
    public void sendPrefixMessage(Player player, String message) {
        String formattedMessage = formatMessage(message, true);
        player.sendMessage(formattedMessage);
    }

    /**
     * Sends a message with a prefix to an audience after formatting it.
     *
     * @param audience The audience to send the message to
     * @param message The message to send
     */
    public void sendPrefixMessage(Audience audience, String message) {
        String formattedMessage = formatMessage(message, true);
        audience.sendMessage(LegacyComponentSerializer.legacySection().deserialize(formattedMessage));
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

    /**
     * Formats the message with or without the prefix based on the prefix toggle.
     *
     * @param message      The message to format
     * @param prefix_toggle If true, the prefix is added; if false, the prefix is omitted
     * @return The formatted message
     */
    public String formatMessage(String message, Boolean prefix_toggle) {
        if (prefix_toggle) {
            return ChatColor.translateAlternateColorCodes('&', prefix + message);
        } else {
            return ChatColor.translateAlternateColorCodes('&', message);
        }
    }
}