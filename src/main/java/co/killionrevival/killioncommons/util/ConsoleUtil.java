package co.killionrevival.killioncommons.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

@Deprecated
public class ConsoleUtil {
    private final Boolean isDebugMode;
    private final MessageUtil messageUtil;
    private final Logger logger;
    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

    public ConsoleUtil(Plugin plugin) {
        this.isDebugMode = plugin.getConfig().getBoolean("debug-mode");
        this.logger = plugin.getLogger();
        this.messageUtil = new MessageUtil(plugin);
    }

    public void sendFormatMessage(String message) {
        console.sendMessage(messageUtil.formatMessage(message, true));
    }

    public void sendInfo(String message) {
        sendFormatMessage(messageUtil.colorMessage(ChatColor.AQUA, message));
    }

    public void sendWarning(String message) {
        sendFormatMessage(messageUtil.colorMessage(ChatColor.GOLD, message));
    }

    public void sendError(String message) {
        sendFormatMessage(messageUtil.colorMessage(ChatColor.RED, message));
    }

    public void sendThrowable(Throwable t) {
        logger.log(Level.SEVERE, messageUtil.formatMessage("Exception: ", true), t);
    }

    public void sendSuccess(String message) {
        sendFormatMessage(messageUtil.colorMessage(ChatColor.DARK_GREEN, message));
    }

    public void sendDebug(String message) {
        if (isDebugMode) {
            sendFormatMessage(messageUtil.colorMessage(ChatColor.LIGHT_PURPLE, message));
        }
    }
}