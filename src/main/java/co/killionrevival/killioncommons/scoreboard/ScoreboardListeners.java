package co.killionrevival.killioncommons.scoreboard;

import co.killionrevival.killioncommons.KillionCommons;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListeners implements Listener {
    final KillionCommons instance = KillionCommons.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        instance.getScoreboardManager().startScoreboardDisplay(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        instance.getScoreboardManager().stopScoreboardDisplay(e.getPlayer());
    }
}
