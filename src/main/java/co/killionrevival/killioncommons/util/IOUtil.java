package co.killionrevival.killioncommons.util;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class IOUtil {
    /**
     * Gets a file in the plugin's data folder, relative to the base folder of the plugin.
     * @param plugin Plugin to reference
     * @param relativeFilePath File in the plugin's data folder
     * @return InputStream of the file, null if the folder or the file does not exist. Or if it's parent directories do not exist.
     */
    public static InputStream getPluginFile(final Plugin plugin, final String relativeFilePath) {
        final File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            return null;
        }
        final File file = new File(dataFolder.getPath() + "/" + relativeFilePath);
        if (!file.exists()) {
            return null;
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException ignored) {
            return null;
        }
    }
}
