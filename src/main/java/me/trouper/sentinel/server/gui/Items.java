package me.trouper.sentinel.server.gui;

import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
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
            .name(Text.color("&7"))
            .build();

    public static final ItemStack GREEN = ItemBuilder.create()
            .material(Material.LIME_STAINED_GLASS_PANE)
            .name(Text.color("&7"))
            .build();

    public static final ItemStack RED = ItemBuilder.create()
            .material(Material.RED_STAINED_GLASS_PANE)
            .name(Text.color("&7"))
            .build();

    public static final ItemStack BACK = ItemBuilder.create()
            .material(Material.ARROW)
            .name(Text.color("&cBack"))
            .lore(Text.color("&8&l➥&7 Return to the previous page"))
            .build();

    public static final ItemStack CREDITS = ItemBuilder.create()
            .material(Material.SHIELD)
            .name(Text.color("&6&lSentinel &8&l|&f Anti-Nuke"))
            .lore(" ")
            .lore(Text.color("&bVersion&7: &f%s".formatted(Sentinel.getInstance().getDescription().getVersion())))
            .lore(Text.color("&bLicensed to&7: &f%s".formatted(Sentinel.getInstance().nonce)))
            .lore(" ")
            .lore(Text.color("&e&nAuthor(s)&r&e: &e%s").formatted(Sentinel.getInstance().getDescription().getAuthors()))
            .enchant(Enchantment.PROTECTION,64)
            .flag(ItemFlag.HIDE_ENCHANTS)
            .build();

    public static final ItemStack CONFIG = ItemBuilder.create()
            .material(Material.PISTON)
            .name(Text.color("&6&lEdit Config"))
            .lore(Text.color("&8&l➥&7 Click this if you hate JSON."))
            .enchant(Enchantment.PROTECTION,64)
            .flag(ItemFlag.HIDE_ENCHANTS)
            .build();

    public static final ItemStack CHAT_CONFIG = ItemBuilder.create()
            .material(Material.HOPPER)
            .name(Text.color("&bChat Config"))
            .lore(Text.color("&8&l➥&7 Spam Filter"))
            .lore(Text.color("&8&l➥&7 Profanity Filter"))
            .lore(Text.color("&8&l➥&7 Unicode Filter"))
            .lore(Text.color("&8&l➥&7 URL Filter"))
            .enchant(Enchantment.PROTECTION,64)
            .flag(ItemFlag.HIDE_ENCHANTS)
            .build();

    public static final ItemStack ANTI_NUKE_CONFIG = ItemBuilder.create()
            .material(Material.TNT)
            .name(Text.color("&cAnti-Nuke Config"))
            .lore(Text.color("&8&l➥&7 Manage all violations"))
            .enchant(Enchantment.PROTECTION,64)
            .flag(ItemFlag.HIDE_ENCHANTS)
            .build();
    
    public static final ItemStack WHITELIST = ItemBuilder.create()
            .material(Material.TNT)
            .name(Text.color("&aCommand Block Whitelist"))
            .lore(Text.color("&8&l➥&7 Manage running command blocks"))
            .enchant(Enchantment.PROTECTION, 64)
            .flag(ItemFlag.HIDE_ENCHANTS)
            .build();

    public static ItemStack configItem(String valueName, Material material, String description) {
        ServerUtils.verbose("Items#configItem: Creating a config item:\n Value Name -> %s\nMaterial in use -> %s".formatted(valueName,material.toString()));

        List<String> desc = Arrays.stream(description.split("\n")).toList();

        ItemBuilder item = ItemBuilder.create();
        item.material(material);
        item.name(Text.color("&6%s".formatted(valueName)));
        for (String s : desc) {
            item.lore(Text.color("&e%s".formatted(s)));
        }
        item.lore(Text.color("&8&l➥&7 Click to set a &nnew&r&7 value."));
        item.lore(Text.color("&8&l➥&7 Current Value: &b_ORIGINAL_"));

        return item.build();
    }



    public static ItemStack stringListItem(Iterable<String> values, Material material, String valueName, String description) {
        ServerUtils.verbose("Items#stringListItem: Creating a config item:\n Value Name -> %s\nMaterial in use -> %s".formatted(valueName,material.toString()));
        ItemBuilder itemBuilder = ItemBuilder.create();
        itemBuilder.material(material);
        itemBuilder.name(Text.color("&6%s".formatted(valueName)));
        List<String> desc = Arrays.stream(description.split("\n")).toList();
        for (String s : desc) {
            itemBuilder.lore(Text.color("&e%s".formatted(s)));
        }
        itemBuilder.lore(Text.color("&8&l➥&7 Left-Click to add a new value."));
        itemBuilder.lore(Text.color("&8&l➥&7 Right-Click to clear values."));
        itemBuilder.lore(Text.color("&8&l➥&7 Current Values: "));
        itemBuilder.flag(ItemFlag.HIDE_ATTRIBUTES);

        for (String value : values) {
            itemBuilder.lore(Text.color("&9 - &b%s".formatted(value)));
        }

        return itemBuilder.build();
    }

    public static ItemStack stringItem(String originalValue, ItemStack originalItem) {
        ServerUtils.verbose("Items#stringItem Creating a string item:\n Value -> %s".formatted(originalValue));

        if (originalItem == null || !originalItem.hasItemMeta()) return originalItem;
        ItemMeta meta = originalItem.getItemMeta();
        if (meta == null || !meta.hasLore()) return originalItem;
        List<String> lore = meta.getLore();
        if (lore == null) return originalItem;

        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            ServerUtils.verbose("Items#stringItem Looping through lore line: %s/%s".formatted(i,lore.size()));
            if (line.contains("_ORIGINAL_")) {
                try {
                    ServerUtils.verbose("Items#stringItem Found a lore on line %s, making replacement value".formatted(i));
                    String replace = line.replace("_ORIGINAL_", originalValue);
                    ServerUtils.verbose("Items#stringItem After replacement -> %s".formatted(replace));
                    lore.set(i,replace);
                    ServerUtils.verbose("Items#stringItem Just replaced line %s -> %s".formatted(i,lore.get(i)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ServerUtils.verbose("Items#stringItem end of loop %s. continue?".formatted(i));
        }

        ServerUtils.verbose("Items#stringItem Broke out of loop, setting the lore");
        meta.setLore(lore);
        ServerUtils.verbose("Items#stringItem Setting the meta");
        originalItem.setItemMeta(meta);
        ServerUtils.verbose("Items#stringItem Returning the item");
        return originalItem;
    }

    public static ItemStack booleanItem(boolean originalValue, ItemStack originalItem) {
        ServerUtils.verbose("Items#booleanItem Creating a string item:\n Value -> %s".formatted(originalValue));

        if (originalItem == null || !originalItem.hasItemMeta()) return originalItem;
        ItemMeta meta = originalItem.getItemMeta();
        if (meta == null || !meta.hasLore()) return originalItem;
        List<String> lore = meta.getLore();
        if (lore == null) return originalItem;

        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            ServerUtils.verbose("Items#booleanItem Looping through lore line: %s/%s".formatted(i,lore.size()));
            if (line.contains("_ORIGINAL_")) {
                try {
                    ServerUtils.verbose("Items#booleanItem Found a lore on line %s, making replacement value".formatted(i));
                    String replace = line.replace("_ORIGINAL_", "" + originalValue);
                    ServerUtils.verbose("Items#booleanItem After replacement -> %s".formatted(replace));
                    lore.set(i,replace);
                    ServerUtils.verbose("Items#booleanItem Just replaced line %s -> %s".formatted(i,lore.get(i)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ServerUtils.verbose("Items#booleanItem end of loop %s. continue?".formatted(i));
        }

        ServerUtils.verbose("Items#booleanItem Broke out of loop, setting the lore");
        meta.setLore(lore);
        ServerUtils.verbose("Items#booleanItem Setting the meta");
        originalItem.setItemMeta(meta);
        ServerUtils.verbose("Items#booleanItem Returning the item");
        return originalItem;
    }

    public static ItemStack intItem(int originalValue, ItemStack originalItem) {
        ServerUtils.verbose("Items#intitem Creating a string item:\n Value -> %s".formatted(originalValue));

        if (originalItem == null || !originalItem.hasItemMeta()) return originalItem;
        ItemMeta meta = originalItem.getItemMeta();
        if (meta == null || !meta.hasLore()) return originalItem;
        List<String> lore = meta.getLore();
        if (lore == null) return originalItem;

        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            ServerUtils.verbose("Items#intitem Looping through lore line: %s/%s".formatted(i,lore.size()));
            if (line.contains("_ORIGINAL_")) {
                try {
                    ServerUtils.verbose("Items#intitem Found a lore on line %s, making replacement value".formatted(i));
                    String replace = line.replace("_ORIGINAL_", "" + originalValue);
                    ServerUtils.verbose("Items#intitem After replacement -> %s".formatted(replace));
                    lore.set(i,replace);
                    ServerUtils.verbose("Items#intitem Just replaced line %s -> %s".formatted(i,lore.get(i)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ServerUtils.verbose("Items#intitem end of loop %s. continue?".formatted(i));
        }

        ServerUtils.verbose("Items#intitem Broke out of loop, setting the lore");
        meta.setLore(lore);
        ServerUtils.verbose("Items#intitem Setting the meta");
        originalItem.setItemMeta(meta);
        ServerUtils.verbose("Items#intitem Returning the item");
        return originalItem;
    }
}
