package co.killionrevival.killioncommons.ui.panels;

import org.bukkit.entity.Player;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

public abstract class Panel {
  protected Player player;
  protected String title;

  protected Gui gui;
  protected Window window;

  public Panel(Player player, String title) {
    this.player = player;
    this.title = title;

    this.setup();
  }

  protected abstract Gui createGui();

  private Window createWindow() {
    return Window.single().setViewer(this.player).setTitle(this.title).setGui(this.gui).build();
  }

  private void setup() {
    this.gui = this.createGui();
    this.window = this.createWindow();
  }

  public void open() {
    this.window.open();
  }

  public void close() {
    this.window.close();
  }

  public void refresh() {
    this.setup();
    this.open();
  }
}
