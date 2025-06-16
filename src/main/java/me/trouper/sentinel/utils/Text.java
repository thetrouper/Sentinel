package me.trouper.sentinel.utils;

import me.trouper.sentinel.server.Main;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Text implements Main {

    /**
     * Messages an audience applying pallet formating to the text and placeholders. Placeholders are zero-indexed and curly braced. {0}, {1}, {2}...
     * @param pallet The colors to use for text and arguments.
     * @param audience Any audience.
     * @param text The message to format
     * @param args Qualified placeholders to color.
     */
    public static void messageAny(Pallet pallet, Audience audience, String text, Object... args) {
        message(pallet, audience, color(text), Arrays.stream(args).map(object -> Component.text(String.valueOf(object))).toArray(ComponentLike[]::new));
    }

    /**
     * Messages an audience applying pallet formating to the component and placeholders. Placeholders are zero-indexed and curly braced. {0}, {1}, {2}...
     * Preserves existing formatting like click events and hover events.
     * @param pallet The colors to use for text and arguments.
     * @param audience Any audience.
     * @param text The component message to format
     * @param args Qualified placeholders to color.
     */
    public static void message(Pallet pallet, Audience audience, ComponentLike text, ComponentLike... args) {
        ComponentLike message = getMessage(pallet, text, args);
        audience.sendMessage(message);
        if (audience instanceof Player p) p.playSound(p.getLocation(), pallet.sound.sound, SoundCategory.VOICE, 10f, pallet.sound.pitch);
    }

    /**
     * Gets the component form of a message, applying pallet formating to the text and placeholders. Placeholders are zero-indexed and curly braced. {0}, {1}, {2}...
     * @param pallet The colors to use for text and arguments.
     * @param text The message to format
     * @param args Qualified placeholders to color.
     * @return The final component, prefixed.
     */
    public static Component getMessageAny(Pallet pallet, String text, Object... args) {
        return getMessage(pallet,color(text), Arrays.stream(args).map(arg -> color(String.valueOf(arg))).toArray(ComponentLike[]::new));
    }

    /**
     * Gets the component form of a message, applying pallet formating to the component and placeholders. Placeholders are zero-indexed and curly braced. {0}, {1}, {2}...
     * Preserves existing formatting like click events and hover events.
     * @param pallet The colors to use for text and arguments.
     * @param text The component message to format
     * @param args Qualified placeholders to color.
     * @return The final component, prefixed.
     */
    public static Component getMessage(Pallet pallet, ComponentLike text, ComponentLike... args) {
        return prefix(format(pallet, text, args));
    }

    /**
     * Prefixes a component with the plugin's configurable prefix.
     * @param text The component to prefix
     * @return A component with the prefix inserted before it. No spaces are added.
     */
    public static Component prefix(ComponentLike text) {
        if (LegacyComponentSerializer.legacySection().serialize(text.asComponent()).startsWith(main.io().mainConfig.plugin.prefix)) return text.asComponent();
        return color(main.io().mainConfig.plugin.prefix).append(text);
    }

    /**
     * Wrapper for LegacyComponentSerializer, using ampersand (&) codes.
     * @param msg the legacy text
     * @return The deserialize component
     */
    public static Component color(String msg) {
        if (msg.contains("§")) return LegacyComponentSerializer.legacySection().deserialize(msg);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(msg);
    }
    
    public static String legacyColor(String ampersands) {
        return ampersands.replaceAll("&","§");
    }

    public static Component format(Pallet pallet, String text, Object... args) {
        return format(pallet,Component.text(text), Arrays.stream(args).map(arg->Component.text(arg.toString())).toArray(Component[]::new));
    }
    
    public static Component format(Pallet pallet, ComponentLike text, ComponentLike... args) {
        Component resultComponent = text.asComponent().color(pallet.mainText);

        if (args == null || args.length == 0) {
            return resultComponent;
        }

        for (int i = 0; i < args.length; i++) {
            Component argument = args[i].asComponent();
            if (shouldRecolor(argument.asComponent())) {
                TextColor newColor = getArgColor(pallet, i);
                argument = argument.color(newColor);
            }

            TextReplacementConfig replacementConfig = TextReplacementConfig.builder()
                    .matchLiteral("{" + i + "}")
                    .replacement(argument)
                    .build();

            resultComponent = resultComponent.replaceText(replacementConfig);
        }

        return resultComponent;
    }

    /**
     * A dummy method to determine if an argument component should have its color
     * overridden by the pallet.
     *
     * @param component The component to check.
     * @return Currently always returns true, indicating recoloring should occur.
     */
    private static boolean shouldRecolor(Component component) {
        return true;
    }

    public static String removeColors(String input) {
        if (input == null) return null;
        
        input = input.replaceAll("(?i)[&§][0-9a-fk-or]", "");

        input = input.replaceAll("(?i)[&§]#[a-f0-9]{6}", "");

        input = input.replaceAll("(?i)§x(§[a-f0-9]){6}", "");

        return input;
    }

    public static Component removeColors(ComponentLike input) {
        if (input == null) return Component.text("");
        
        String plainText = PlainTextComponentSerializer.plainText().serialize(input.asComponent());

        return Component.text(plainText);
    }

    /**
     * Gets the appropriate argument color based on the argument index.
     */
    private static TextColor getArgColor(Pallet pallet, int argIndex) {
        return switch (argIndex) {
            case 1 -> pallet.arg2;
            case 2 -> pallet.arg3;
            default -> pallet.argDefault;
        };
    }

    public enum Pallet {
        ERROR(
                TextColor.color(0xD3A6A4),
                TextColor.color(0xFFF1AE),          
                TextColor.color(0xFF796D),          
                TextColor.color(0xC62828),        
                new SoundData(Sound.BLOCK_NOTE_BLOCK_BASS, 1)
        ),
        WARNING(
                TextColor.color(0xFFF3CD),
                TextColor.color(0xFFF9F5),       
                TextColor.color(0xFFD54F),        
                TextColor.color(0xFFA000),         
                new SoundData(Sound.BLOCK_NOTE_BLOCK_BIT, 0.5F)
        ),
        INFO(
                TextColor.color(0xBBDEFB),
                TextColor.color(0xD2D0EA),
                TextColor.color(0x64B5F6),    
                TextColor.color(0x1976D2),            
                new SoundData(Sound.BLOCK_NOTE_BLOCK_CHIME, 0.7F)
        ),
        SUCCESS(
                TextColor.color(0xCDFFC7),
                TextColor.color(0xFFFFFF),     
                TextColor.color(0xB0FFE3),     
                TextColor.color(0x63CD83),   
                new SoundData(Sound.BLOCK_NOTE_BLOCK_PLING, 1.5F)
        ),
        NEUTRAL(
                TextColor.color(0xD3D3D3),
                TextColor.color(0xFFFFFF),
                TextColor.color(0xFFB3F8),
                TextColor.color(0xE280FF),
                new SoundData(Sound.BLOCK_NOTE_BLOCK_BELL, 1)
        ),
        LOCATION(
                TextColor.color(0xAAAAAA), 
                TextColor.color(0xFFB0C1), 
                TextColor.color(0xB6F5B6), 
                TextColor.color(0xB0C1FF),
                new SoundData(Sound.UI_HUD_BUBBLE_POP, 2)
        );

        private final TextColor mainText;
        private final TextColor argDefault;
        private final TextColor arg2;
        private final TextColor arg3;
        private final SoundData sound;

        Pallet(TextColor mainText, TextColor argDefault, TextColor arg2, TextColor arg3, SoundData sound) {
            this.mainText = mainText;
            this.argDefault = argDefault;
            this.arg2 = arg2;
            this.arg3 = arg3;
            this.sound = sound;
        }
    }

    public record SoundData(Sound sound, float pitch) {}
}