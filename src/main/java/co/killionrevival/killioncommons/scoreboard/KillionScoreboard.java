package co.killionrevival.killioncommons.scoreboard;

import co.killionrevival.killioncommons.KillionCommons;
import fr.mrmicky.fastboard.adventure.FastBoard;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class KillionScoreboard {
    private static final KillionScoreboardManager manager = KillionCommons.getInstance().getScoreboardManager();
    private FastBoard fastBoard;

    private boolean display;

    protected HashMap<String, ScoreboardAddition> additionMap;

    public KillionScoreboard(Player player) {
        this.fastBoard = new FastBoard(player);
        display = true;
        additionMap = new HashMap<>();
        manager.additions.values().forEach(
                addition -> {
                    final ScoreboardAddition clone = addition.clone();
                    clone.setPlayer(player);
                    clone.initWithPlayer(player);
                    additionMap.put(addition.componentName(), clone);
                }
        );
    }

    /**
     * Toggle the display of a scoreboard addition for a player
     * @param additionName The name of the addition to toggle
     */
    public void toggleAddition(final String additionName) {
        additionMap.get(additionName).toggle();
    }

    /**
     * Enable a scoreboard addition for a player
     * @param additionName The name of the addition to enable
     */
    public void enableAddition(final String additionName) {
        additionMap.get(additionName).setEnabled(true);
    }

    /**
     * Disable a scoreboard addition for a player
     * @param additionName The name of the addition to disable
     */
    public void disableAddition(final String additionName) {
        additionMap.get(additionName).setEnabled(false);
    }

    /**
     * Render the scoreboard for a player
     */
    public void updateScoreboardDisplay() {
        final List<Component> boardLines = new ArrayList<>(List.of()); // Not sure what to put here yet!

        additionMap.values().stream().filter(ScoreboardAddition::isEnabled).forEach(
                addition -> boardLines.addAll(addition.getLinesToAdd())
        );

        fastBoard.updateLines(boardLines);
    }
}
