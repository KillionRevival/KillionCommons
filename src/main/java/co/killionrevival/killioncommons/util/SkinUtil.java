package co.killionrevival.killioncommons.util;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.pojos.SkinData;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class SkinUtil {
    final Gson gson = new Gson();
    final Plugin plugin;
    final File skinsFolder;

    public SkinUtil(
            final Plugin plugin,
            final String skinCacheFolderName
    ) {
        this.plugin = plugin;
        skinsFolder = new File(plugin.getDataFolder(), skinCacheFolderName != null ? skinCacheFolderName : "skins");
    }

    public SkinUtil(
            final Plugin plugin
    ) {
        this(plugin, null);
    }

    public SkinData getSkin(
           final UUID playerId
    ) {
        createSkinsFolder();
        final File skinFile = new File(skinsFolder, playerId + ".json");
        if (skinFile.exists()) {
            try (InputStream inputStream = new FileInputStream(skinFile)) {
                return gson.fromJson(new InputStreamReader(inputStream), SkinData.class);
            } catch (Exception e) {
                KillionCommons.getUtil().getConsoleUtil().sendError("Could not open skin file " + skinFile);
                return null;
            }
        }
        final OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);
        final PlayerProfile profile = Bukkit.getServer().createProfile(player.getUniqueId(), player.getName());

        profile.complete();
        final Optional<ProfileProperty> textureProperty = profile.getProperties()
                                                 .stream()
                                                 .filter(property -> property.getName().equalsIgnoreCase("textures"))
                                                 .findFirst();
        if (textureProperty.isEmpty()) {
            return null;
        }

        final ProfileProperty texture = textureProperty.get();
        final SkinData skinData = new SkinData(texture.getValue(), texture.getSignature());
        try {
            Files.write(skinFile.toPath(), Collections.singleton(gson.toJson(skinData)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            KillionCommons.getUtil().getConsoleUtil().sendError("Could not open skin file " + skinFile);
        }

        return skinData;
    }

    public SkinData getSkin(
            final OfflinePlayer player
    ) {
        return getSkin(player.getUniqueId());
    }

    private void createSkinsFolder() {
        if (!skinsFolder.exists()) {
            skinsFolder.mkdir();
        }
    }
}
