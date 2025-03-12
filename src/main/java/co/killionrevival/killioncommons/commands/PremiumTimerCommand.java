package co.killionrevival.killioncommons.commands;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import co.killionrevival.killioncommons.util.DateUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.paper.util.sender.PlayerSource;

import co.killionrevival.killioncommons.KillionCommons;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;

public class PremiumTimerCommand {

    @Command(value = "premiumtimer", requiredSender = PlayerSource.class)
    @CommandDescription("Get the amount of time left on your premium subscription")
    public void premiumtimer(final CommandSender sender) {
        Player player = (Player) sender;

        LuckPerms lp = KillionCommons.getLuckperms();
        User user = lp.getUserManager()
                      .getUser(player.getUniqueId());

        if (user == null) {
            return;
        }

        for (Node node : user.getNodes()) {
            if (node instanceof InheritanceNode) {
                InheritanceNode groupNode = (InheritanceNode) node;

                if (groupNode.getGroupName()
                             .equalsIgnoreCase("premium")) {
                    Duration expiry = node.getExpiryDuration();

                    if (expiry == null) {
                        player.sendMessage("You have permanent premium!");
                        return;
                    }

                    long secondsLeft = expiry.getSeconds();

                    if (secondsLeft > 0) {
                        player.sendMessage(
                            "Premium expires on: "
                                + DateUtil.getHumanReadableDateTimeString(Instant.now().plusSeconds(secondsLeft))
                                + " (" + DateUtil.getTimeStringFromDuration(expiry)+ ")"
                        );
                        return;
                    }
                }
            }
        }

        player.sendMessage("You do not currently have premium. Use /buy to purchase it!");
    }
}
