package co.killionrevival.killioncommons.listeners;

import co.killionrevival.killioncommons.KillionCommons;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class KillionGameplayListeners implements Listener {
    final static List<EntityDamageEvent.DamageCause> playerDamageCauses = List.of(
            EntityDamageEvent.DamageCause.ENTITY_ATTACK,
            EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);

    @EventHandler
    public void removeMobDropsUnlessKilledByPlayer(final EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Mob eventMob)) {
            return;
        }
        final EntityDamageEvent cause = event.getEntity().getLastDamageCause();
        if (cause == null) {
            KillionCommons.getUtil().getConsoleUtil().sendDebug(
                    "Mob died but had no last cause of damage, cannot prevent event." +
                            "\nMob: " + eventMob.getName());
            return;
        }

        final boolean wasPlayerKill = playerDamageCauses.contains(cause.getCause()) && eventMob.getKiller() != null;
        if (!wasPlayerKill) {
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }

    /**
     * This method disables any Strength effect greater than strength 1.
     */
    @EventHandler
    public void onlyAllowStrengthOne(final EntityPotionEffectEvent event) {
        if (!KillionCommons.getInstance().getConfig().getBoolean("disable-strength-2")) {
            return;
        }
        if (event.getNewEffect() == null) {
            return;
        }
        if (!event.getNewEffect().getType().equals(PotionEffectType.STRENGTH)) {
            return;
        }

        int strength = event.getNewEffect().getAmplifier();
        boolean enableOnBeacons = KillionCommons.getInstance().getConfig().getBoolean("enable-strength-2-on-beacons");

        if (strength <= 0) {
            return;
        }

        if (event.getCause() == EntityPotionEffectEvent.Cause.BEACON && enableOnBeacons) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            player.removePotionEffect(event.getNewEffect().getType());
        }

        event.setCancelled(true);
    }
}
