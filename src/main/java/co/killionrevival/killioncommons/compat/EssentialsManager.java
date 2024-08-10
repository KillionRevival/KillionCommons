package co.killionrevival.killioncommons.compat;

import co.killionrevival.killioncommons.KillionCommons;
import com.earth2me.essentials.Essentials;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

public class EssentialsManager implements Compat {
    @Getter
    Essentials essentials = null;

    // region Essentials
    @Override
    public void init(Plugin essentials) {
        this.essentials = (Essentials) essentials;
        try {
            this.essentials.getItemDb().registerResolver(KillionCommons.getInstance(), "killion", new EssentialsItemResolver());
        } catch (Exception e) {
            KillionCommons.getUtil().getConsoleUtil().sendThrowable(e);
        }

        KillionCommons.getUtil().getConsoleUtil().sendSuccess("Registered Essentials ItemDB.");
    }

    public void destroy() {
        if (this.essentials == null) {
            return;
        }

        try {
            this.essentials.getItemDb().unregisterResolver(KillionCommons.getInstance(), "killion");
        } catch (Exception e) {
            KillionCommons.getUtil().getConsoleUtil().sendThrowable(e);
        }
    }
}
