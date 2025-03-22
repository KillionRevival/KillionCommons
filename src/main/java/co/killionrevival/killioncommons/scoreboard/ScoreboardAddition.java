package co.killionrevival.killioncommons.scoreboard;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Default constructor will have a null player. Use the initWithPlayer method to initialize the player.
 * {@link KillionScoreboardManager} maintains a list of ScoreboardAdditions along with the plugin that initialized them
 * and will add them to all current scoreboards when they are registered. This list of scoreboard objects has a null player,
 * and is mostly used to keep track of the Addition objects using their component name.
 * Each {@link KillionScoreboard} will have a list of ScoreboardAdditions that are toggled on or off. Each player has a
 * corresponding Scoreboard object.
 */
public abstract class ScoreboardAddition {
    @Getter @Setter
    boolean enabled;

    @Getter @Setter
    private Player player;

    /**
     * Use in your ScoreboardAddition to initialize your instance of Scoreboard addition with a player object.
     * @param player
     */
    public abstract void initWithPlayer(final Player player);

    /**
     * @return the name of your component that players will use in a command to disable it
     */
    public abstract String componentName();

    /**
     * @return the list of components that will be rendered in the scoreboard if the component is enabled
     */
    public abstract List<Component> getLinesToAdd();

    /**
     * @return a new instance of your ScoreboardAddition object. Used in {@link KillionScoreboard#KillionScoreboard(Player)}
     * to clone the object
     */
    public abstract ScoreboardAddition clone();

    public ScoreboardAddition() {
        this.enabled = true;
    }

    /**
     * Toggles if this component is enabled.
     */
    public void toggle() {
        this.enabled = !this.enabled;
    }
}
