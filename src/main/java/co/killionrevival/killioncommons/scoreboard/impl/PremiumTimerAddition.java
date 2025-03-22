package co.killionrevival.killioncommons.scoreboard.impl;

import co.killionrevival.killioncommons.scoreboard.ScoreboardAddition;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public class PremiumTimerAddition extends ScoreboardAddition {
    final String COMPONENT_NAME = "premium_timer";

    @Override
    public void initWithPlayer(Player player) {
        if (player == null) {
            return;
        }
        this.setEnabled(false);
    }

    @Override
    public String componentName() {
        return COMPONENT_NAME;
    }

    @Override
    public List<Component> getLinesToAdd() {
        return List.of();
    }

    @Override
    public ScoreboardAddition clone() {
        return null;
    }
}
