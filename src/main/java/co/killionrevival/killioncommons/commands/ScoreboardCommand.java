package co.killionrevival.killioncommons.commands;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.scoreboard.KillionScoreboardManager;
import co.killionrevival.killioncommons.scoreboard.ScoreboardAddition;
import co.killionrevival.killioncommons.util.MessageUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.paper.util.sender.PlayerSource;

import java.util.List;

@Command("panel")
public class ScoreboardCommand {
    private final MessageUtil messageUtil = KillionCommons.getUtil().getMessageUtil();
    private final KillionScoreboardManager scoreboardManager;

    public ScoreboardCommand(final KillionScoreboardManager manager) {
        this.scoreboardManager = manager;
    }

    @Suggestions("components")
    public List<String> components() {
        return scoreboardManager.getAdditions().values().stream().map(ScoreboardAddition::componentName).toList();
    }

    @Command(value = "toggle [component]", requiredSender = PlayerSource.class)
    public void toggleCommand(
            final Player player,
            @Argument(
                    value = "component",
                    description = "Toggle a specific component of the scoreboard.",
                    suggestions = "components"
            ) final String component
    ) {
        if (component == null || component.isEmpty()) {
            scoreboardManager.toggleScoreboardForPlayer(player);
            messageUtil.sendPrefixMessage(player, "Toggled scoreboard display. It is now " + (scoreboardManager.isScoreboardEnabledForPlayer(player) ? "enabled" : "disabled") + ".");
            return;
        }

        final ScoreboardAddition addition = scoreboardManager.getAdditions().values().stream().filter(
                scoreboardAddition -> scoreboardAddition.componentName().equalsIgnoreCase(component)
        ).findFirst().orElse(null);
        if (addition == null) {
            messageUtil.sendPrefixMessage(player, "Invalid component.");
            return;
        }

        scoreboardManager.toggleAdditionForPlayer(player, component);
        messageUtil.sendPrefixMessage(player, "Toggled display of the " + component + " component. It is now " + (scoreboardManager.isAdditionEnabledForPlayer(player, component) ? "enabled" : "disabled") + ".");
    }
}
