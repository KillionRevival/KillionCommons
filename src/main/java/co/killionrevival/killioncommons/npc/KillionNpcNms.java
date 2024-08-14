package co.killionrevival.killioncommons.npc;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;

public class KillionNpcNms extends ServerPlayer implements IKillionNpc  {
    boolean attackable = false;

    public KillionNpcNms(
            MinecraftServer server,
            ServerLevel world,
            GameProfile profile
    ) {
        super(server, world, profile);
    }

    public KillionNpcNms(
            MinecraftServer server,
            ServerLevel world,
            GameProfile profile,
            IKillionNpc npc
    ) {
        super(server, world, profile);
        this.attackable = npc.getAttackable();
    }

    @Override
    public PlayerProfile getPlayerRepresentation() {
        return Bukkit.createProfile(uuid, displayName);
    }

    @Override
    public boolean getAttackable() {
        return attackable;
    }

    @Override
    public void setAttackable(final boolean attackable) {
        this.attackable = attackable;
    }
}
