package co.killionrevival.killioncommons.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HeadrosRod {

    private static ItemStack fishingRod;

    static {
        fishingRod = new ItemStack(Material.FISHING_ROD, 1);
        final ItemMeta meta = fishingRod.getItemMeta();
        meta.displayName(
                Component.text("" + ChatColor.RESET)
                        .color(TextColor.color(0x3F88C5))
                        .append(Component.text("Headros' ").decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("Special "))
                        .append(Component.text("Rod").decoration(TextDecoration.ITALIC, false))
        );
        fishingRod.setAmount(1);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(""));
        lore.add(Component.text("Who knows where it's been?!").color(TextColor.color(0x909590)));
        meta.lore(lore);
        fishingRod.setItemMeta(meta);
        fishingRod.addUnsafeEnchantment(Enchantment.LURE, 4);
    }

    public static ItemStack getNewFishingRod() {
        return fishingRod.clone();
    }
}
