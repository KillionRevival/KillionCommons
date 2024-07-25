package co.killionrevival.killioncommons.compat;

import co.killionrevival.killioncommons.items.Crowbar;
import co.killionrevival.killioncommons.items.HeadrosRod;
import net.ess3.api.IItemDb;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EssentialsItemResolver implements IItemDb.ItemResolver {
    @Override
    public ItemStack apply(String name) {
        name = name.toLowerCase();
        switch (name) {
            case "headros_rod":
                return HeadrosRod.getNewFishingRod();
            case "crowbar":
                return Crowbar.getNewCrowbar();
            default:
                return null;
        }
    }

    @Override
    public Collection<String> getNames() {
        final List<String> names = new ArrayList<>();
        names.add("headros_rod");
        names.add("crowbar");
        return names;
    }
}
