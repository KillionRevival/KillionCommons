package co.killionrevival.killioncommons.util.console;

import org.bukkit.plugin.Plugin;
import org.jline.utils.Log;

import co.killionrevival.killioncommons.util.ConfigUtil;
import co.killionrevival.killioncommons.util.MessageUtil;
import co.killionrevival.killioncommons.util.console.models.LogLevel;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleUtil {
    private LogLevel logLevel;
    private final MessageUtil messageUtil;
    private final Logger logger;

    /**
     * @param plugin The plugin instance
     */
    public ConsoleUtil(Plugin plugin) {
        this.logger = plugin.getLogger();
        this.messageUtil = new MessageUtil(plugin);

        getLogLevel(plugin);
    }

    public ConsoleUtil(Plugin plugin, LogLevel logLevel) {
        this.logger = plugin.getLogger();
        this.messageUtil = new MessageUtil(plugin);

        this.logger.setLevel(logLevel.getLevel());
    }

    /**
     * Logs an error message to the console with a red color.
     *
     * @param message The error message to log
     */
    public void sendError(String message) {
        logger.log(Level.SEVERE, getFormattedMessage(message, NamedTextColor.RED));
    }

    /**
     * Logs an error message to the console with a red color.
     * Also logs the throwable (exception) to the console.
     * 
     * @param message The error message to log
     * @param e The throwable to log
     */
    public void sendError(String message, Throwable e) {
        logger.log(Level.SEVERE, getFormattedMessage(message, NamedTextColor.RED), e);
    }

    /**
     * Logs a throwable (exception) to the console with a dark red color.
     *
     * @param t The throwable to log
     */
    public void sendThrowable(Throwable t) {
        logger.log(Level.SEVERE, getFormattedMessage("Exception: ", NamedTextColor.DARK_RED), t);
    }

    /**
     * Logs a warning message to the console with a gold color.
     *
     * @param message The warning message to log
     */
    public void sendWarning(String message) {
        logger.log(Level.WARNING, getFormattedMessage(message, NamedTextColor.GOLD));
    }

    /**
     * Logs a warning message to the console with a gold color.
     * Also logs the throwable (exception) to the console.
     * 
     * @param message The warning message to log
     * @param e The throwable to log
     */
    public void sendWarning(String message, Throwable e) {
        logger.log(Level.WARNING, getFormattedMessage(message, NamedTextColor.GOLD), e);
    }

    /**
     * Logs an informational message to the console with an aqua color.
     *
     * @param message The informational message to log
     */
    public void sendInfo(String message) {
        logger.log(Level.INFO, getFormattedMessage(message, NamedTextColor.AQUA));
    }

    /**
     * Logs a debug message to the console with a light purple color.
     *
     * @param message The debug message to log
     */
    public void sendDebug(String message) {
        logger.log(Level.FINER, getFormattedMessage(message, NamedTextColor.LIGHT_PURPLE));
    }

    /**
     * Logs a success message to the console with a dark green color.
     *
     * @param message The success message to log
     */
    public void sendSuccess(String message) {
        logger.log(Level.INFO, getFormattedMessage(message, NamedTextColor.DARK_GREEN));
    }

    /**
     * Formats a message with the specified color and prefix.
     *
     * @param message The message to format
     * @param color   The color to apply to the message
     * @return The formatted message
     */
    private String getFormattedMessage(String message, NamedTextColor color) {
        String colored = messageUtil.colorMessage(color, message);
        return messageUtil.formatMessage(colored, true);
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
            return;
        }
        final ConfigUtil configUtil = new ConfigUtil(plugin);
        logLevel = configUtil.getJsonMember("logLevel").getAsString();

        if (logLevel == null || logLevel.isEmpty()) {
            this.logLevel = LogLevel.INFO;
        } else {
            this.logLevel = LogLevel.valueOf(logLevel);
        }

        this.logger.setLevel(this.logLevel.getLevel());
    }
}