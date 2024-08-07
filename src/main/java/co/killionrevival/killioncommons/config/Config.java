package co.killionrevival.killioncommons.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.config.models.DefaultConfig;

public class Config<T> {
    private final JavaPlugin plugin;
    private final File configFile;
    private final Class<T> configType;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private T config;

    public Config(JavaPlugin plugin, Class<T> configType) {
        this.plugin = plugin;
        this.configFile = new File(this.plugin.getDataFolder(), "config.json");
        this.configType = configType;

        this.loadConfig();
    }

    public T getConfig() {
        return this.config;
    }

    private void loadConfig() {
        this.ensureConfigFileExists();
        try {
            this.config = objectMapper.readValue(configFile, this.configType);
        } catch (IOException e) {
            e.printStackTrace();
            this.config = null;
        }
    }

    private void ensureConfigFileExists() {
        this.ensureDataFolderExists();

        // If the config file doesnt exist, try adding the plugin's default config
        if (!this.configFile.exists()) {
            try {
                this.plugin.saveResource("config.json", false);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        // If the config file still doesn't exist, try adding the killioncommons default
        // config
        if (!this.configFile.exists()) {
            try {
                KillionCommons.getInstance().saveResource("config.json", false);
                try {
                    DefaultConfig c = objectMapper.readValue(configFile, DefaultConfig.class);
                    c.setPluginPrefix("&8[&6" + KillionCommons.getInstance().getName() + "&8]&r");
                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(configFile, c);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void ensureDataFolderExists() {
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdirs();
        }
    }
}
