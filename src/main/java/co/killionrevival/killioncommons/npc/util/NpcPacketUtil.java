package co.killionrevival.killioncommons.npc.util;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.npc.KillionNpcNms;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class NpcPacketUtil {
    public static void displayNpcHitToPlayers(
            final KillionNpcNms npc
    ) {
        final CraftWorld world = npc.getLevel().getWorld();
        final Location playerLocation = npc.getBukkitEntity().getLocation();
        final ArrayList<Player> playersInRender = new ArrayList<>(
                world.getNearbyPlayers(playerLocation, 128)
        );
        playersInRender.stream()
                       .map( player -> (CraftPlayer) player)
                       .forEach(player -> {
                           final ServerGamePacketListenerImpl ps = player.getHandle().connection;
                           ps.send(new ClientboundAnimatePacket(npc, ClientboundAnimatePacket.HURT));
                           ps.send(new ClientboundUpdateAttributesPacket(npc.getId(), npc.getAttributes().getSyncableAttributes()));
                           ps.send(new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData(), true));
                           ps.send(new ClientboundSetEntityMotionPacket(npc));
                       });
    }
}
