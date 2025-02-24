package me.trouper.sentinel.utils.trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    public String title;
    public List<String> texts;
    public Map<String, String> values;
    public Map<String, String> fields;
    public List<Node> children;

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

