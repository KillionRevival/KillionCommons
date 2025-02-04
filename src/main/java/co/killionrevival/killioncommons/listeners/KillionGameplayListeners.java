package co.killionrevival.killioncommons.listeners;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.config.KillionCommonsConfig;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.damage.DamageSource;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class KillionGameplayListeners implements Listener {
    private final static ConsoleUtil logger = KillionCommons.getUtil().getConsoleUtil();
    private final static KillionCommonsConfig config = KillionCommons.getCustomConfig();
    final static List<EntityDamageEvent.DamageCause> playerDamageCauses = List.of(
            EntityDamageEvent.DamageCause.ENTITY_ATTACK,
            EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK,
            EntityDamageEvent.DamageCause.PROJECTILE
    );
    final static Set<Material> swords = Set.of(
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD
    );
    final static Set<UUID> playersBlocking = new HashSet<>();

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    public void onPlayerDamageBySword(final EntityDamageEvent event) {
        if (!config.isSwordBlocking()) {
            return;
        }
        if (!(event.getEntity() instanceof final Player player)) {
            return;
        }
        final DamageSource source = event.getDamageSource();
        if (!(source.getDirectEntity() instanceof Player damagingPlayer)) {
            return;
        }
        logger.sendDebug("Player " + player.getName() + " was damaged by " + damagingPlayer.getName());
        if (playersBlocking.contains(player.getUniqueId())) {
            logger.sendDebug("Player " + player.getName() + " was blocking, reducing damage");
            event.setDamage(event.getDamage() / 2);
        }
    }

    @EventHandler
    public void onPlayerFakeBlock(final PlayerInteractEvent event) {
        if (!config.isSwordBlocking() || (event.getHand() != null && !event.getHand().equals(EquipmentSlot.HAND))) {
            return;
        }
        final ItemStack stack = event.getItem();
        if (stack == null || !swords.contains(stack.getType())) {
            return;
        }
        final UUID playerId = event.getPlayer().getUniqueId();
        if (playersBlocking.contains(playerId)) {
            logger.sendError("Somehow " + playerId + " is already blocking, this should not happen.");
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            playersBlocking.add(event.getPlayer().getUniqueId());
            logger.sendDebug(event.getPlayer() + " is blocking with a sword");
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (playersBlocking.contains(playerId)) {
                        final Player player = Bukkit.getPlayer(playerId);
                        if (player == null || !player.isOnline()) {
                            playersBlocking.remove(playerId);
                            logger.sendDebug("Player was null, they are no longer blocking (offline?)");
                            return;
                        }
                        if (player.hasActiveItem()) {
                            logger.sendDebug("Player is blocking");
                            return;
                        }
                        playersBlocking.remove(playerId);
                        logger.sendDebug("Player is no longer blocking");
                        this.cancel();
                    }
                }
            }.runTaskTimer(KillionCommons.getInstance(), 0, 1);
        }
    }

    /**
     * This method adds the consumable tag to any sword that is picked up by a player.
     */
    @EventHandler
    public void changeBlockingBehavior(final PlayerInventorySlotChangeEvent event) {
        final Inventory inventory = event.getPlayer().getOpenInventory().getInventory(event.getRawSlot());
        if (inventory == null) {
            return;
        }
        final boolean addConsume = config.isSwordBlocking();
        final ItemStack newItem = event.getNewItemStack();
        if (!swords.contains(newItem.getType())) {
            return;
        }

        final ItemStack modifiedItem = addOrRemoveFakeBlock(newItem, addConsume);
        if (newItem == modifiedItem) {
            return;
        }
        inventory.setItem(event.getSlot(), modifiedItem);
    }

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

    private ItemStack addOrRemoveFakeBlock(final ItemStack stack, final boolean add) {
        final net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

        if (add && nmsStack.has(DataComponents.CONSUMABLE) || (!add && !nmsStack.has(DataComponents.CONSUMABLE))) {
            return stack;
        }

        if (add) {
            final Consumable.Builder consumableBuilder = Consumable.builder();
            consumableBuilder.consumeSeconds(Integer.MAX_VALUE);
            consumableBuilder.animation(ItemUseAnimation.BLOCK);
            consumableBuilder.sound(SoundEvents.CROSSBOW_LOADING_END);
            consumableBuilder.hasConsumeParticles(false);
            nmsStack.set(DataComponents.CONSUMABLE, consumableBuilder.build());
        } else {
            nmsStack.remove(DataComponents.CONSUMABLE);
        }
        return CraftItemStack.asBukkitCopy(nmsStack);
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
