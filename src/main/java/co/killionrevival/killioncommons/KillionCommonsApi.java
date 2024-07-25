package co.killionrevival.killioncommons;

import co.killionrevival.killioncommons.util.BroadcastUtil;
import co.killionrevival.killioncommons.util.ConsoleUtil;
import co.killionrevival.killioncommons.util.MessageUtil;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

@Getter
public class KillionCommonsApi {
    private final BroadcastUtil broadcastUtil;
    private final ConsoleUtil consoleUtil;
    private final MessageUtil messageUtil;

    public KillionCommonsApi(final Plugin plugin) {
        this.messageUtil = new MessageUtil(plugin);
        this.broadcastUtil = new BroadcastUtil(plugin);
        this.consoleUtil = new ConsoleUtil(plugin);
    }
}
