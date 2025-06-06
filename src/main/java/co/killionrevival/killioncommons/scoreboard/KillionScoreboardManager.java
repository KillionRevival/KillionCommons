package co.killionrevival.killioncommons.scoreboard;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillionScoreboardManager {
    private static final ConsoleUtil logger = KillionCommons.getUtil().getConsoleUtil();

    final Map<UUID, KillionScoreboard> scoreboardMap;
    @Getter
    final Map<Plugin, ScoreboardAddition> additions;

    public KillionScoreboardManager() {
        scoreboardMap = new HashMap<>();
        additions = new HashMap<>();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
                KillionCommons.getInstance(),
                new UpdateScoreboardTask(),
                0,
                20
        );
    }

    /**
     * Register a scoreboard addition for your plugin.
     * If it is registered outside of startup, it will be added to all current scoreboards.
     * @param plugin The plugin registering the addition
     * @param addition The addition to register
     */
    public void registerAddition(
            final Plugin plugin,
            final ScoreboardAddition addition
    ) {
        additions.put(plugin, addition);

        if (!scoreboardMap.isEmpty()) {
            scoreboardMap.values().forEach(
                    board -> board.additionMap.put(addition.componentName(), addition)
            );
        }
    }

    /**
     * Remove a scoreboard addition from the manager.
     * If it is removed outside of shutdown, it will be removed from all current scoreboards.
     * @param plugin The plugin removing the addition
     */
    public void removeAdditions(final Plugin plugin) {
        final ScoreboardAddition addition = additions.remove(plugin);

        if (!scoreboardMap.isEmpty()) {
            scoreboardMap.values().forEach(
                    board -> board.additionMap.remove(addition.componentName())
            );
        }
    }

    /**
     * Toggle a scoreboard addition for a player
     * @param player The player to toggle the addition for
     * @param additionName The name of the addition to toggle
     */
    public void toggleAdditionForPlayer(
            final Player player,
            final String additionName
    ) {
        if (scoreboardMap.containsKey(player.getUniqueId())) {
            scoreboardMap.get(player.getUniqueId()).toggleAddition(additionName);
        }
    }

    /**
     * Check if a scoreboard is enabled for a player
     * @param player The player to check for
     * @return True if the scoreboard is enabled, false otherwise
     */
    public boolean isScoreboardEnabledForPlayer(final Player player) {
        return scoreboardMap.containsKey(player.getUniqueId());
    }

    /**
     * Check if a scoreboard addition is enabled for a player
     * @param player The player to check for
     * @param additionName The name of the addition to check
     * @return True if the addition is enabled, false otherwise
     */
    public boolean isAdditionEnabledForPlayer(
            final Player player,
            final String additionName
    ) {
        return scoreboardMap.containsKey(player.getUniqueId()) &&
                scoreboardMap.get(player.getUniqueId()).isAdditionEnabled(additionName);
    }

    /**
     * Toggle the scoreboard display for a player
     * @param player The player to toggle the scoreboard for
     */
    public void toggleScoreboardForPlayer(final Player player) {
        if (scoreboardMap.containsKey(player.getUniqueId())) {
            stopScoreboardDisplay(player);
            return;
        }
        startScoreboardDisplay(player);
    }

    protected void startScoreboardDisplay(Player player) {
        logger.sendDebug("Starting scoreboard display for " + player.getName() + " in 2 seconds");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    scoreboardMap.put(player.getUniqueId(), new KillionScoreboard(player));
                }
            }
        }.runTaskLater(KillionCommons.getInstance(), 40);
    }

    protected void stopScoreboardDisplay(Player player) {
        logger.sendDebug("Stopping scoreboard display for " + player.getName());
        KillionScoreboard board = scoreboardMap.remove(player.getUniqueId());
        if (board != null) {
            board.getFastBoard().delete();
        }
    }

    private class UpdateScoreboardTask implements Runnable {
        @Override
        public void run() {
            scoreboardMap.forEach((player, entry) -> entry.updateScoreboardDisplay());
        }
    }
}
