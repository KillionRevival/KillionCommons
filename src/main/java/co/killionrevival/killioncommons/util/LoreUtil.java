package co.killionrevival.killioncommons.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;

import java.util.ArrayList;
import java.util.List;

public class LoreUtil {
    /**
     * Create a list of lore components from a list of strings.
     * @param strings List of strings to transform into TextComponents
     * @param overallTextStyle Style that will be applied to the text components
     * @return A list of TextComponents with the style provided.
     */
    public static List<Component> createLoreList(
            final List<String> strings,
            final Style overallTextStyle
    ) {
        return createLoreList(strings, overallTextStyle, null);
    }

    /**
     * Create a list of lore components from a list of strings.
     * @param strings List of strings to transform into TextComponents
     * @param overallTextStyle Style that will be applied to the text components
     * @param wrapTextAt If not null, will wrap each line of text at a certain number of characters.
     * @return A list of TextComponents with the style provided, and wrapped at the number of chars = wrapTextAt.
     */
    public static List<Component> createLoreList(
            final List<String> strings,
            final Style overallTextStyle,
            final Integer wrapTextAt
    ) {
        final List<Component> componentList = new ArrayList<>();

        for (final String text : strings) {
            final TextComponent component = Component.text(text).style(overallTextStyle);
            if (wrapTextAt != null) {
                componentList.addAll(wrapText(List.of(component), wrapTextAt));
                continue;
            }

            componentList.add(component);
        }

        return componentList;
    }

    /**
     * Wraps a list of text components and breaks up the lines so that none of the lines exceed the
     * maxCharPerLine.
     * If a line is broken, the new line will maintain the same style as the line that was broken.
     * @param componentsToWrap List of components to wrap
     * @param maxCharPerLine How many characters each line can have before breaking
     * @return A new list of components with extra lines for each line that exceeded the wrap.
     */
    public static List<TextComponent> wrapText(
            final List<TextComponent> componentsToWrap,
            final int maxCharPerLine
    ) {
        final List<TextComponent> wrappedLines = new ArrayList<>();

        componentsToWrap.forEach( component -> {
            final Style componentStyle = component.style();
            final String content = component.content();

            // just add the current line if we don't need to wrap
            if (content.length() < maxCharPerLine) {
                wrappedLines.add(component);
                return;
            }

            StringBuilder currentLine = new StringBuilder();

            for (final String word : content.split(" ")) {
                // wrap if we are going to exceed maxCharPerLine
                if (currentLine.length() + word.length() + 1 > maxCharPerLine) {
                    wrappedLines.add(Component.text(currentLine.toString().trim()).style(componentStyle));
                    currentLine = new StringBuilder();
                }
                currentLine.append(word).append(" ");
            }

            if (!currentLine.isEmpty()) {
                wrappedLines.add(Component.text(currentLine.toString().trim()).style(componentStyle));
            }
        });

        return wrappedLines;
    }
}
