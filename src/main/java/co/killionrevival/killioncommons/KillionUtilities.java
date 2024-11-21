package co.killionrevival.killioncommons;

import co.killionrevival.killioncommons.util.BroadcastUtil;
import co.killionrevival.killioncommons.util.ConfigUtil;
import co.killionrevival.killioncommons.util.MessageUtil;
import co.killionrevival.killioncommons.util.SkinUtil;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

@Getter
public class KillionUtilities {
    private final BroadcastUtil broadcastUtil;
    private final ConsoleUtil consoleUtil;
    private final MessageUtil messageUtil;
    private final SkinUtil skinUtil;
    private ConfigUtil configUtil;

    public KillionUtilities(final Plugin plugin) {
        this.consoleUtil = new ConsoleUtil(plugin);
        this.configUtil = new ConfigUtil(plugin);
        this.messageUtil = new MessageUtil(plugin);
        this.broadcastUtil = new BroadcastUtil(plugin);
        this.skinUtil = new SkinUtil(plugin);
    }

    public KillionUtilities(final Plugin plugin, Class<?> configClass) {
        this.consoleUtil = new ConsoleUtil(plugin);
        this.configUtil = new ConfigUtil(plugin, configClass);
        this.messageUtil = new MessageUtil(plugin);
        this.broadcastUtil = new BroadcastUtil(plugin);
        this.skinUtil = new SkinUtil(plugin);
    }
}
