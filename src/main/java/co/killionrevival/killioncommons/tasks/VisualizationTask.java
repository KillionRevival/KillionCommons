package co.killionrevival.killioncommons.tasks;

import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class VisualizationTask extends BukkitRunnable {
    final List<Location> locations;
    final Player player;

    @Getter
    int timerCounter;
    @Getter
    final int timerLimit;
    @Getter
    final int period;

    final Particle particle;
    final Particle.DustOptions dustOptions;

    /**
     * Run a visualization with the following defaults:
     * 15 second duration
     * Spawn a particle every half second (10 ticks)
     * Redstone particle w/ color: #FF7F00
     * @param locations
     * @param player
     */
    public VisualizationTask(
            final List<Location> locations,
            final Player player
    ) {
        this.locations = locations;
        this.player = player;

        this.timerCounter = 0;
        this.timerLimit = 14; // run for 15 seconds total
        this.period = 10; // half a tick
        this.particle = Particle.DUST;
        this.dustOptions = new Particle.DustOptions(Color.fromRGB(0xFF7F00), .75f);
    }

    /**
     * Task to spawn particles at a list of locations for a specified duration.
     * @param locations Coordinates to spawn the particles at
     * @param player Player to spawn the particles for (client-side)
     * @param timerCounter Initial count
     * @param timerLimit How many times should the counter increment before cancelling the task
     * @param period Period in ticks to increment the counter
     * @param particle Particle to spawn
     * @param dustOptions Options for the particle
     */
    public VisualizationTask(
            final List<Location> locations,
            final Player player,
            final int timerCounter,
            final int timerLimit,
            final int period,
            final Particle particle,
            final Particle.DustOptions dustOptions
    ) {
        this.locations = locations;
        this.player = player;
        this.timerCounter = timerCounter;
        this.timerLimit = timerLimit;
        this.period = period;
        this.particle = particle;
        this.dustOptions = dustOptions;
    }

    @Override
    public void run() {
        for (Location l : locations) {
            player.spawnParticle(particle, l, 1, dustOptions);
        }

        timerCounter++;
    }
}
