package co.killionrevival.killioncommons.npc.util;

import co.killionrevival.killioncommons.npc.KillionNpcNms;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class NpcPacketUtil {
    public static void displayNpcHitToPlayers(
            final KillionNpcNms npc) {
        final CraftWorld world = npc.level().getWorld();
        final Location playerLocation = npc.getBukkitEntity().getLocation();
        final ArrayList<Player> playersInRender = new ArrayList<>(
                world.getNearbyPlayers(playerLocation, 128));
        playersInRender.stream()
                .map(player -> (CraftPlayer) player)
                .forEach(player -> {
                    final ServerGamePacketListenerImpl ps = player.getHandle().connection;
                    ps.send(new ClientboundHurtAnimationPacket(npc));
                });
    }
}
