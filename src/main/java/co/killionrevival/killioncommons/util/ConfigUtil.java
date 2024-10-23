package co.killionrevival.killioncommons.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * This class exists to help read json configuration for your plugin
 * assumes config.json as the configuration filename in your plugin's default directory, unless otherwise set
 */
public class ConfigUtil {
    private final Logger logger = LogManager.getLogger(ConfigUtil.class);
    private final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    private Class<?> type;
    private File configFile;
    private String configFileName;
    private final Plugin plugin;
    private final Path configDirectory;

    public ConfigUtil(final Plugin plugin) {
        this.plugin = plugin;
        configDirectory = plugin.getDataPath();
        this.configFileName = "config.json";
        configFile = new File(configDirectory + "/" + configFileName);
        type = null;
    }

    public ConfigUtil(final Plugin plugin, Class<?> configClass) {
        this(plugin);
        type = configClass;
    }

    public ConfigUtil(final String configFileName, final Plugin plugin, Class<?> configClass) {
        this(plugin);
        this.configFileName = configFileName;
        configFile = new File(configDirectory + "/" + configFileName);
        type = configClass;
    }

    /**
     * Parses the config file that this instance of ConfigUtil is configured for into a POJO representation
     * @return A serialized version of the config file for the type registered with this class
     */
    public Object getConfigObject() {
        return gson.fromJson(getConfigJson(), type);
    }

    /**
     * Saves the specified object as the config json, at the file specified in this instance of ConfigUtil
     * Will create the default directory and file if it does not exist.
     * @param object Object to save as the config json file
     */
    public void saveConfig(Object object) {
        try (final FileWriter writer = new FileWriter(configFile)) {
            writer.write(gson.toJson(object));
        } catch (IOException e) {
            logger.error("Could not write config to file", e);
        }
    }

    /**
     * Saves the default config for the file name that this ConfigUtil instance
     * holds. Will pull from the plugin's resources folder.
     */
    public void saveDefaultConfig() {
        if (Files.exists(configFile.toPath())) {
            return;
        }

        createConfigDirectoriesAndFile();
        final InputStream defaultConfig = plugin.getResource(configFileName);
        if (defaultConfig == null) {
            logger.error("Default for file {} does not exist.", configFileName);
            return;
        }
        BufferedReader defaultConfigReader = new BufferedReader(
                new InputStreamReader(defaultConfig, StandardCharsets.UTF_8));
        final String text = defaultConfigReader.lines().collect(Collectors.joining("\n"));
        try {
            defaultConfigReader.close();
        } catch (IOException e) {
            logger.error("Could not copy default config to config file:", e);
        }
        try {
            Files.write(configFile.toPath(), text.getBytes());
        } catch (IOException e) {
            logger.error("Could not copy default config to config file:", e);
        }
    }

    /**
     * Get a specific member object of the config as a json element.
     * Creates the directory, and file if it doesn't exist.
     * @param node Member node of the base element
     * @return JsonElement representation
     */
    public JsonElement getJsonMember(final String node) {
        saveDefaultConfig();
        return gson.fromJson(getConfigJson(), JsonObject.class).get(node);
    }

    private void createConfigDirectoriesAndFile() {
        if (Files.exists(configFile.toPath())) {
            return;
        }

        try {
            Files.createDirectories(configFile.toPath().getParent());
            Files.createFile(configFile.toPath());
        } catch (IOException e) {
            logger.error("Could not create config file directory and file", e);
        }
    }

    private String getConfigJson() {
        try {
            return Files.readString(configFile.toPath());
        } catch (Exception e) {
            logger.error("Could not get config json string, returning null", e);
        }

        return null;
    }
}
