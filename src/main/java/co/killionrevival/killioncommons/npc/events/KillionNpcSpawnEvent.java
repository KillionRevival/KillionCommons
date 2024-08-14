package co.killionrevival.killioncommons.npc.events;

import co.killionrevival.killioncommons.npc.IKillionNpc;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KillionNpcSpawnEvent extends Event implements Cancellable {
    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private boolean canceled;

    @Getter
    private final IKillionNpc npc;
    @Getter
    private final List<Player> playersInRender;
    @Getter @Setter
    private Location spawnLocation;

    public KillionNpcSpawnEvent(
            final IKillionNpc npc,
            final Location location,
            final List<Player> playersInRender
    ) {
        this.npc = npc;
        this.spawnLocation = location;
        this.playersInRender = playersInRender;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }
}
