package co.killionrevival.killioncommons.config;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class KillionCommonsConfig {
    boolean debugMode;
    String logLevel;

    boolean disableStrength2;
    boolean enableStrength2OnBeacons;
    boolean enableMendingChanges;
    boolean swordBlocking;

    public void merge(final KillionCommonsConfig config) {
        if (config == null) {
            return;
        }
        this.debugMode = config.debugMode;
        this.logLevel = config.logLevel;
        this.disableStrength2 = config.disableStrength2;
        this.enableStrength2OnBeacons = config.enableStrength2OnBeacons;
        this.enableMendingChanges = config.enableMendingChanges;
        this.swordBlocking = config.swordBlocking;
    }
}
