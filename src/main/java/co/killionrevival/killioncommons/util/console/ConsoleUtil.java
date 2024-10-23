package co.killionrevival.killioncommons.util.console;

import co.killionrevival.killioncommons.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import co.killionrevival.killioncommons.util.TextFormatUtil;
import co.killionrevival.killioncommons.util.console.models.LogLevel;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ConsoleUtil {
    private LogLevel logLevel;
    private String prefix;
    private static final Audience console = Bukkit.getConsoleSender();

    /**
     * @param plugin The plugin instance
     */
    public ConsoleUtil(Plugin plugin) {
        getLogLevel(plugin);
        getPrefix(plugin);
    }

    public ConsoleUtil(Plugin plugin, LogLevel logLevel) {
        this(plugin);
        this.logLevel = logLevel;
    }

    /**
     * Logs an error message to the console with a red color.
     *
     * @param message The error message to log
     */
    public void sendError(String message) {
        if (LogLevel.ERROR.getLevel() <= this.logLevel.getLevel()) {
            console.sendMessage(getFormattedMessage("ERROR", message, NamedTextColor.RED));
        }
    }

    /**
     * Logs an error message to the console with a red color.
     * Also logs the throwable (exception) to the console.
     * 
     * @param message The error message to log
     * @param e The throwable to log
     */
    public void sendError(String message, Throwable e) {
        if (LogLevel.ERROR.getLevel() <= this.logLevel.getLevel()) {
            console.sendMessage(getFormattedMessage("ERROR", message, NamedTextColor.RED));
            this.sendThrowable(e);
        }
    }

    /**
     * Logs a throwable (exception) to the console with a dark red color.
     *
     * @param t The throwable to log
     */
    public void sendThrowable(Throwable t) {
        if (LogLevel.ERROR.getLevel() <= this.logLevel.getLevel()) {
            console.sendMessage(getFormattedMessage("EXCEPTION", t.getMessage(), NamedTextColor.DARK_RED));
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            console.sendMessage(
                    getFormattedMessage("EXCEPTION", "Stack trace:\n" + sw.toString(), NamedTextColor.DARK_RED));
        }
    }

    /**
     * Logs a warning message to the console with a gold color.
     *
     * @param message The warning message to log
     */
    public void sendWarning(String message) {
        if (LogLevel.WARNING.getLevel() <= this.logLevel.getLevel()) {
            console.sendMessage(getFormattedMessage("WARNING", message, NamedTextColor.GOLD));
        }
    }

    /**
     * Logs a warning message to the console with a gold color.
     * Also logs the throwable (exception) to the console.
     * 
     * @param message The warning message to log
     * @param e The throwable to log
     */
    public void sendWarning(String message, Throwable e) {
        if (LogLevel.WARNING.getLevel() <= this.logLevel.getLevel()) {
            console.sendMessage(getFormattedMessage("WARNING", message, NamedTextColor.GOLD));
            this.sendThrowable(e);
        }
    }

    /**
     * Logs an informational message to the console with an aqua color.
     *
     * @param message The informational message to log
     */
    public void sendInfo(String message) {
        if (LogLevel.INFO.getLevel() <= this.logLevel.getLevel()) {
            console.sendMessage(getFormattedMessage("INFO", message, NamedTextColor.AQUA));
        }
    }

    /**
     * Logs a debug message to the console with a light purple color.
     *
     * @param message The debug message to log
     */
    public void sendDebug(String message) {
        if (LogLevel.DEBUG.getLevel() <= this.logLevel.getLevel()) {
            console.sendMessage(getFormattedMessage("DEBUG", message, NamedTextColor.LIGHT_PURPLE));
        }
    }

    /**
     * Logs a success message to the console with a dark green color.
     *
     * @param message The success message to log
     */
    public void sendSuccess(String message) {
        if (LogLevel.INFO.getLevel() <= this.logLevel.getLevel()) {
            console.sendMessage(getFormattedMessage("SUCCESS", message, NamedTextColor.DARK_GREEN));
        }
    }

    /**
     * Formats a message with the specified color and level tag.
     * @param levelTag The log level tag to use
     * @param message The message to format
     * @param color The color to apply to the message
     * @return
     */
    private Component getFormattedMessage(String levelTag, String message, NamedTextColor color) {
        String fullMessage = String.format("%s: %s", levelTag, message);
        return (Component) this.getConsoleComponent(color, fullMessage);
    }

    /**
     * Retrieves and sets the log level for the plugin from the configuration file.
     * @param plugin
     */
    private void getLogLevel(Plugin plugin) {
        String logLevel;
        if (plugin.getResource("config.json") != null) {
            final ConfigUtil configUtil = new ConfigUtil(plugin);
            logLevel = configUtil.getJsonMember("log-level").getAsString();
        }
        else {
            logLevel = plugin.getConfig().getString("log-level");
        }

        if (logLevel == null || logLevel.isEmpty()) {
            this.logLevel = LogLevel.INFO;
        } else {
            try {
                this.logLevel = LogLevel.valueOf(logLevel);
            } catch (IllegalArgumentException e) {
                this.logLevel = LogLevel.INFO;
            }
        }
    }

    /**
     * Retrieves and sets the prefix for the plugin from the configuration file.
     * @param plugin
     */
    private void getPrefix(Plugin plugin) {
        String prefix;
        if (plugin.getResource("config.json") != null) {
            final ConfigUtil configUtil = new ConfigUtil(plugin);
            prefix = configUtil.getJsonMember("plugin-prefix").getAsString();
        }
        else {
            prefix = plugin.getConfig().getString("plugin-prefix");
        }

        if (prefix == null || prefix.isEmpty()) {
            this.prefix = "";
        } else {
            this.prefix = prefix;
        }
    }

    /**
    * Formats a message with the specified color and prefix.
    * @param color The color to apply to the message
    * @param message The message to format
    * @return The formatted message as a TextComponent
    */
    private TextComponent getConsoleComponent(NamedTextColor color, String message) {
        return Component.text().append(TextFormatUtil.getComponentFromLegacyString(prefix))
                .append(Component.text().content(message).color(color)).build();
    }
}