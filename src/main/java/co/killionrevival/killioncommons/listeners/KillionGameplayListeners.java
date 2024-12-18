package co.killionrevival.killioncommons.listeners;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.config.KillionCommonsConfig;
import io.papermc.paper.event.inventory.PaperInventoryMoveItemEvent;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class KillionGameplayListeners implements Listener {
    private final static KillionCommonsConfig config = KillionCommons.getCustomConfig();
    final static List<EntityDamageEvent.DamageCause> playerDamageCauses = List.of(
            EntityDamageEvent.DamageCause.ENTITY_ATTACK,
            EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK,
            EntityDamageEvent.DamageCause.PROJECTILE
    );

    /**
     * This method adds the Curse of Vanishing to any item that is enchanted with Mending.
     */
    @EventHandler
    public void addCurseOfVanishingToAnythingEnchantedWithMendingWhenInventoryChange(final PlayerInventorySlotChangeEvent event) {
        final Inventory inventory = event.getPlayer().getOpenInventory().getInventory(event.getRawSlot());
        if (inventory == null) {
            return;
        }
        final ItemStack newItem = event.getNewItemStack();
        final ItemStack newItemWithCurse = addMendingEnchantToItemIfNotExists(newItem);
        if (newItem == newItemWithCurse) {
            return;
        }

        inventory.setItem(event.getSlot(), newItemWithCurse);
    }


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
        if (!config.isDisableStrength2()) {
            return;
        }
        if (event.getNewEffect() == null) {
            return;
        }
        if (!event.getNewEffect().getType().equals(PotionEffectType.STRENGTH)) {
            return;
        }

        int strength = event.getNewEffect().getAmplifier();
        boolean enableOnBeacons = config.isEnableStrength2OnBeacons();

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

    private ItemStack addMendingEnchantToItemIfNotExists(final ItemStack stack) {
        if (stack == null || !stack.containsEnchantment(Enchantment.MENDING)) {
            return stack;
        }
        if (stack.containsEnchantment(Enchantment.VANISHING_CURSE)) {
            return stack;
        }
        final ItemStack newStack = stack.clone();
        newStack.addEnchantment(Enchantment.VANISHING_CURSE, 1);
        return newStack;
    }
}
