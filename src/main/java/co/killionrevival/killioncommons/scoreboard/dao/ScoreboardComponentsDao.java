package co.killionrevival.killioncommons.scoreboard.dao;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.database.DataAccessObject;
import co.killionrevival.killioncommons.database.DatabaseConnection;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreboardComponentsDao extends DataAccessObject<EnabledComponent> {
    private final ConsoleUtil logger = KillionCommons.getUtil().getConsoleUtil();
    @Getter
    private final HashMap<UUID, Map<String, Boolean>> enabledComponents = new HashMap<>();

    final String SELECT_COMPONENT = """
            Select player_id,
                   component_name,
                   enabled
              From scoreboard_components
    """;

    final String SELECT_COMPONENTS_BY_PLAYER = //language=psql
            SELECT_COMPONENT +
            """
                Where player_id = ?
            """;

    final String INSERT_COMPONENT = """
                Insert Into scoreboard_components
                Values (?, ?, ?)
                On Conflict (player_id, component_name)
                Do Update Set enabled = ?
            """;


    public ScoreboardComponentsDao(DatabaseConnection connection) {
        super(connection);
    }

    /**
     * Save a component as enabled or disabled for a player
     * @param component The component to save
     */
    public void saveComponent(EnabledComponent component) {
        try {
            executeUpdate(
                    INSERT_COMPONENT,
                    component.getPlayerId().toString(),
                    component.getComponentName(),
                    component.isEnabled(),
                    component.isEnabled()
            );
        } catch (Exception e) {
            logger.sendError("Failed to save scoreboard component to the database", e);
        }
    }

    /**
     * Load all player-defined enabled components from the database
     */
    public void loadComponents() {
        List<EnabledComponent> components;
        try {
            components = fetchQuery(SELECT_COMPONENT);
        } catch (Exception e) {
            logger.sendError("Failed to load scoreboard components from the database", e);
            components = new ArrayList<>();
        }

        for (EnabledComponent component : components) {
            enabledComponents.computeIfAbsent(component.getPlayerId(), k -> new HashMap<>())
                    .put(component.getComponentName(), component.isEnabled());
        }
    }

    @Override
    public List<EnabledComponent> parse(ResultSet resultSet) throws SQLException {
        final List<EnabledComponent> components = new ArrayList<>();

        while (resultSet.next()) {
            final UUID playerId = UUID.fromString(resultSet.getString("player_id"));
            final String componentName = resultSet.getString("component_name");
            final boolean enabled = resultSet.getBoolean("enabled");

            components.add(new EnabledComponent(playerId, componentName, enabled));
        }

        return components;
    }
}
