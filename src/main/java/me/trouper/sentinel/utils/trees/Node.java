package me.trouper.sentinel.utils.trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    String title;
    List<String> texts;
    Map<String, String> values;
    Map<String, String> fields;
    List<Node> children;

    public Node(String title) {
        this.title = title;
        this.texts = new ArrayList<>();
        this.values = new HashMap<>();
        this.fields = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public void addTextLine(String text) {
        this.texts.add(text);
    }

    public void addKeyValue(String name, String value) {
        this.values.put(name, value);
    }

    public void addField(String title, String value) {
        this.fields.put(title,value);
    }

    public void addChild(Node child) {
        this.children.add(child);
    }
}

