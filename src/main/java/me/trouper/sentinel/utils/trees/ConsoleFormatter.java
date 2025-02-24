package me.trouper.sentinel.utils.trees;

import me.trouper.sentinel.utils.Text;

import java.util.Map;

public class ConsoleFormatter {

    public static String format(Node node) {
        StringBuilder sb = new StringBuilder();
        formatNode(sb, node, 0);
        return sb.toString();
    }

    private static void formatNode(StringBuilder sb, Node node, int level) {
        if (level == 0) {
            sb.append("]==-- ").append(node.title).append(" --==[\n");
        } else {
            sb.append(node.title).append("\n");
        }

        node.texts = node.texts.reversed();
        for (String text : node.texts) {
            text = Text.removeColors(text);
            text = text.replace("<hs>"," > ");
            text = text.replace("<he>"," < ");
            if (level == 0) {
                sb.append(text).append("\n");
            } else {
                sb.append(" ➥ ").append(text).append("\n");
            }
        }

        for (Map.Entry<String, String> entry : node.values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            key = Text.removeColors(key);
            value = Text.removeColors(value);
            key = key.replace("<hs>"," > ");
            key = key.replace("<he>"," < ");
            value = value.replace("<hs>"," > ");
            value = value.replace("<he>"," < ");
            if (level == 0) {
                sb.append(key).append(": ").append(value).append("\n");
            } else {
                sb.append(" ➥ ").append(key).append(": ").append(value).append("\n");
            }
        }

        for (Map.Entry<String, String> entry : node.fields.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            key = Text.removeColors(key);
            value = Text.removeColors(value);
            key = key.replace("<hs>"," > ");
            key = key.replace("<he>"," < ");
            value = value.replace("<hs>"," > ");
            value = value.replace("<he>"," < ");
            if (level == 0) {
                sb.append(key).append(":\n ").append(value).append("\n");
            } else {
                sb.append(" ➥ ").append(key).append(":\n ").append(value).append("\n");
            }
        }

        for (Node child : node.children) {
            formatNode(sb, child, level + 1);
        }
    }
}
