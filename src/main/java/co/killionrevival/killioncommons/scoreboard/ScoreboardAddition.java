package co.killionrevival.killioncommons.scoreboard;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class ScoreboardAddition {
    @Getter @Setter
    boolean enabled;

    @Getter @Setter
    private Player player;

    public abstract void initWithPlayer(final Player player);
    public abstract String componentName();
    public abstract List<Component> getLinesToAdd();
    public abstract ScoreboardAddition clone();

    public ScoreboardAddition() {
        this.enabled = true;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }
}
