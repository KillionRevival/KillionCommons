package co.killionrevival.killioncommons.listeners;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.config.KillionCommonsConfig;
import co.killionrevival.killioncommons.util.EnchantmentType;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import uk.antiperson.stackmob.events.StackDropItemEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class KillionGameplayListeners implements Listener {
    private final static ConsoleUtil logger = KillionCommons.getUtil().getConsoleUtil();
    private final static KillionCommonsConfig config = KillionCommons.getCustomConfig();

    final double REDUCTION_PER_ARMOR_POINT = 0.04;
    final double REDUCTION_PER_RESISTANCE_LEVEL = 0.2;

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
    private final Set<EntityDamageEvent.DamageCause> ARMOR_IGNORING_CAUSES = Set.of(
            EntityDamageEvent.DamageCause.FIRE_TICK,
            EntityDamageEvent.DamageCause.SUFFOCATION,
            EntityDamageEvent.DamageCause.DROWNING,
            EntityDamageEvent.DamageCause.STARVATION,
            EntityDamageEvent.DamageCause.FALL,
            EntityDamageEvent.DamageCause.VOID,
            EntityDamageEvent.DamageCause.CUSTOM,
            EntityDamageEvent.DamageCause.MAGIC,
            EntityDamageEvent.DamageCause.WITHER,  // From 1.9
            EntityDamageEvent.DamageCause.FLY_INTO_WALL,
            EntityDamageEvent.DamageCause.DRAGON_BREATH // In 1.19 FIRE bypasses armor, but it doesn't in 1.8 so we don't add it here
    );

    final static Set<UUID> playersBlocking = new HashSet<>();

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamageBySword(final EntityDamageByEntityEvent event) {
        if (!config.isSwordBlocking()) {
            return;
        }

        if (!(event.getEntity() instanceof final Player player) || !(event.getDamager() instanceof final Player damagingPlayer)) {
            return;
        }

        if (playersBlocking.contains(player.getUniqueId())) {
            logger.sendDebug("Player " + player.getName() + " was blocking, reducing damage");
            double baseDamage = event.getDamage();
            if (baseDamage > 0) {
                double finalDamageHalved = baseDamage - 1;
                finalDamageHalved *= 0.5;
                if (finalDamageHalved < 0) {
                    finalDamageHalved = 0;
                }
                logger.sendDebug("New Base Damage: " + finalDamageHalved);
                event.setDamage(EntityDamageEvent.DamageModifier.BASE, finalDamageHalved);
            }
            logger.sendDebug("Player " + player.getName() + " was damaged by " + damagingPlayer.getName());
        }

        // OCM Old Armor Strength Calculations
        // adapted from
        // https://github.com/kernitus/BukkitOldCombatMechanics/blob/master/src/main/kotlin/kernitus/plugin/OldCombatMechanics/module/ModuleOldArmourStrength.kt
        final HashMap<EntityDamageEvent.DamageModifier, Double> validModifiers =
                new HashMap<>(Arrays.stream(EntityDamageEvent.DamageModifier.values()).filter(event::isApplicable)
                      .collect(Collectors.toMap(
                              modifier -> modifier, event::getDamage
                      )));
        calculateDefenceDamageReduction((LivingEntity) event.getEntity(), validModifiers, event.getCause());

        validModifiers.forEach(event::setDamage);

        logger.sendDebug("Final Damage: " + event.getFinalDamage());
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
                            //logger.sendDebug("Player is blocking");
                            return;
                        }
                        playersBlocking.remove(playerId);
                        //logger.sendDebug("Player is no longer blocking");
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

    @EventHandler
    public void removeMendingFromItemsWhenPlayerDies(final PlayerDeathEvent event) {
        final List<ItemStack> drops = event.getDrops();
        for (ItemStack drop : drops) {
            final ItemStack dropNoMending = removeMendingFromItemIfExists(drop);
            if (drop == dropNoMending) {
                continue;
            }
            drops.set(drops.indexOf(drop), dropNoMending);
        }
    }

    /**
     * This method adds the Curse of Vanishing to any item that is enchanted with Mending.
     */
    @EventHandler
    public void removeCurseOfVanishingFromMendingItemWhenInventoryChange(final PlayerInventorySlotChangeEvent event) {
        final Inventory inventory = event.getPlayer().getOpenInventory().getInventory(event.getRawSlot());
        if (inventory == null) {
            return;
        }
        final ItemStack newItem = event.getNewItemStack();
        final ItemStack newItemNoCurse = removeCurseFromMendingItemIfExists(newItem);
        if (newItem == newItemNoCurse) {
            return;
        }

        inventory.setItem(event.getSlot(), newItemNoCurse);
    }


    @EventHandler
    public void removeMobDropsUnlessKilledByPlayer(final StackDropItemEvent stackEvent) {
        final EntityDeathEvent event = stackEvent.getOriginalEvent();
        final boolean wasPlayerKill = wasValidPlayerKill(event);
        if (!wasPlayerKill) {
            stackEvent.getDrops().clear();
        }
    }

    @EventHandler
    public void removeMobDropsUnlessKilledByPlayer(final EntityDeathEvent event) {
        final boolean wasPlayerKill = wasValidPlayerKill(event);
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

    private ItemStack removeCurseFromMendingItemIfExists(final ItemStack stack) {
        if (stack == null || !stack.containsEnchantment(Enchantment.VANISHING_CURSE)) {
            return stack;
        }
        final ItemStack newStack = stack.clone();
        newStack.removeEnchantment(Enchantment.VANISHING_CURSE);
        return newStack;
    }

    private ItemStack removeMendingFromItemIfExists(final ItemStack stack) {
        if (stack == null || !stack.containsEnchantment(Enchantment.MENDING)) {
            return stack;
        }
        final ItemStack newStack = stack.clone();
        newStack.removeEnchantment(Enchantment.MENDING);
        return newStack;
    }

    private double calculateArmorEnchantmentReductionFactor(
            List<ItemStack> armorEquipped,
            EntityDamageEvent.DamageCause cause
    ) {
        int totalEpf = 0;
        for (ItemStack item : armorEquipped) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            for (EnchantmentType enchantmentType : EnchantmentType.values()) {
                if (!enchantmentType.protectsAgainst(cause)) {
                    continue;
                }
                int enchantmentLevel = item.getEnchantmentLevel(enchantmentType.enchantment);
                if (enchantmentLevel > 0) {
                    totalEpf += enchantmentType.getEpf(enchantmentLevel);
                }
            }
        }

        // Cap at 25
        totalEpf = (int) Math.min(25.0, totalEpf);

        // Multiply by random value between 50% and 100%, then round up
        double multiplier = ThreadLocalRandom.current().nextDouble(0.5, 1.0);
        totalEpf = (int) Math.ceil(totalEpf * multiplier);

        // Cap at 20
        totalEpf = (int) Math.min(20.0, totalEpf);

        return REDUCTION_PER_ARMOR_POINT  * totalEpf;
    }

    @SuppressWarnings("deprecation")
    private void calculateDefenceDamageReduction(
            final LivingEntity damagedEntity,
            final Map<EntityDamageEvent.DamageModifier, Double> modifiers,
            final EntityDamageEvent.DamageCause damageCause
    ) {
        final AttributeInstance armorInstance = damagedEntity.getAttribute(Attribute.ARMOR);
        double armorPoints = armorInstance != null ? armorInstance.getValue() : 0.0;
        // Make sure we don't go over 100% protection
        double armorReductionFactor = Math.min(1.0, armorPoints * REDUCTION_PER_ARMOR_POINT);

        // applyArmorModifier() calculations from NMS
        // Apply armor damage reduction after hard hat (wearing helmet & hit by block) and blocking reduction
        double currentDamage = modifiers.getOrDefault(EntityDamageEvent.DamageModifier.BASE, 0.0) +
                modifiers.getOrDefault(EntityDamageEvent.DamageModifier.HARD_HAT, 0.0) +
                modifiers.getOrDefault(EntityDamageEvent.DamageModifier.BLOCKING, 0.0) ;

        if (modifiers.containsKey(EntityDamageEvent.DamageModifier.ARMOR)) {
            double armorReduction = 0.0;
            // If the damage cause does not ignore armor
            // If the block they are in is a stalagmite, also ignore armor
            if (!ARMOR_IGNORING_CAUSES.contains(damageCause) &&
                !(damageCause == EntityDamageEvent.DamageCause.CONTACT && damagedEntity.getLocation().getBlock().getType() == Material.POINTED_DRIPSTONE)
            ) {
                armorReduction = currentDamage * -armorReductionFactor;
            }
            modifiers.put(EntityDamageEvent.DamageModifier.ARMOR, armorReduction);
            currentDamage += armorReduction;
        }

        // This is the applyMagicModifier() calculations from NMS
        if (damageCause != EntityDamageEvent.DamageCause.STARVATION) {
            // Apply resistance effect
            if (modifiers.containsKey(EntityDamageEvent.DamageModifier.RESISTANCE) && damageCause != EntityDamageEvent.DamageCause.VOID &&
                    damagedEntity.hasPotionEffect(PotionEffectType.RESISTANCE)
            ) {
                final PotionEffect effect = damagedEntity.getPotionEffect(PotionEffectType.RESISTANCE);
                double level = (effect != null ? effect.getAmplifier() : 0) + 1;
                // Make sure we don't go over 100% protection
                double resistanceReductionFactor = Math.min(1.0, level * REDUCTION_PER_RESISTANCE_LEVEL);
                double resistanceReduction = -resistanceReductionFactor * currentDamage;
                modifiers.put(EntityDamageEvent.DamageModifier.RESISTANCE, resistanceReduction);
                currentDamage += resistanceReduction;
            }

            // Apply armor enchants
            // Don't calculate enchants if damage already 0 (like 1.8 NMS). Enchants cap at 80% reduction
            if (currentDamage > 0 && modifiers.containsKey(EntityDamageEvent.DamageModifier.MAGIC)) {
                final ArrayList<ItemStack> armorContents = new ArrayList<>();
                if (damagedEntity.getEquipment() != null) {
                    armorContents.addAll(
                            Arrays.stream(damagedEntity.getEquipment().getArmorContents()).toList()
                    );
                }

                double enchantsReductionFactor = calculateArmorEnchantmentReductionFactor(armorContents, damageCause);
                double enchantsReduction = currentDamage * -enchantsReductionFactor;
                modifiers.put(EntityDamageEvent.DamageModifier.MAGIC, enchantsReduction);
                currentDamage += enchantsReduction;
            }

            // Absorption
            if (modifiers.containsKey(EntityDamageEvent.DamageModifier.ABSORPTION)) {
                double absorptionAmount = damagedEntity.getAbsorptionAmount();
                double absorptionReduction = -Math.min(absorptionAmount, currentDamage);
                modifiers.put(EntityDamageEvent.DamageModifier.ABSORPTION, absorptionReduction);
            }
        }
    }

    private boolean wasValidPlayerKill(final EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Mob eventMob)) {
            return false;
        }
        final EntityDamageEvent cause = event.getEntity().getLastDamageCause();
        if (cause == null) {
            KillionCommons.getUtil().getConsoleUtil().sendDebug(
                    "Mob died but had no last cause of damage, cannot prevent event." +
                            "\nMob: " + eventMob.getName());
            return false;
        }

        return playerDamageCauses.contains(cause.getCause()) && eventMob.getKiller() != null;
    }
}
