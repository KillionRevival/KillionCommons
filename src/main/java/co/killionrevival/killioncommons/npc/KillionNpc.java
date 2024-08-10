package co.killionrevival.killioncommons.npc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public abstract class KillionNpc {
    UUID npcUuid;
    String name;
    OfflinePlayer playerSkin;
}
