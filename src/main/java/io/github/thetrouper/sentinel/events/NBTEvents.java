package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.data.Action;
import io.github.thetrouper.sentinel.data.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class NBTEvents implements Listener {
    @EventHandler
    private void onNBTPull(InventoryCreativeEvent e) {
        if (Config.preventNBT) {
            if (!(e.getWhoClicked() instanceof Player p)) {
                return;
            }
            if (e.getCursor() == null) return;
            ItemStack i = e.getCursor();
            if (!Sentinel.isTrusted(p)) {
                if (e.getCursor().getItemMeta() == null) return;
                if (i.hasItemMeta() && i.getItemMeta() != null) {
                    if (!itemPasses(i)) {
                        Action a = new Action.Builder()
                                .setEvent(e)
                                .setAction(ActionType.NBT)
                                .setPlayer(Bukkit.getPlayer(e.getWhoClicked().getName()))
                                .setItem(e.getCursor())
                                .setDenied(Config.preventNBT)
                                .setDeoped(Config.deop)
                                .setPunished(Config.nbtPunish)
                                .setRevertGM(Config.preventNBT)
                                .setNotifyConsole(true)
                                .setNotifyTrusted(true)
                                .setnotifyDiscord(Config.logNBT)
                                .execute();
                    }
                }
            }
        }
    }

    private boolean itemPasses(ItemStack i) {
        if (i.hasItemMeta()) {
            ItemMeta meta = i.getItemMeta();
            if (!Config.allowName && meta.hasDisplayName()) return false;
            if (!Config.allowLore && meta.hasLore()) return false;
            if (!Config.allowAttributes && meta.hasAttributeModifiers()) return false;
            if (Config.globalMaxEnchant == 0 && hasIllegalEnchants(i)) return false;
        }
        return true;
    }
    private boolean hasIllegalEnchants(ItemStack i) {
        if (i.hasItemMeta() && i.getItemMeta().hasEnchants()) {
            final ItemMeta meta = i.getItemMeta();
            final Map<Enchantment, Integer> enchantments = meta.getEnchants();
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();

                if (level > Config.globalMaxEnchant) {
                    return true;
                }
            }

            // ALL
            if (meta.hasEnchant(Enchantment.MENDING)) {
                final int level = meta.getEnchantLevel(Enchantment.MENDING);
                return level > Config.maxMending || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.DURABILITY)) {
                final int level = meta.getEnchantLevel(Enchantment.DURABILITY);
                return level > Config.maxUnbreaking || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.VANISHING_CURSE)) {
                final int level = meta.getEnchantLevel(Enchantment.VANISHING_CURSE);
                return level > Config.maxVanishing || level > Config.globalMaxEnchant;
            }

            // ARMOR
            if (meta.hasEnchant(Enchantment.BINDING_CURSE)) {
                final int level = meta.getEnchantLevel(Enchantment.BINDING_CURSE);
                return level > Config.maxCurseOfBinding || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.WATER_WORKER)) {
                final int level = meta.getEnchantLevel(Enchantment.WATER_WORKER);
                return level > Config.maxAquaAffinity || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
                return level > Config.maxProtection || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_EXPLOSIONS)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_EXPLOSIONS);
                return level > Config.maxBlastProtection || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.DEPTH_STRIDER)) {
                final int level = meta.getEnchantLevel(Enchantment.DEPTH_STRIDER);
                return level > Config.maxDepthStrider || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_FALL)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_FALL);
                return level > Config.maxFeatherFalling || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_FIRE)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_FIRE);
                return level > Config.maxFireProtection || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.FROST_WALKER)) {
                final int level = meta.getEnchantLevel(Enchantment.FROST_WALKER);
                return level > Config.maxFrostWalker || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_PROJECTILE)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_PROJECTILE);
                return level > Config.maxProjectileProtection || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.OXYGEN)) {
                final int level = meta.getEnchantLevel(Enchantment.OXYGEN);
                return level > Config.maxRespiration || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.SOUL_SPEED)) {
                final int level = meta.getEnchantLevel(Enchantment.SOUL_SPEED);
                return level > Config.maxSoulSpeed || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.THORNS)) {
                final int level = meta.getEnchantLevel(Enchantment.THORNS);
                return level > Config.maxThorns || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.SWEEPING_EDGE)) {
                final int level = meta.getEnchantLevel(Enchantment.SWEEPING_EDGE);
                return level > Config.maxSweepingEdge || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.FROST_WALKER)) {
                final int level = meta.getEnchantLevel(Enchantment.FROST_WALKER);
                return level > Config.maxFrostWalker || level > Config.globalMaxEnchant;
            }

            // MELEE WEAPONS
            if (meta.hasEnchant(Enchantment.DAMAGE_ARTHROPODS)) {
                final int level = meta.getEnchantLevel(Enchantment.DAMAGE_ARTHROPODS);
                return level > Config.maxBaneOfArthropods || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.FIRE_ASPECT)) {
                final int level = meta.getEnchantLevel(Enchantment.FIRE_ASPECT);
                return level > Config.maxFireAspect || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LOOT_BONUS_MOBS)) {
                final int level = meta.getEnchantLevel(Enchantment.LOOT_BONUS_MOBS);
                return level > Config.maxLooting || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.IMPALING)) {
                final int level = meta.getEnchantLevel(Enchantment.IMPALING);
                return level > Config.maxImpaling || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.KNOCKBACK)) {
                final int level = meta.getEnchantLevel(Enchantment.KNOCKBACK);
                return level > Config.maxKnockback || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.DAMAGE_ALL)) {
                final int level = meta.getEnchantLevel(Enchantment.DAMAGE_ALL);
                return level > Config.maxSharpness || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.DAMAGE_UNDEAD)) {
                final int level = meta.getEnchantLevel(Enchantment.DAMAGE_UNDEAD);
                return level > Config.maxSmite || level > Config.globalMaxEnchant;
            }

            // RANGED WEAPONS
            if (meta.hasEnchant(Enchantment.CHANNELING)) {
                final int level = meta.getEnchantLevel(Enchantment.CHANNELING);
                return level > Config.maxChanneling || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.ARROW_FIRE)) {
                final int level = meta.getEnchantLevel(Enchantment.ARROW_FIRE);
                return level > Config.maxFlame || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.ARROW_INFINITE)) {
                final int level = meta.getEnchantLevel(Enchantment.ARROW_INFINITE);
                return level > Config.maxInfinity || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LOYALTY)) {
                final int level = meta.getEnchantLevel(Enchantment.LOYALTY);
                return level > Config.maxLoyalty || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.RIPTIDE)) {
                final int level = meta.getEnchantLevel(Enchantment.RIPTIDE);
                return level > Config.maxRiptide || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.MULTISHOT)) {
                final int level = meta.getEnchantLevel(Enchantment.MULTISHOT);
                return level > Config.maxMultishot || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PIERCING)) {
                final int level = meta.getEnchantLevel(Enchantment.PIERCING);
                return level > Config.maxPiercing || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.ARROW_DAMAGE)) {
                final int level = meta.getEnchantLevel(Enchantment.ARROW_DAMAGE);
                return level > Config.maxPower || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.ARROW_KNOCKBACK)) {
                final int level = meta.getEnchantLevel(Enchantment.ARROW_KNOCKBACK);
                return level > Config.maxPunch || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.QUICK_CHARGE)) {
                final int level = meta.getEnchantLevel(Enchantment.QUICK_CHARGE);
                return level > Config.maxQuickCharge || level > Config.globalMaxEnchant;
            }

            // TOOLS
            if (meta.hasEnchant(Enchantment.DIG_SPEED)) {
                final int level = meta.getEnchantLevel(Enchantment.DIG_SPEED);
                return level > Config.maxEfficiency || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
                final int level = meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
                return level > Config.maxFortune || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LUCK)) {
                final int level = meta.getEnchantLevel(Enchantment.LUCK);
                return level > Config.maxLuckOfTheSea || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LURE)) {
                final int level = meta.getEnchantLevel(Enchantment.LURE);
                return level > Config.maxLure || level > Config.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.SILK_TOUCH)) {
                final int level = meta.getEnchantLevel(Enchantment.SILK_TOUCH);
                return level > Config.maxSilkTouch || level > Config.globalMaxEnchant;
            }
        }
        return false;
    }
}

