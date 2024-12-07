package co.killionrevival.killioncommons;

import co.killionrevival.killioncommons.listeners.KillionGameplayListeners;
import co.killionrevival.killioncommons.npc.NpcManager;
import co.killionrevival.killioncommons.npc.listeners.AttackPacketListener;
import co.killionrevival.killioncommons.scoreboard.KillionScoreboardManager;
import co.killionrevival.killioncommons.scoreboard.ScoreboardListeners;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class KillionCommons extends JavaPlugin {
    @Getter
    private static KillionCommons instance;

    @Getter
    private static KillionUtilities util;

    @Getter
    private NpcManager npcManager;

    @Getter
    private KillionScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        instance = this;
        util = new KillionUtilities(this);
        initCompat();
        initListeners();
        initManagers();
        initProtocolLib();
        util.getConsoleUtil().sendSuccess("KillionCommons has been enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        destroyCompat();
        util.getConsoleUtil().sendSuccess("KillionCommons has been disabled.");
    }

    private void initManagers() {
        this.npcManager = new NpcManager();
        this.scoreboardManager = new KillionScoreboardManager();
    }

    private void initListeners() {
        getServer().getPluginManager().registerEvents(new KillionGameplayListeners(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListeners(), this);
        util.getConsoleUtil().sendSuccess("KillionCommons listeners initialized.");
    }

    private void initCompat() {

    }

    private void initProtocolLib() {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new AttackPacketListener(
                new PacketAdapter.AdapterParameteters()
                        .plugin(this)
                        .types(PacketType.Play.Client.USE_ENTITY)
        ));
    }

    private void destroyCompat() {
    }

    // endregion
}
