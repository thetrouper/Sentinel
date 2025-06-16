package me.trouper.sentinel.utils.trees;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Map;

public class HoverFormatter {

    public static Component format(Node node) {
        return formatNode(node, 0);
    }

    public static String formatLegacy(Node node) {
        Component component = format(node);
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    private static Component formatNode(Node node, int level) {
        Component result = Component.empty();

        if (level == 0) {
            Component titleLine = Component.text("]==-- ")
                    .color(NamedTextColor.DARK_GRAY)
                    .append(node.title.color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .append(Component.text(" --==[").color(NamedTextColor.DARK_GRAY))
                    .append(Component.newline());
            result = result.append(titleLine);
        } else {
            Component childTitle = node.title
                    .color(NamedTextColor.WHITE)
                    .decorate(TextDecoration.BOLD)
                    .append(Component.newline());
            result = result.append(childTitle);
        }

        for (Component text : node.texts) {
            Component processedText = processHighlightTags(text);
            if (level == 0) {
                result = result.append(processedText.color(NamedTextColor.GRAY))
                        .append(Component.newline());
            } else {
                result = result.append(Component.text(" ➥ ")
                                .color(NamedTextColor.DARK_GRAY)
                                .decorate(TextDecoration.BOLD))
                        .append(processedText.color(NamedTextColor.GRAY))
                        .append(Component.newline());
            }
        }

        for (Map.Entry<Component, Component> entry : node.values.entrySet()) {
            Component key = processHighlightTags(entry.getKey());
            Component value = processHighlightTags(entry.getValue());

            if (level == 0) {
                result = result.append(key.color(NamedTextColor.GRAY))
                        .append(Component.text(": ").color(NamedTextColor.DARK_GRAY))
                        .append(value.color(NamedTextColor.AQUA))
                        .append(Component.newline());
            } else {
                result = result.append(Component.text(" ➥ ")
                                .color(NamedTextColor.DARK_GRAY)
                                .decorate(TextDecoration.BOLD))
                        .append(key.color(NamedTextColor.GRAY))
                        .append(Component.text(": ").color(NamedTextColor.DARK_GRAY))
                        .append(value.color(NamedTextColor.AQUA))
                        .append(Component.newline());
            }
        }

        for (Map.Entry<Component, Component> entry : node.fields.entrySet()) {
            Component key = processHighlightTags(entry.getKey());
            Component value = processHighlightTags(entry.getValue());

            if (level == 0) {
                result = result.append(key.color(NamedTextColor.GRAY))
                        .append(Component.text(":").color(NamedTextColor.DARK_GRAY))
                        .append(Component.newline())
                        .append(Component.text(" ").append(value.color(NamedTextColor.AQUA)))
                        .append(Component.newline());
            } else {
                result = result.append(Component.text(" ➥ ")
                                .color(NamedTextColor.DARK_GRAY)
                                .decorate(TextDecoration.BOLD))
                        .append(key.color(NamedTextColor.GRAY))
                        .append(Component.text(":").color(NamedTextColor.DARK_GRAY))
                        .append(Component.newline())
                        .append(Component.text("  ")
                                .append(value.color(NamedTextColor.AQUA)))
                        .append(Component.newline());
            }
        }

        for (Node child : node.children) {
            result = result.append(formatNode(child, level + 1));
        }

        return result;
    }

    private static Component processHighlightTags(Component component) {
        // TODO: Process legacy highlight tags
        return component;
    }
}