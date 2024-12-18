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
}
