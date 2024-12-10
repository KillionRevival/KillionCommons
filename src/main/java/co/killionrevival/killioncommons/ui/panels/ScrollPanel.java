package co.killionrevival.killioncommons.ui.panels;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import co.killionrevival.killioncommons.ui.items.ScrollDownItem;
import co.killionrevival.killioncommons.ui.items.ScrollUpItem;
import co.killionrevival.killioncommons.ui.items.MySimpleItem;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Marker;
import xyz.xenondevs.invui.item.Item;

public class ScrollPanel extends Panel {
  private final Marker orientation;
  private final List<Item> items;
  private Runnable goBack;
  private boolean goBackEnabled;

  public ScrollPanel(Player player, String title, Marker orientation, List<Item> items) {
    super(player, title);

    this.orientation = orientation;
    this.items = items;
    this.goBack = () -> {
    };
    this.goBackEnabled = false;
  }

  public ScrollPanel(Player player, String title, Marker orientation, List<Item> items, Runnable goBack) {
    this(player, title, orientation, items);
    this.goBack = goBack;
    this.goBackEnabled = true;
  }

  @Override
  protected Gui createGui() {
    if (this.goBackEnabled) {
      return ScrollGui.items().setStructure(
        "x x x x x x x x u",
        "x x x x x x x x #",
        "x x x x x x x x B",
        "x x x x x x x x R",
        "x x x x x x x x #",
        "x x x x x x x x d")
        .addIngredient('x', orientation)
        .addIngredient('u', new ScrollUpItem())
        .addIngredient('d', new ScrollDownItem())
        .addIngredient('B',
            new MySimpleItem(Material.ARROW, "Back", () -> this.goBack.run()))
        .addIngredient('R',
            new MySimpleItem(Material.COMPASS, "Refresh", this::refresh))
        .setContent(items)
        .build();
      }
      return ScrollGui.items().setStructure(
        "x x x x x x x x u",
        "x x x x x x x x #",
        "x x x x x x x x R",
        "x x x x x x x x #",
        "x x x x x x x x d")
        .addIngredient('x', orientation)
        .addIngredient('u', new ScrollUpItem())
        .addIngredient('d', new ScrollDownItem())
        .addIngredient('R',
            new MySimpleItem(Material.COMPASS, "Refresh", this::refresh))
        .setContent(items)
        .build();
  }
}
