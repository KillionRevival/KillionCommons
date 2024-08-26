package co.killionrevival.killioncommons.util;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleUtil {
    private final Boolean isDebugMode;
    private final MessageUtil messageUtil;
    private final Logger logger;
    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

    public ConsoleUtil(Plugin plugin) {
        this.logger = plugin.getLogger();
        this.messageUtil = new MessageUtil(plugin);

        final InputStream jsonConfig = plugin.getResource("config.json");
        if (jsonConfig == null) {
            this.isDebugMode = plugin.getConfig().getBoolean("debug-mode");
            return;
        }
        final ConfigUtil configUtil = new ConfigUtil(plugin);
        this.isDebugMode = configUtil.getJsonMember("plugin-prefix").getAsBoolean();
    }

    public void sendFormatMessage(String message) {
        console.sendMessage(messageUtil.formatMessage(message, true));
    }

    public void sendInfo(String message) {
        sendFormatMessage("&b" + message);
    }

    public void sendError(String message) {
        sendFormatMessage("&c" + message);
    }

    public void sendThrowable(Throwable t) {
        logger.log(Level.SEVERE, messageUtil.formatMessage("Exception: ", true), t);
    }

    public void sendSuccess(String message) {
        sendFormatMessage("§2" + message);
    }

    public void sendDebug(String message) {
        if (isDebugMode) {
            sendFormatMessage("&d" + message);
        }
    }
}