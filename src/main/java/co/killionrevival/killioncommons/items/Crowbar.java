package co.killionrevival.killioncommons.items;

import co.killionrevival.killioncommons.KillionCommons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Crowbar {

    public static NamespacedKey itemKey;
    private static final ItemStack crowbar;

    static {
        itemKey = new NamespacedKey(KillionCommons.getInstance(), "killion_crowbar");
        crowbar = new ItemStack(Material.CHAIN, 1);

        final ItemMeta meta = crowbar.getItemMeta();
        meta.displayName(
                Component.text("" + ChatColor.RESET)
                        .color(TextColor.color(0x3F88C5))
                        .append(Component.text("Right click me on a spawner!")));
        crowbar.setAmount(1);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right click on a spawner to replace this item with the spawner.")
                .color(TextColor.color(0xD00000)));
        lore.add(Component.text("No refunds!").color(TextColor.color(0xD00000)));
        meta.lore(lore);

        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "true");

        crowbar.setItemMeta(meta);
        crowbar.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);
    }

    public static ItemStack getNewCrowbar() {
        return crowbar.clone();
    }

    public static boolean isCrowbar(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return false;
        }
        final String value = item.getItemMeta().getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);

        return value != null && value.equals("true");
    }
}
