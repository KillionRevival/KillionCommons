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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Command("panel")
public class ScoreboardCommand {
    private final MessageUtil messageUtil = KillionCommons.getUtil().getMessageUtil();
    private final KillionScoreboardManager scoreboardManager;

    public ScoreboardCommand(final KillionScoreboardManager manager) {
        this.scoreboardManager = manager;
    }

    @Suggestions("components")
    public List<String> components() {
        final Set<String> names = new HashSet<>();
        for (final Map<String, ScoreboardAddition> map : scoreboardManager.getAdditions().values()) {
            names.addAll(map.keySet());
        }
        return names.stream().toList();
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

        final ScoreboardAddition addition = scoreboardManager
                .getScoreboardMap()
                .get(player.getUniqueId())
                .getAdditionMap()
                .get(component);
        if (addition == null) {
            messageUtil.sendPrefixMessage(player, "Invalid component.");
            return;
        }

        scoreboardManager.toggleAdditionForPlayer(player, component);
        messageUtil.sendPrefixMessage(player, "Toggled display of the " + component + " component. It is now " + (scoreboardManager.isAdditionEnabledForPlayer(player, component) ? "enabled" : "disabled") + ".");
    }
}
