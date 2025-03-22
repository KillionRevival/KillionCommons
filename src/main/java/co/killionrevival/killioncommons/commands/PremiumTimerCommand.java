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

        final Duration premiumTimer = getRemainingPremiumTimer(player);

        if (premiumTimer == null) {
            player.sendMessage("You do not currently have premium. Use /buy to purchase it!");
            return;
        }
        else if (premiumTimer == Duration.ofDays(Long.MAX_VALUE)) {
            player.sendMessage("You have permanent premium!");
            return;
        }

        player.sendMessage(
                "Premium expires on: "
                        + DateUtil.getHumanReadableDateTimeString(Instant.now().plusSeconds(premiumTimer.toSeconds()))
                        + " (" + DateUtil.getTimeStringFromDuration(premiumTimer)+ ")"
        );
    }

    /**
     * Get the remaining time of a player's premium status
     * @param player The player to check
     * @return The remaining time of the player's premium status, or null if the player does not have premium, or a duration of
     *         Long.MAX_VALUE days if the player has permanent premium
     */
    public static Duration getRemainingPremiumTimer(final Player player) {
        if (!player.hasPermission("group.premium")) {
            return null;
        }

        LuckPerms lp = KillionCommons.getLuckperms();
        User user = lp.getUserManager().getUser(player.getUniqueId());

        if (user == null) {
            return null;
        }

        for (Node node : user.getNodes()) {
            if (!(node instanceof InheritanceNode groupNode)) {
                continue;
            }
            if (groupNode.getGroupName().equalsIgnoreCase("premium")) {
                Duration expiry = node.getExpiryDuration();

                if (expiry == null) {
                    return Duration.ofDays(Long.MAX_VALUE);
                }

                long secondsLeft = expiry.getSeconds();
                return Duration.ofSeconds(secondsLeft);
            }
        }

        // user has group.premium but no group with the name "premium" ??????
        return null;
    }
}
