package co.killionrevival.killioncommons.ui.items;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class SimpleItem extends AbstractItem {
  private final Material material;
  private final String title;
  private final String[] loreLines;
  private Runnable clickFunction;

  public SimpleItem(Material material, String title, String[] loreLines) {
    this.material = material;
    this.title = title;
    this.loreLines = loreLines;
    this.clickFunction = () -> {
    };
  }

  public SimpleItem(Material material, String title, String[] loreLines, Runnable clickFunction) {
    this(material, title, loreLines);
    this.clickFunction = clickFunction;
  }

  public SimpleItem(Material material, String title) {
    this(material, title, new String[] {});
  }

  public SimpleItem(Material material, String title, Runnable clickFunction) {
    this(material, title, new String[] {}, clickFunction);
  }

  @SuppressWarnings("removal")
  @Override
  public ItemProvider getItemProvider() {
    ItemStack itemStack = new ItemStack(this.material);
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta != null) {
      // Add fake attribute modifiers to GENERIC_ATTACK_DAMAGE and GENERIC_ATTACK_SPEED
      itemMeta.addAttributeModifier(
          Attribute.ATTACK_DAMAGE,
          new AttributeModifier(
              UUID.randomUUID(),
              "fake_attack_damage", // Arbitrary name
              0, // No actual effect
              AttributeModifier.Operation.ADD_NUMBER // Adds 0
          ));

      itemMeta.addAttributeModifier(
          Attribute.ATTACK_SPEED,
          new AttributeModifier(
              UUID.randomUUID(),
              "fake_attack_speed", // Arbitrary name
              0, // No actual effect
              AttributeModifier.Operation.ADD_NUMBER // Adds 0
          ));

      // Add the HIDE_ATTRIBUTES flag to hide tooltips
      itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

      // Set the meta back to the item
      itemStack.setItemMeta(itemMeta);
    }
    return new ItemBuilder(itemStack).setDisplayName(title).addLoreLines(loreLines)
        .addItemFlags(
            ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_ENCHANTS,
            ItemFlag.HIDE_UNBREAKABLE,
            ItemFlag.HIDE_ARMOR_TRIM,
            ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
  }

  @Override
  public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
    this.clickFunction.run();
  }

  public void update() {
    notifyWindows();
  }
}
