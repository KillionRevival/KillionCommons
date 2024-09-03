package co.killionrevival.killioncommons.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TextFormatUtil {

    public static Component getComponentFromLegacyString(String text) {
        LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
                .character('&')
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();

        return legacySerializer.deserialize(text);
    }

    public static Component getComponentFromJson(String json) {
        GsonComponentSerializer gsonSerializer = GsonComponentSerializer.gson();
        return gsonSerializer.deserialize(json);
    }
}
