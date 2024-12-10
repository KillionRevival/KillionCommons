package co.killionrevival.killioncommons.ui.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class BorderItem extends AbstractItem {
  private final Material material;

  public BorderItem(Material material) {
    this.material = material;
  }

  @Override
  public ItemProvider getItemProvider() {
    return new ItemBuilder(this.material).setDisplayName("§r");
  }

  @Override
  public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
  }
}