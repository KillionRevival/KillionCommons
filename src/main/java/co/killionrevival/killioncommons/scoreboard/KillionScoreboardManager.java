package co.killionrevival.killioncommons.scoreboard;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.scoreboard.exceptions.DuplicateComponentNameException;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class KillionScoreboardManager {
    private static final ConsoleUtil logger = KillionCommons.getUtil().getConsoleUtil();

    final Map<String, Plugin> additionNameToPluginMap;

    @Getter
    final Map<UUID, KillionScoreboard> scoreboardMap;
    @Getter
    final Map<Plugin, Map<String, ScoreboardAddition>> additions;

    public KillionScoreboardManager() {
        scoreboardMap = new HashMap<>();
        additionNameToPluginMap = new TreeMap<>();
        additions = new HashMap<>();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
                KillionCommons.getInstance(),
                new UpdateScoreboardTask(),
                0,
                20
        );
    }

    /**
     * Register a new scoreboard addition for your plugin.
     * If it is registered outside of startup, it will be added to all current scoreboards.
     * If your plugin has not registered an addition before, a new map will be initialized with the component name as the key
     *
     * @param plugin The plugin registering the addition
     * @param addition The addition to register
     */
    public void registerAddition(
            final Plugin plugin,
            final ScoreboardAddition addition
    ) throws DuplicateComponentNameException {
        if (additionNameToPluginMap.containsKey(addition.componentName())) {
            throw new DuplicateComponentNameException(addition.componentName() + " is already registered by " + additionNameToPluginMap.get(addition.componentName()).getName());
        }

        if (!additions.containsKey(plugin)) {
            additions.computeIfAbsent(plugin, k -> {
                logger.sendDebug("Initializing new addition map for " + plugin.getName());
                additionNameToPluginMap.put(addition.componentName(), plugin);
                return Map.of(addition.componentName(), addition);
            });
            return;
        }

        additions.get(plugin).put(addition.componentName(), addition);
        additionNameToPluginMap.put(addition.componentName(), plugin);

        if (!scoreboardMap.isEmpty()) {
            scoreboardMap.values().forEach(
                    board -> board.additionMap.put(addition.componentName(), addition)
            );
        }
    }

    /**
     * Remove all scoreboard additions for a plugin from the manager.
     * If it is removed outside of shutdown, it will be removed from all current scoreboards.
     * @param plugin The plugin removing the addition
     */
    public void removeAdditions(final Plugin plugin) {
        final Map<String, ScoreboardAddition> addition = additions.remove(plugin);
        if (addition == null) {
            return;
        }
        addition.keySet().forEach(additionNameToPluginMap::remove);
        if (!scoreboardMap.isEmpty()) {
            scoreboardMap.values().forEach(
                    board -> addition.keySet().forEach(board.additionMap::remove)
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
