package me.trouper.sentinel.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class ImageUtils {

    public static List<Component> makeImage(String URL) {
        try {
            java.net.URL url = new URL(URL);
            BufferedImage img = ImageIO.read(url);
            List<Component> lines = new ArrayList<>();
            Component message = Component.text("");

            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int rgb = img.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    String hex = String.format("#%02x%02x%02x", red, green, blue);
                    message = message.append(Component.text("█").color(TextColor.fromHexString(hex)));
                }
                lines.add(message);
                message = Component.text("");
            }
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
