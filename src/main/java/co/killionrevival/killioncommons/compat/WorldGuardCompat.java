package co.killionrevival.killioncommons.compat;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class WorldGuardCompat {
    public static boolean enabled = false;
    public static DoubleFlag XP_MULT = null;

    public static void init() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            DoubleFlag flag = new DoubleFlag("xp-multiplier");
            registry.register(flag);
            XP_MULT = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("xp-multiplier");
            if (existing instanceof DoubleFlag) {
                XP_MULT = (DoubleFlag) existing;
            } else {
                // Handle the case where the flag already exists but is not of the expected type
                throw new IllegalStateException("Failed to register flag 'xp-multiplier' - a flag with the same name already exists!");
            }
        }

        enabled = true;
    }
}
