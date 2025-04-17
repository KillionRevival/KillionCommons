package co.killionrevival.killioncommons.listeners;

import co.killionrevival.killioncommons.compat.WorldGuardCompat;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class WorldGuardListeners implements Listener {

    @EventHandler
    public void onXpGain(PlayerExpChangeEvent event) {
        if (!WorldGuardCompat.enabled) {
            return;
        }
        final Player player = event.getPlayer();
        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionQuery query = container.createQuery();
        final Double xpMultiplier = query.queryValue(
                localPlayer.getLocation(),
                localPlayer,
                WorldGuardCompat.XP_MULT
        );
        if (xpMultiplier == null) {
            return;
        }
        event.setAmount((int) (event.getAmount() * xpMultiplier));
    }
}
