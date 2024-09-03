package co.killionrevival.killioncommons.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

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

    /**
     * Formats a message with the specified color and prefix.
     * @param color The color to apply to the message
     * @param message The message to format
     * @return The formatted message as a TextComponent
     */
    public TextComponent getConsoleComponent(NamedTextColor color, String message) {
        return Component.text().append(TextFormatUtil.getComponentFromLegacyString(prefix))
                .append(Component.text().content(message).color(color)).build();
    }
}