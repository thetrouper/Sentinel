package me.trouper.sentinel.utils.trees;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConsoleFormatter {

    public static String format(Node node) {
        StringBuilder sb = new StringBuilder();
        formatNode(sb, node, 0);
        return sb.toString();
    }

    public static Component formatComponent(Node node) {
        return formatNodeComponent(node, 0);
    }

    private static void formatNode(StringBuilder sb, Node node, int level) {
        if (level == 0) {
            sb.append("]==-- ")
                    .append(componentToPlainText(node.title))
                    .append(" --==[\n");
        } else {
            sb.append(componentToPlainText(node.title)).append("\n");
        }

        // Reverse the texts list for display
        List<Component> reversedTexts = new ArrayList<>(node.texts);
        Collections.reverse(reversedTexts);

        for (Component text : reversedTexts) {
            String plainText = processComponentForConsole(text);
            if (level == 0) {
                sb.append(plainText).append("\n");
            } else {
                sb.append(" ➥ ").append(plainText).append("\n");
            }
        }

        for (Map.Entry<Component, Component> entry : node.values.entrySet()) {
            String key = processComponentForConsole(entry.getKey());
            String value = processComponentForConsole(entry.getValue());

            if (level == 0) {
                sb.append(key).append(": ").append(value).append("\n");
            } else {
                sb.append(" ➥ ").append(key).append(": ").append(value).append("\n");
            }
        }

        for (Map.Entry<Component, Component> entry : node.fields.entrySet()) {
            String key = processComponentForConsole(entry.getKey());
            String value = processComponentForConsole(entry.getValue());

            if (level == 0) {
                sb.append(key).append(":\n ").append(value).append("\n");
            } else {
                sb.append(" ➥ ").append(key).append(":\n  ").append(value).append("\n");
            }
        }

        for (Node child : node.children) {
            formatNode(sb, child, level + 1);
        }
    }

    private static Component formatNodeComponent(Node node, int level) {
        Component result = Component.empty();

        if (level == 0) {
            result = result.append(Component.text("]==-- "))
                    .append(node.title)
                    .append(Component.text(" --==["))
                    .append(Component.newline());
        } else {
            result = result.append(node.title).append(Component.newline());
        }

        List<Component> reversedTexts = new ArrayList<>(node.texts);
        Collections.reverse(reversedTexts);

        for (Component text : reversedTexts) {
            Component processedText = processComponentForConsoleComponent(text);
            if (level == 0) {
                result = result.append(processedText).append(Component.newline());
            } else {
                result = result.append(Component.text(" ➥ "))
                        .append(processedText)
                        .append(Component.newline());
            }
        }

        for (Map.Entry<Component, Component> entry : node.values.entrySet()) {
            Component key = processComponentForConsoleComponent(entry.getKey());
            Component value = processComponentForConsoleComponent(entry.getValue());

            if (level == 0) {
                result = result.append(key)
                        .append(Component.text(": "))
                        .append(value)
                        .append(Component.newline());
            } else {
                result = result.append(Component.text(" ➥ "))
                        .append(key)
                        .append(Component.text(": "))
                        .append(value)
                        .append(Component.newline());
            }
        }

        for (Map.Entry<Component, Component> entry : node.fields.entrySet()) {
            Component key = processComponentForConsoleComponent(entry.getKey());
            Component value = processComponentForConsoleComponent(entry.getValue());

            if (level == 0) {
                result = result.append(key)
                        .append(Component.text(":"))
                        .append(Component.newline())
                        .append(Component.text(" "))
                        .append(value)
                        .append(Component.newline());
            } else {
                result = result.append(Component.text(" ➥ "))
                        .append(key)
                        .append(Component.text(":"))
                        .append(Component.newline())
                        .append(Component.text("  "))
                        .append(value)
                        .append(Component.newline());
            }
        }

        for (Node child : node.children) {
            result = result.append(formatNodeComponent(child, level + 1));
        }

        return result;
    }

    private static String componentToPlainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    private static String processComponentForConsole(Component component) {
        String text = componentToPlainText(component);
        text = text.replace("█HS█", " > ");
        text = text.replace("█HE█", " < ");
        return text;
    }

    private static Component processComponentForConsoleComponent(Component component) {
        // TODO: Parse console components 
        return component;
    }
}