package co.killionrevival.killioncommons.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public enum EnchantmentType {
    // Data from https://minecraft.fandom.com/wiki/Armor#Mechanics
    PROTECTION (
            () -> {
                Set<EntityDamageEvent.DamageCause> damageCauses = new HashSet<>();
                damageCauses.add(EntityDamageEvent.DamageCause.CONTACT);
                damageCauses.add(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
                damageCauses.add(EntityDamageEvent.DamageCause.PROJECTILE);
                damageCauses.add(EntityDamageEvent.DamageCause.FALL);
                damageCauses.add(EntityDamageEvent.DamageCause.FIRE);
                damageCauses.add(EntityDamageEvent.DamageCause.LAVA);
                damageCauses.add(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION);
                damageCauses.add(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
                damageCauses.add(EntityDamageEvent.DamageCause.LIGHTNING);
                damageCauses.add(EntityDamageEvent.DamageCause.POISON);
                damageCauses.add(EntityDamageEvent.DamageCause.MAGIC);
                damageCauses.add(EntityDamageEvent.DamageCause.WITHER);
                damageCauses.add(EntityDamageEvent.DamageCause.FALLING_BLOCK);
                damageCauses.add(EntityDamageEvent.DamageCause.THORNS);
                damageCauses.add(EntityDamageEvent.DamageCause.HOT_FLOOR);
                damageCauses.add(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
                return damageCauses;
            },
            0.75, Enchantment.PROTECTION
    ),
    FIRE_PROTECTION(
            () -> {
                Set<EntityDamageEvent.DamageCause> damageCauses = new HashSet<>();
                damageCauses.add(EntityDamageEvent.DamageCause.FIRE);
                damageCauses.add(EntityDamageEvent.DamageCause.FIRE_TICK);
                damageCauses.add(EntityDamageEvent.DamageCause.LAVA);
                damageCauses.add(EntityDamageEvent.DamageCause.HOT_FLOOR);
                return damageCauses;
            }, 1.25, Enchantment.FIRE_PROTECTION),
    BLAST_PROTECTION(
            () -> {
                Set<EntityDamageEvent.DamageCause> damageCauses = new HashSet<>();
                damageCauses.add(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
                damageCauses.add(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION);
                return damageCauses;
            }, 1.5, Enchantment.BLAST_PROTECTION
    ),
    PROJECTILE_PROTECTION(
            () -> {
                Set<EntityDamageEvent.DamageCause> damageCauses = new HashSet<>();
                damageCauses.add(EntityDamageEvent.DamageCause.PROJECTILE);
                return damageCauses;
            }, 1.5, Enchantment.PROJECTILE_PROTECTION),
    FALL_PROTECTION(
            () -> {
                Set<EntityDamageEvent.DamageCause> damageCauses = new HashSet<>();
                damageCauses.add(EntityDamageEvent.DamageCause.FALL);
                return damageCauses;
            }, 2.5, Enchantment.FEATHER_FALLING);

    public final Supplier<Set<EntityDamageEvent.DamageCause>> protectionSupplier;
    public double typeModifier;
    public final Enchantment enchantment;
    public final Set<EntityDamageEvent.DamageCause> protection;

    EnchantmentType(Supplier<Set<EntityDamageEvent.DamageCause>> protectionSupplier, double typeModifier, Enchantment enchantment) {
        this.protectionSupplier = protectionSupplier;
        this.typeModifier = typeModifier;
        this.enchantment = enchantment;
        protection = protectionSupplier.get();
    }

    /**
     * Returns whether the armor protects against the given damage cause.
     *
     * @param cause the damage cause
     * @return true if the armor protects against the given damage cause
     */
    public boolean protectsAgainst(EntityDamageEvent.DamageCause cause) {
        return protection.contains(cause);
    }

    /**
     * Returns the enchantment protection factor (EPF).
     *
     * @param level the level of the enchantment
     * @return the EPF
     */
    public int getEpf(int level){
        // floor ( (6 + level^2) * TypeModifier / 3 )
        return (int) Math.floor((6 + level * level) * typeModifier / 3);
    }
}