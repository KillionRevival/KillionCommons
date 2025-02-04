package co.killionrevival.killioncommons;

import co.killionrevival.killioncommons.commands.CommonsCommand;
import co.killionrevival.killioncommons.commands.ScoreboardCommand;
import co.killionrevival.killioncommons.config.KillionCommonsConfig;
import co.killionrevival.killioncommons.listeners.KillionGameplayListeners;
import co.killionrevival.killioncommons.npc.NpcManager;
import co.killionrevival.killioncommons.npc.listeners.AttackPacketListener;
import co.killionrevival.killioncommons.scoreboard.KillionScoreboardManager;
import co.killionrevival.killioncommons.scoreboard.ScoreboardListeners;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import io.leangen.geantyref.TypeToken;
import lombok.Getter;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.setting.ManagerSetting;

public final class KillionCommons extends JavaPlugin {
    @Getter
    private static KillionCommons instance;

    @Getter
    private static KillionUtilities util;

    @Getter
    private static PaperCommandManager<Source> commandManager;

    @Getter
    private static AnnotationParser<Source> annotationParser;

    @Getter
    private static KillionCommonsConfig customConfig;

    @Getter
    private NpcManager npcManager;

    @Getter
    private KillionScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        util = new KillionUtilities(this, KillionCommonsConfig.class);
        util.getConfigUtil().saveDefaultConfig();
        customConfig = (KillionCommonsConfig) util.getConfigUtil().getConfigObject();
        initCompat();
        initListeners();
        initManagers();
        initProtocolLib();
        setUpCommands();
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

    @SuppressWarnings("UnstableApiUsage")
    private void setUpCommands() {
        commandManager =
                PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
                                   .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                                   .buildOnEnable(this);

        commandManager.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);
        commandManager.settings().set(ManagerSetting.OVERRIDE_EXISTING_COMMANDS, true);

        commandManager.parameterInjectorRegistry().registerInjector(
                TypeToken.get(Player.class),
                (context, annotationAccessor) -> {
                    final Source sender = context.sender();

                    if (sender instanceof PlayerSource playerSource) {
                        return playerSource.source();
                    }

                    return null;
                }
        );

        util.getConsoleUtil().sendInfo("Registering commands:");
        annotationParser = new AnnotationParser<>(commandManager, Source.class, params -> SimpleCommandMeta.empty());
        annotationParser.parse(new ScoreboardCommand(scoreboardManager));
        annotationParser.parse(new CommonsCommand());
    }

    public static void reloadCustomConfig() {
        util.getConsoleUtil().sendInfo("Reloading custom config");
        customConfig.merge((KillionCommonsConfig) util.getConfigUtil().getConfigObject());
        util.getConsoleUtil().sendInfo("Custom config reloaded.");
    }

    // endregion
}
