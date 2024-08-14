package co.killionrevival.killioncommons.npc.listeners;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.KillionUtilities;
import co.killionrevival.killioncommons.npc.KillionNpcNms;
import co.killionrevival.killioncommons.npc.NpcManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AttackPacketListener extends PacketAdapter {
    final NpcManager manager;
    final KillionUtilities util;

    public AttackPacketListener(@NotNull PacketAdapter.AdapterParameteters params) {
        super(params);
        manager = KillionCommons.getInstance()
                                .getNpcManager();
        util = KillionCommons.getUtil();
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        final PacketContainer packet = event.getPacket();
        final int entityId = packet.getIntegers().read(0);
        // if it's not our npc, it won't be in the cache.
        final KillionNpcNms npcNms = (KillionNpcNms) manager.getNpcFromCache(entityId);
        if (npcNms == null) {
            return;
        }
        // are we attacking?
        final EnumWrappers.EntityUseAction interactType = packet.getEnumEntityUseActions().readSafely(0).getAction();
        if (
                !Objects.equals(interactType, EnumWrappers.EntityUseAction.ATTACK) ||
                !npcNms.getAttackable()
        ) {
            return;
        }
        // we are! damage the entity and send packet back to player
        final CraftPlayer attacker = (CraftPlayer) event.getPlayer();
        final ServerPlayer serverAttacker = attacker.getHandle();
        serverAttacker.attack(npcNms); // damage.
        util.getConsoleUtil().sendInfo("Hit " + npcNms.getName() + "!");
    }
}
