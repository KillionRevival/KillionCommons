package co.killionrevival.killioncommons.util.logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import co.killionrevival.killioncommons.config.Config;
import co.killionrevival.killioncommons.config.models.DefaultConfig;
import co.killionrevival.killioncommons.util.MessageUtil;
import co.killionrevival.killioncommons.util.logger.models.LogLevel;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtil {
    private LogLevel myLoggingLevel;
    private Level level;
    private final MessageUtil messageUtil;
    private final Logger logger;

    public LoggerUtil(JavaPlugin plugin, Config config) {
        this.myLoggingLevel = ((DefaultConfig) config.getConfig()).getLogLevel();
        this.logger = plugin.getLogger();
        this.messageUtil = new MessageUtil(plugin);

        this.setLogLevel(myLoggingLevel);
    }

    public void sendError(String message) {
        logger.log(Level.SEVERE, getFormattedMessage(message, ChatColor.RED));
    }

    public void sendThrowable(Throwable t) {
        logger.log(Level.SEVERE, getFormattedMessage("Exception: ", ChatColor.DARK_RED), t);
    }

    public void sendWarning(String message) {
        logger.log(Level.WARNING, getFormattedMessage(message, ChatColor.GOLD));
    }

    public void sendInfo(String message) {
        logger.log(Level.INFO, getFormattedMessage(message, ChatColor.AQUA));
    }

    public void sendDebug(String message) {
        logger.log(Level.FINER, getFormattedMessage(message, ChatColor.LIGHT_PURPLE));
    }

    public void sendSuccess(String message) {
        logger.log(Level.INFO, getFormattedMessage(message, ChatColor.DARK_GREEN));
    }

    private String getFormattedMessage(String message, ChatColor color) {
        String colored = messageUtil.colorMessage(color, message);
        return messageUtil.formatMessage(colored, true);
    }

    private void setLogLevel(LogLevel myLevel) {
        this.level = myLevel.getLevel();
        this.logger.setLevel(this.level);
    }
}