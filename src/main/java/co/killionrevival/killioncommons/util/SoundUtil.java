package co.killionrevival.killioncommons.util;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;

public class SoundUtil {
    /**
     * Plays a sound at a location. If the namespaced key for the given Sound is null, the sound will not play.
     * Uses volume = 1 and pitch = 1
     * @param sound Sound to play
     * @param location Location to play the sound at
     */
    public static void playSoundAtLocation(
            final Location location,
            final org.bukkit.Sound sound
    ) {
        playSoundAtLocation(location, sound, 1, 1);
    }
    /**
     * Plays a sound at a location. If the namespaced key for the given Sound is null, the sound will not play.
     * @param sound Sound to play
     * @param location Location to play the sound at
     * @param volume Volume of the sound
     * @param pitch Pitch of the sound
     */
    public static void playSoundAtLocation(
            final Location location,
            final org.bukkit.Sound sound,
            final float volume,
            final float pitch
    ) {
        final NamespacedKey key = Registry.SOUNDS.getKey(sound);
        if (key == null) {
            return;
        }
        final Sound adventureSound = Sound.sound(
                key,
                Sound.Source.BLOCK,
                volume,
                pitch
        );
        location.getWorld().playSound(adventureSound);
    }

    public static void playErrorSoundtoPlayer(
            final Player player
    ) {
        playSoundToPlayer(player, org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, .75f);
    }

    /**
     * Plays a sound to a player. If the namespaced key for the given Sound is null, the sound will not play.
     * @param player Player to play the sound to
     * @param sound Sound to play
     */
    public static void playSoundToPlayer(
            final Player player,
            final org.bukkit.Sound sound,
            final float volume,
            final float pitch
    ) {
        final NamespacedKey key = Registry.SOUNDS.getKey(sound);
        if (key == null) {
            return;
        }
        player.playSound(Sound.sound(
                key,
                Sound.Source.BLOCK,
                volume,
                pitch
        ));
    }

    /**
     * Plays a sound to a player. If the namespaced key for the given Sound is null, the sound will not play.
     * Uses volume = 1 and pitch = 1
     * @param player Player to play the sound to
     * @param sound Sound to play
     */
    public static void playSoundToPlayer(
            final Player player,
            final org.bukkit.Sound sound
    ) {
        playSoundToPlayer(player, sound, 1 ,1);
    }

    /**
     * Plays a sound to a player. If the namespaced key for the given Sound is null, the sound will not play.
     * Will pick a random pitch between the two floats.
     * @param player Player to play the sound to
     * @param sound Sound to play
     */
    public static void playModularSoundToPlayer(
            final Player player,
            final org.bukkit.Sound sound,
            float minPitch,
            float maxPitch
    ) {
        final float pitch = (float) (Math.random() * (maxPitch - minPitch) + minPitch);
        playSoundToPlayer(player, sound, 1 , pitch);
    }
}
