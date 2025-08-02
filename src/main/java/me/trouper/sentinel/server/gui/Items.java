package me.trouper.sentinel.server.gui;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.ItemBuilder;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.OldTXT;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Items {

    public static final ItemStack BLANK = ItemBuilder.create()
            .material(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
            .displayName(Component.empty())
            .build();

    public static final ItemStack GREEN = ItemBuilder.create()
            .material(Material.LIME_STAINED_GLASS_PANE)
            .displayName(Component.empty())
            .build();

    public static final ItemStack RED = ItemBuilder.create()
            .material(Material.RED_STAINED_GLASS_PANE)
            .displayName(Component.empty())
            .build();

    public static final ItemStack BACK = ItemBuilder.create()
            .material(Material.ARROW)
            .displayName(Component.text("Back",NamedTextColor.RED).decoration(TextDecoration.ITALIC,false))
            .loreComponent(Text.color("&8&l➥&7 Return to the previous page"))
            .build();

    public static final ItemStack CREDITS = ItemBuilder.create()
            .material(Material.SHIELD)
            .displayName(Text.color("&6&lSentinel &8&l|&f Anti-Nuke").decoration(TextDecoration.ITALIC,false))
            .loreComponent(
                    Component.empty(),
                    Text.color("&bVersion&7: &f%s".formatted(Sentinel.getInstance().version)),
                    Text.color("&bLicensed to&7: &f%s".formatted(Sentinel.getInstance().nonce)),
                    Component.empty(),
                    Text.color("&e&nAuthor(s)&r&e: &e%s".formatted(Sentinel.getInstance().getPluginMeta().getAuthors()))
            )
            .enchant(Enchantment.PROTECTION,64)
            .flags(ItemFlag.HIDE_ENCHANTS)
            .build();

    public static final ItemStack CONFIG = ItemBuilder.create()
            .displayName(Component.text("Edit Config",Style.style(TextDecoration.BOLD, NamedTextColor.GOLD)).decoration(TextDecoration.ITALIC,false))
            .material(Material.PISTON)
            .loreComponent(Text.color("&8&l➥&7 Click this if you hate JSON."))
            .enchant(Enchantment.PROTECTION,64)
            .flags(ItemFlag.HIDE_ENCHANTS)
            .build();

    public static final ItemStack CHAT_CONFIG = ItemBuilder.create()
            .material(Material.HOPPER)
            .displayName(Component.text("Chat Config",NamedTextColor.AQUA).decoration(TextDecoration.ITALIC,false))
            .loreComponent(
                    Text.color("&8&l➥&7 Spam Filter"),
                    Text.color("&8&l➥&7 Profanity Filter"),
                    Text.color("&8&l➥&7 Unicode Filter"),
                    Text.color("&8&l➥&7 URL Filter")
            )
            .enchant(Enchantment.PROTECTION,64)
            .flags(ItemFlag.HIDE_ENCHANTS)
            .build();

    public static final ItemStack ANTI_NUKE_CONFIG = ItemBuilder.create()
            .material(Material.TNT)
            .displayName(Component.text("Anti-Nuke Config", NamedTextColor.RED).decoration(TextDecoration.ITALIC,false))
            .loreComponent(Text.color("&8&l➥&7 Manage all violations"))
            .enchant(Enchantment.PROTECTION, 64)
            .flags(ItemFlag.HIDE_ENCHANTS)
            .build();

    public static final ItemStack WHITELIST = ItemBuilder.create()
            .material(Material.TNT)
            .displayName(Component.text("Command Block Whitelist", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC,false))
            .loreComponent(Text.color("&8&l➥&7 Manage running command blocks"))
            .enchant(Enchantment.PROTECTION, 64)
            .flags(ItemFlag.HIDE_ENCHANTS)
            .build();

    public static final ItemStack NBT = ItemBuilder.create()
            .material(Material.HONEY_BOTTLE)
            .displayName(Component.text("NBT Honeypot", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC,false))
            .loreComponent(Text.color("&8&l➥&7 View caught NBT"))
            .enchant(Enchantment.PROTECTION, 64)
            .flags(ItemFlag.HIDE_ENCHANTS)
            .build();

    public static ItemStack configItem(String valueName, Material material, String description) {
        ServerUtils.verbose("Creating a config item:\n Value Name -> %s\nMaterial in use -> %s",
                valueName, material.toString());

        List<String> desc = Arrays.stream(description.split("\n")).toList();

        ItemBuilder item = ItemBuilder.create()
                .material(material)
                .displayName(Component.text(valueName, NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false));

        for (String s : desc) {
            item.loreComponent(Component.text(s, NamedTextColor.YELLOW));
        }

        item.loreComponent(
                Text.color("&8&l➥&7 Click to set a &nnew&r&7 value."),
                Text.color("&8&l➥&7 Current Value: &b_ORIGINAL_")
        );

        return item.build();
    }

    public static ItemStack stringListItem(Iterable<String> values, Material material, String valueName, String description) {
        ServerUtils.verbose("Items#stringListItem: Creating a config item:\n Value Name -> %s\nMaterial in use -> %s",
                valueName, material.toString());

        ItemBuilder itemBuilder = ItemBuilder.create()
                .material(material)
                .displayName(Component.text(valueName, NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false));

        List<String> desc = Arrays.stream(description.split("\n")).toList();
        for (String s : desc) {
            itemBuilder.loreComponent(Component.text(s, NamedTextColor.YELLOW));
        }

        itemBuilder.loreComponent(
                Text.color("&8&l➥&7 Left-Click to add a new value."),
                Text.color("&8&l➥&7 Right-Click to clear values."),
                Text.color("&8&l➥&7 Current Values: ")
        ).flags(ItemFlag.HIDE_ATTRIBUTES);

        for (String value : values) {
            itemBuilder.loreComponent(
                    Component.text(" - ", NamedTextColor.BLUE)
                            .append(Component.text(value, NamedTextColor.AQUA))
            );
        }

        return itemBuilder.build();
    }

    public static ItemStack stringItem(String originalValue, ItemStack originalItem) {
        ServerUtils.verbose("Items#stringItem Creating a string item:\n Value -> %s", originalValue);

        if (originalItem == null || !originalItem.hasItemMeta()) return originalItem;
        ItemMeta meta = originalItem.getItemMeta();
        if (meta == null) return originalItem;

        List<Component> lore = meta.lore();
        if (lore == null) return originalItem;

        for (int i = 0; i < lore.size(); i++) {
            Component line = lore.get(i);
            String plainText = PlainTextComponentSerializer.plainText().serialize(line);

            ServerUtils.verbose("Items#stringItem Looping through lore line: %s/%s", i, lore.size());

            if (plainText.contains("_ORIGINAL_")) {
                try {
                    ServerUtils.verbose("Items#stringItem Found a lore on line %s, making replacement value", i);

                    Component newLine = line.replaceText(TextReplacementConfig.builder()
                            .matchLiteral("_ORIGINAL_")
                            .replacement(originalValue)
                            .build());

                    lore.set(i, newLine);
                    ServerUtils.verbose("Items#stringItem Just replaced line %s", i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ServerUtils.verbose("Items#stringItem end of loop %s. continue?", i);
        }

        ServerUtils.verbose("Items#stringItem Broke out of loop, setting the lore");
        meta.lore(lore);
        ServerUtils.verbose("Items#stringItem Setting the meta");
        originalItem.setItemMeta(meta);
        ServerUtils.verbose("Items#stringItem Returning the item");
        return originalItem;
    }

    public static ItemStack booleanItem(boolean originalValue, ItemStack originalItem) {
        return replaceOriginalValue(String.valueOf(originalValue), originalItem, "booleanItem");
    }

    public static ItemStack intItem(int originalValue, ItemStack originalItem) {
        return replaceOriginalValue(String.valueOf(originalValue), originalItem, "intItem");
    }

    private static ItemStack replaceOriginalValue(String value, ItemStack originalItem, String methodName) {
        ServerUtils.verbose("Items#%s Creating item with value: %s", methodName, value);

        if (originalItem == null || !originalItem.hasItemMeta()) return originalItem;
        ItemMeta meta = originalItem.getItemMeta();
        if (meta == null) return originalItem;

        List<Component> lore = meta.lore();
        if (lore == null) return originalItem;

        for (int i = 0; i < lore.size(); i++) {
            Component line = lore.get(i);
            String plainText = PlainTextComponentSerializer.plainText().serialize(line);

            ServerUtils.verbose("Items#%s Looping through lore line: %s/%s", methodName, i, lore.size());

            if (plainText.contains("_ORIGINAL_")) {
                try {
                    ServerUtils.verbose("Items#%s Found a lore on line %s, making replacement value", methodName, i);

                    Component newLine = line.replaceText(TextReplacementConfig.builder()
                            .matchLiteral("_ORIGINAL_")
                            .replacement(value)
                            .build());

                    lore.set(i, newLine);
                    ServerUtils.verbose("Items#%s Just replaced line %s", methodName, i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ServerUtils.verbose("Items#%s end of loop %s. continue?", methodName, i);
        }

        ServerUtils.verbose("Items#%s Broke out of loop, setting the lore", methodName);
        meta.lore(lore);
        ServerUtils.verbose("Items#%s Setting the meta", methodName);
        originalItem.setItemMeta(meta);
        ServerUtils.verbose("Items#%s Returning the item", methodName);
        return originalItem;
    }
}
