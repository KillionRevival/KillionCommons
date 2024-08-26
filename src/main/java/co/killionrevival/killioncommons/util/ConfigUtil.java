package co.killionrevival.killioncommons.util;

import co.killionrevival.killioncommons.KillionCommons;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class exists to help read json configuration for your plugin
 * assumes config.json as the configuration filename in your plugin's default directory, unless otherwise set
 */
public class ConfigUtil {
    private final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    private Logger logger;
    private final Plugin plugin;
    private final Path configDirectory;
    private final File configFile;
    private final String configFileName;

    public ConfigUtil(final Plugin plugin) {
        this.plugin = plugin;
        configFileName = "config.json";
        configDirectory = plugin.getDataPath();
        configFile = new File(configDirectory+ "/" + configFileName);
        logger = plugin.getLogger();
    }

    public ConfigUtil(final String configFileName, final Plugin plugin) {
        this.configFileName = configFileName;
        this.plugin = plugin;
        configDirectory = plugin.getDataPath();
        configFile = new File(configDirectory+ "/" + configFileName);
    }

    /**
     * Parses the config file that this instance of ConfigUtil is configured for into a POJO representation
     * @param object Object class to
     * @return A serialized version of the config file for {@link T}
     * @param <T> type of object to return
     */
    public <T> T parseConfigToObject(Class<T> object) {
        return gson.fromJson(getConfigJson(), object);
    }

    /**
     * Saves the specified object as the config json, at the file specified in this instance of ConfigUtil
     * Will create the default directory and file if it does not exist.
     * @param object Object to save as the config json file
     */
    public void saveConfig(Object object) {
        if (!Files.exists(configFile.toPath())) {
            try {
                Files.createDirectories(configFile.toPath());
                Files.createFile(configFile.toPath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Could not create config file directory and file", e);
                return;
            }
        }

        try (final FileWriter writer = new FileWriter(configFile)) {
            writer.write(gson.toJson(object));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not write config to file", e);
        }
    }

    /**
     * Saves the default config for the file name that this ConfigUtil instance
     * holds. Will pull from the plugin's resources.
     */
    public void saveDefaultConfig() {
        final InputStream defaultConfig = plugin.getResource(configFileName);
        if (defaultConfig == null) {
            logger.severe("Default for file " + configFileName + " does not exist. Creating blank default.");
        }

        final Path filePath = Path.of(getConfigDirectory() + "/" + configFileName);
        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Could not create config file:", e);
                return;
            }
        }
        if (defaultConfig == null) {
            return;
        }

        try {
            Files.copy(defaultConfig, filePath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not copy default config to config file:", e);
        }
    }

    /**
     * Will return the path of the config directory and create it if it does not exist
     * @return The path of the plugin's data path.
     */
    public Path getConfigDirectory() {
        if (!Files.exists(configDirectory)) {
            try {
                Files.createDirectories(configDirectory);
            }
            catch (Exception e) {
                logger.log(Level.SEVERE, "Could not create config directory:", e);
                return configDirectory;
            }
        }
        return configDirectory;
    }

    private String getConfigJson() {
        try {
            return Files.readString(configFile.toPath());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not get config json string, returning null", e);
        }

        return null;
    }
}
