package co.killionrevival.killioncommons.npc;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.npc.events.KillionNpcSpawnEvent;
import co.killionrevival.killioncommons.pojos.SkinData;
import co.killionrevival.killioncommons.util.ConsoleUtil;
import co.killionrevival.killioncommons.util.SkinUtil;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NpcManager {
    final static KillionCommons instance = KillionCommons.getInstance();
    final ConsoleUtil consoleUtil;
    final SkinUtil skinUtil;
    final Map<UUID, ServerPlayer> npcs;

    public NpcManager() {
        npcs = new ConcurrentHashMap<>();
        consoleUtil = KillionCommons.getUtil().getConsoleUtil();
        skinUtil = KillionCommons.getUtil().getSkinUtil();
    }

    public void spawn(
            final KillionNpc npcToSpawn,
            final Location locationToSpawnAt
    ) {
        final CraftWorld world = (CraftWorld) locationToSpawnAt.getWorld();
        final ServerPlayer npc = getServerPlayer(npcToSpawn, locationToSpawnAt, world);
        final ArrayList<Player> playersInRender = new ArrayList<>(world.getNearbyPlayers(locationToSpawnAt, 128));

        final KillionNpcSpawnEvent event = new KillionNpcSpawnEvent(npcToSpawn, locationToSpawnAt, playersInRender);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        npcs.put(npcToSpawn.getNpcUuid(), npc);
        playersInRender.stream()
                       .map(player -> (CraftPlayer) player)
                       .forEach(player -> {
                           final ServerGamePacketListenerImpl ps = player.getHandle().connection;
                           ps.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
                           ps.send(new ClientboundAddPlayerPacket(npc));
                       });
    }

    private ServerPlayer getServerPlayer(KillionNpc npcToSpawn, Location locationToSpawnAt, CraftWorld world) {
        final ServerLevel level = world.getHandle().getLevel();
        final MinecraftServer server = level.getServer();
        final GameProfile profile = new GameProfile(npcToSpawn.getNpcUuid(), npcToSpawn.getName());
        if (npcToSpawn.getPlayerSkin() != null) {
            final SkinData data = KillionCommons.getUtil().getSkinUtil().getSkin(npcToSpawn.getPlayerSkin());
            profile.getProperties().put("textures", new Property("textures", data.getTexture(), data.getSignature()));
        }

        final ServerPlayer npc = new ServerPlayer(
                server,
                level,
                profile
        );

        npc.setPos(locationToSpawnAt.getX(), locationToSpawnAt.getY(), locationToSpawnAt.getZ());
        return npc;
    }

    public void persistNpcs() {

    }
}
