package co.killionrevival.killioncommons;

import co.killionrevival.killioncommons.compat.EssentialsItemResolver;
import co.killionrevival.killioncommons.listeners.CrowbarListeners;
import co.killionrevival.killioncommons.listeners.KillionGameplayListeners;
import com.earth2me.essentials.Essentials;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class KillionCommons extends JavaPlugin {
    private static Essentials essentialsInstance;

    @Getter
    private static KillionCommons instance;

    @Getter
    private static KillionCommonsApi api;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        instance = this;
        api = new KillionCommonsApi(this);
        initEssentials();
        initListeners();
        api.getConsoleUtil().sendSuccess("KillionCommons has been enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        destroyEssentials();
        api.getConsoleUtil().sendSuccess("KillionCommons has been disabled.");
    }

    private void initListeners() {
        getServer().getPluginManager().registerEvents(new CrowbarListeners(), this);
        getServer().getPluginManager().registerEvents(new KillionGameplayListeners(), this);
        api.getConsoleUtil().sendSuccess("KillionCommons listeners initialized.");
    }

    // region Essentials
    private void initEssentials() {
        Plugin essentials = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        if (essentials == null) {
            api.getConsoleUtil().sendError("Essentials is not loaded. Items will not attempt to be loaded into Essentials itemdb.");
            return;
        }

        essentialsInstance = (Essentials) essentials;
        try {
            essentialsInstance.getItemDb().registerResolver(this, "killion", new EssentialsItemResolver());
        } catch (Exception e) {
            api.getConsoleUtil().sendThrowable(e);
        }

        api.getConsoleUtil().sendSuccess("Registered Essentials ItemDB.");
    }

    private void destroyEssentials() {
        if (essentialsInstance == null) {
            return;
        }

        try {
            essentialsInstance.getItemDb().unregisterResolver(this, "killion");
        } catch (Exception e) {
            api.getConsoleUtil().sendThrowable(e);
        }
    }

    // endregion
}
