package co.killionrevival.killioncommons.util;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.pojos.SkinData;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerTextures;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class SkinUtil {
    @AllArgsConstructor @Getter
    public enum Skull {
        DIAMOND_ARROW_UP    ("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q2OTVkMzM1ZTZiZThjYjJhMzRlMDVlMThlYTJkMTJjM2IxN2I4MTY2YmE2MmQ2OTgyYTY0M2RmNzFmZmFjNSJ9fX0="),
        DIAMOND_ARROW_DOWN  ("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM3ODYyY2RjMTU5OTk4ZWQ2YjZmZGNjYWFhNDY3NTg2N2Q0NDg0ZGI1MTJhODRjMzY3ZmFiZjRjYWY2MCJ9fX0="),
        DIAMOND_ARROW_LEFT  ("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2MTQwYWYzMmNiMzY0ZDliZTNiOTRlOTMwODFkNmNmYzhjMjdkM2NmZTBiNGRkNDVlNzg1MjI1ZWIifX19"),
        DIAMOND_ARROW_RIGHT ("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNhODQyNjdjYjVhMzdkNjk5YWJlN2Q2YTAzMTc4ZGUwODlkN2NmMmU3MjZmMzdkYTNmZTk5N2ZkNyJ9fX0="),
        RED_ARROW_UP        ("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmQ5Mjg3NjE2MzQzZDgzM2U5ZTczMTcxNTljYWEyY2IzZTU5NzQ1MTEzOTYyYzEzNzkwNTJjZTQ3ODg4NGZhIn19fQ=="),
        RED_ARROW_DOWN      ("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4NTJiZjYxNmYzMWVkNjdjMzdkZTRiMGJhYTJjNWY4ZDhmY2E4MmU3MmRiY2FmY2JhNjY5NTZhODFjNCJ9fX0="),
        RED_X               ("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ=="),
        GREEN_CHECKMARK     ("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=");

        final String texture;
    }

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

    /**
     * Gets a skull itemstack with a predefined skull
     * @param skull Predefined skull to get
     * @return ItemStack with the predefined skull
     */
    public static ItemStack getSkull(Skull skull) {
        final ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta meta = SkinUtil.setSkullMeta((SkullMeta) itemStack.getItemMeta(), skull);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Sets a skull meta's skin to a predefined skull
     * @param meta SkullMeta to set the skin of
     * @param skulls Predefined skull to set the skin to
     */
    @SneakyThrows
    public static SkullMeta setSkullMeta(final SkullMeta meta, final Skull skulls) {
        return setSkullMeta(meta, skulls.getTexture());
    }

    /**
     * Sets a skulls meta's skin to a custom texture
     * @param meta SkullMeta to set the skin of
     * @param skullTextureUrl URL of the skull skin
     */
    public static SkullMeta setSkullMetaByUrl(final SkullMeta meta, String skullTextureUrl) throws MalformedURLException {
        final PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
        final PlayerTextures textures = profile.getTextures();
        textures.setSkin(URI.create(skullTextureUrl).toURL());
        profile.setTextures(textures);
        meta.setPlayerProfile(profile);
        return meta;
    }

    /**
     * Sets a skulls meta's skin to a custom texture
     * @param meta SkullMeta to set the skin of
     * @param b64 Texture of the skull skin
     */
    public static SkullMeta setSkullMeta(final SkullMeta meta, String b64) {
        final PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
        final ProfileProperty textures = new ProfileProperty("textures", b64);
        profile.setProperty(textures);
        meta.setPlayerProfile(profile);
        return meta;
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
