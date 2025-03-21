package me.trouper.sentinel.server.functions.hotbar.items;

import me.trouper.sentinel.server.functions.hotbar.AbstractCheck;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class MetaCheck extends AbstractCheck<ItemStack> {

    @Override
    public boolean passes(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        // Name, lore, potion, attribute and enchantment checks.
        if (!config.allowName && meta.hasDisplayName()) {
            ServerUtils.verbose("Custom names not allowed.");
            return false;
        }
        if (!config.allowLore && meta.hasLore()) {
            ServerUtils.verbose("Custom lore not allowed.");
            return false;
        }
        if (!config.allowBooks && meta instanceof BookMeta) {
            ServerUtils.verbose("Item failed book check.");
            return false;
        }
        if (!config.allowPotions &&
                (item.getType().equals(Material.POTION) ||
                        item.getType().equals(Material.SPLASH_POTION) ||
                        item.getType().equals(Material.LINGERING_POTION))) {
            ServerUtils.verbose("Potions not allowed.");
            return false;
        }
        if (!config.allowAttributes && meta.hasAttributeModifiers()) {
            ServerUtils.verbose("Attribute modifiers not allowed.");
            return false;
        }
        if (config.globalMaxEnchant != 0 && new EnchantmentCheck().hasIllegalEnchants(item)) {
            ServerUtils.verbose("Illegal enchantments found.");
            return false;
        }
        // Recursion check for use-remainder items.
        if (meta.hasUseRemainder()) {
            if (!config.allowRecursion) {
                ServerUtils.verbose("Recursion not allowed.");
                return false;
            }
            if (meta.getUseRemainder() != null && !passes(meta.getUseRemainder())) {
                ServerUtils.verbose("Use remainder item failed check.");
                return false;
            }
        }

        // Bundle check – recursively check the contained items.
        if (item.getType().name().contains("_BUNDLE") && meta instanceof BundleMeta bm) {
            for (ItemStack bundleItem : bm.getItems()) {
                if (!passes(bundleItem)) return false;
            }
        }

        return true;
    }
}
