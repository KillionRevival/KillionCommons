package co.killionrevival.killioncommons.npc;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.OfflinePlayer;

public interface IKillionNpc {
    PlayerProfile getPlayerRepresentation();
    boolean getAttackable();
    void setAttackable(final boolean attackable);
}
