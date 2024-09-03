package co.killionrevival.killioncommons.util;

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
            return prefix + message;
        } else {
            return message;
        }
    }

    /**
     * Colors the message with the specified color.
     *
     * @param color   The NamedTextColor to apply to the message
     * @param message The message to color
     * @return The colored message as a string
     */
    public String colorMessage(NamedTextColor color, String message) {
        TextComponent component = Component.text().content(message).color(color).build();
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}