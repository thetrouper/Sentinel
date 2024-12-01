package me.trouper.sentinel.utils.trees;

import me.trouper.sentinel.utils.Text;

import java.util.Map;

public class HoverFormatter {

    public static String format(Node node) {
        StringBuilder sb = new StringBuilder();
        formatNode(sb, node, 0);
        return Text.color(sb.toString());
    }

    private static void formatNode(StringBuilder sb, Node node, int level) {
        if (level == 0) {
            //sb.append("&#3B3B3B]&#565656=&#717171=&#8C8C8C-&#A7A7A7- &6&l").append(node.title).append("&r &#A7A7A7-&#8C8C8C-&#717171=&#565656=&#3B3B3B[\n");
            sb.append("&8]==-- &6&l").append(node.title).append("&r &8--==[\n&r");
        } else {
            sb.append("&f&l").append(node.title).append("\n");
        }

        for (String text : node.texts) {
            text = text.replace("<hs>","&e&n");
            text = text.replace("<he>","&r&b");
            if (level == 0) {
                sb.append("&7").append(text).append("\n");
            } else {
                sb.append(" &8&l➥&r &7").append(text).append("\n");
            }
        }

        for (Map.Entry<String, String> entry : node.values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            key = key.replace("<hs>","&e&n");
            key = key.replace("<he>","&r&b");
            value = value.replace("<hs>","&e&n");
            value = value.replace("<he>","&r&b");
            if (level == 0) {
                sb.append("&7").append(key).append("&8: &b").append(value).append("\n&r");
            } else {
                sb.append(" &8&l➥&r &7").append(key).append("&8: &b").append(value).append("\n&r");
            }
        }

        for (Map.Entry<String, String> entry : node.fields.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            key = key.replace("<hs>","&e&n");
            key = key.replace("<he>","&r&b");
            value = value.replace("<hs>","&e&n");
            value = value.replace("<he>","&r&b");
            if (level == 0) {
                sb.append("&7").append(key).append("&8:\n&b ").append(value).append("\n&r");
            } else {
                sb.append(" &8&l➥&r &7").append(key).append("&8:\n  &b ").append(value).append("\n&r");
            }
        }

        for (Node child : node.children) {
            formatNode(sb, child, level + 1);
        }
    }
}

