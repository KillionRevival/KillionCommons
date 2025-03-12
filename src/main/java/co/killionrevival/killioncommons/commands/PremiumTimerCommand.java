package co.killionrevival.killioncommons.commands;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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
  @CommandDescription("Request a player to drop their sword")
  public void premiumtimer(final CommandSender sender) {
    Player player = (Player) sender;

    LuckPerms lp = KillionCommons.getLuckperms();
    User user = lp.getUserManager().getUser(player.getUniqueId());

    if (user == null) {
      return;
    }

    for (Node node : user.getNodes()) {
      if (node instanceof InheritanceNode) {
        InheritanceNode groupNode = (InheritanceNode) node;

        if (groupNode.getGroupName().equalsIgnoreCase("premium")) {
          Duration expiry = node.getExpiryDuration();

          if (expiry == null) {
            player.sendMessage("You have permanent premium!");
            return;
          }

          long secondsLeft = expiry.getSeconds();

          if (secondsLeft > 0) {
            player.sendMessage(String.format("Time remaining: %s", this.getFormattedTime(secondsLeft)));
            return;
          }
        }
      }
    }

    player.sendMessage("You do not currently have premium. Use /buy to purchase it!");
  }

  // Format days, hours, minutes, and seconds
  private String getFormattedTime(long seconds) {
    long days = TimeUnit.SECONDS.toDays(seconds);
    seconds -= TimeUnit.DAYS.toSeconds(days);
    long hours = TimeUnit.SECONDS.toHours(seconds);
    seconds -= TimeUnit.HOURS.toSeconds(hours);
    long minutes = TimeUnit.SECONDS.toMinutes(seconds);
    seconds -= TimeUnit.MINUTES.toSeconds(minutes);
    long secondsLeft = TimeUnit.SECONDS.toSeconds(seconds);

    StringBuilder sb = new StringBuilder();
    if (days > 0) {
      sb.append(days).append(" day");
      if (days > 1) {
        sb.append("s");
      }
      sb.append(", ");
    }

    if (hours > 0) {
      sb.append(hours).append(" hour");
      if (hours > 1) {
        sb.append("s");
      }
      sb.append(", ");
    }

    if (minutes > 0) {
      sb.append(minutes).append(" minute");
      if (minutes > 1) {
        sb.append("s");
      }
      sb.append(", ");
    }

    sb.append(secondsLeft).append(" second");
    if (secondsLeft > 1) {
      sb.append("s");
    }
    return sb.toString();
  }

}
