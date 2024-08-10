package co.killionrevival.killioncommons;

import co.killionrevival.killioncommons.compat.EssentialsManager;
import co.killionrevival.killioncommons.listeners.CrowbarListeners;
import co.killionrevival.killioncommons.listeners.KillionGameplayListeners;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class KillionCommons extends JavaPlugin {
    @Getter
    private static KillionCommons instance;

    @Getter
    private static KillionCommonsApi api;

    @Getter
    private EssentialsManager essentialsManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        instance = this;
        api = new KillionCommonsApi(this);
        initCompat();
        initListeners();
        api.getConsoleUtil().sendSuccess("KillionCommons has been enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        destroyCompat();
        api.getConsoleUtil().sendSuccess("KillionCommons has been disabled.");
    }

    private void initListeners() {
        getServer().getPluginManager().registerEvents(new CrowbarListeners(), this);
        getServer().getPluginManager().registerEvents(new KillionGameplayListeners(), this);
        api.getConsoleUtil().sendSuccess("KillionCommons listeners initialized.");
    }

    private void initCompat() {
        final Plugin essentials = Bukkit.getPluginManager().getPlugin("Essentials");
        if (essentials != null && essentials.isEnabled()) {
            essentialsManager = new EssentialsManager();
            essentialsManager.init(essentials);
        }
        else {
            KillionCommons.getApi().getConsoleUtil().sendError("Essentials is not loaded. Items will not attempt to be loaded into Essentials itemdb.");
        }

    }

    private void destroyCompat() {
        if (essentialsManager != null) {
            essentialsManager.destroy();
        }
    }

    // endregion
}
