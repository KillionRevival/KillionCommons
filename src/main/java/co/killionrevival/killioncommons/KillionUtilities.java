package co.killionrevival.killioncommons;

import co.killionrevival.killioncommons.npc.NpcManager;
import co.killionrevival.killioncommons.util.BroadcastUtil;
import co.killionrevival.killioncommons.util.ConsoleUtil;
import co.killionrevival.killioncommons.util.MessageUtil;
import co.killionrevival.killioncommons.util.SkinUtil;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

@Getter
public class KillionUtilities {
    private final BroadcastUtil broadcastUtil;
    private final ConsoleUtil consoleUtil;
    private final MessageUtil messageUtil;
    private final SkinUtil skinUtil;

    public KillionUtilities(final Plugin plugin) {
        this.messageUtil = new MessageUtil(plugin);
        this.broadcastUtil = new BroadcastUtil(plugin);
        this.consoleUtil = new ConsoleUtil(plugin);
        this.skinUtil = new SkinUtil(plugin);
    }
}
