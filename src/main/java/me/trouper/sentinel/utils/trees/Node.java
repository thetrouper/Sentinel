package me.trouper.sentinel.utils.trees;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    public Component title;
    public List<Component> texts;
    public Map<Component, Component> values;
    public Map<Component, Component> fields;
    public List<Node> children;

    public Node(Component title) {
        this.title = title;
        this.texts = new ArrayList<>();
        this.values = new HashMap<>();
        this.fields = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public Node(String title) {
        this(Component.text(title));
    }
    
    public Node() {
        this(Component.text("Sentinel"));
    }

    public void addTextLine(Component text) {
        this.texts.add(text);
    }

    public void addTextLine(String text) {
        this.texts.add(Component.text(text));
    }

    public void addKeyValue(Component key, Component value) {
        this.values.put(key, value);
    }

    public void addKeyValue(String key, String value) {
        this.values.put(Component.text(key), Component.text(value));
    }

    public void addField(Component title, Component value) {
        this.fields.put(title, value);
    }

    public void addField(String title, String value) {
        this.fields.put(Component.text(title), Component.text(value));
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public static Component createHighlightedText(String text) {
        return Component.text(text)
                .color(NamedTextColor.YELLOW)
                .decorate(TextDecoration.UNDERLINED);
    }

    public static Component parseLegacyText(String text) {
        if (text.contains("█HS█") || text.contains("█HE█")) {
            String[] parts = text.split("(?=█HS█)|(?<=█HE█)");
            Component result = Component.empty();

            for (String part : parts) {
                if (part.startsWith("█HS█") && part.endsWith("█HE█")) {
                    part = part.substring(4, part.length() - 4);
                    Component segment = Component.text(part)
                            .color(NamedTextColor.YELLOW)
                            .decorate(TextDecoration.UNDERLINED);
                    result = result.append(segment);
                    continue;
                }
                
                result = result.append(Component.text(part));
            }
            return result;
        }

        return Component.text(text);
    }
}