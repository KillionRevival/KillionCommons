package co.killionrevival.killioncommons.scoreboard.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor @Data
public class EnabledComponent {
    UUID playerId;
    String componentName;
    boolean enabled;
}
