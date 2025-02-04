package co.killionrevival.killioncommons.commands;

import co.killionrevival.killioncommons.KillionCommons;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.paper.util.sender.Source;

@Command("commons")
public class CommonsCommand {
    @Command("reload")
    public void reloadConfig(
            final Source sender
    ) {
        KillionCommons.reloadCustomConfig();
        KillionCommons.getUtil().getMessageUtil().sendPrefixMessage(sender.source(), "Config reloaded!");
    }
}
