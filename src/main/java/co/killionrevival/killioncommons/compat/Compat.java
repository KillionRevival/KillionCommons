package co.killionrevival.killioncommons.compat;

import org.bukkit.plugin.Plugin;

public interface Compat {
    void init(Plugin plugin);
    void destroy();
}
