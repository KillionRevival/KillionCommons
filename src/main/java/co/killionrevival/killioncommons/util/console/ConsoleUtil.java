package co.killionrevival.killioncommons.util.console;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import co.killionrevival.killioncommons.util.ConfigUtil;
import co.killionrevival.killioncommons.util.MessageUtil;
import co.killionrevival.killioncommons.util.console.models.LogLevel;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.InputStream;

public class ConsoleUtil {
    private LogLevel logLevel;
    private final MessageUtil messageUtil;
    private static final Audience console = Bukkit.getConsoleSender();

    /**
     * @param plugin The plugin instance
     */
    public ConsoleUtil(Plugin plugin) {
        this.messageUtil = new MessageUtil(plugin);

        getLogLevel(plugin);
    }

    public ConsoleUtil(Plugin plugin, LogLevel logLevel) {
        this.messageUtil = new MessageUtil(plugin);
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
            console.sendMessage(getFormattedMessage("ERROR", "Exception: ", NamedTextColor.DARK_RED));
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
        return messageUtil.getConsoleComponent(color, fullMessage);
    }

    /**
     * Retrieves and sets the log level for the plugin from the configuration file.
     * It first checks for a JSON configuration file. If not found, it falls back
     * to the standard plugin configuration.
     *
     * @param plugin The plugin instance
     */
    private void getLogLevel(Plugin plugin) {
        final InputStream jsonConfig = plugin.getResource("config.json");
        String logLevel;
        if (jsonConfig == null) {
            logLevel = plugin.getConfig().getString("log-level");
        } else {
            final ConfigUtil configUtil = new ConfigUtil(plugin);
            logLevel = configUtil.getJsonMember("logLevel").getAsString();
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
}