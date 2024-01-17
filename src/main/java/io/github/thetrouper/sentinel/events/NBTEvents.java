package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.ActionType;
import io.github.thetrouper.sentinel.server.Action;
import io.github.thetrouper.sentinel.server.config.MainConfig;
import io.github.thetrouper.sentinel.server.config.NBTConfig;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class NBTEvents implements CustomListener {
    @EventHandler
    private void onNBTPull(InventoryCreativeEvent e) {
        ServerUtils.sendDebugMessage("NBT: Detected creative mode action");
        if (!MainConfig.Plugin.preventNBT) return;
        ServerUtils.sendDebugMessage("NBT: Enabled");
        if (!(e.getWhoClicked() instanceof Player p)) return;
        ServerUtils.sendDebugMessage("NBT: Clicker is a player");
        if (e.getCursor() == null) return;
        ServerUtils.sendDebugMessage("NBT: Cursor isn't null");
        ItemStack i = e.getCursor();
        if (Sentinel.isTrusted(p)) return;
        ServerUtils.sendDebugMessage("NBT: Not trusted");
        if (e.getCursor().getItemMeta() == null) return;
        ServerUtils.sendDebugMessage("NBT: Cursor has meta");
        if (i.hasItemMeta() && i.getItemMeta() != null) {
            ServerUtils.sendDebugMessage("NBT: Item has meta");
            if (!itemPasses(i)) {
                ServerUtils.sendDebugMessage("NBT: Item doesn't pass, preforming action");
                Action a = new Action.Builder()
                        .setEvent(e)
                        .setAction(ActionType.NBT)
                        .setPlayer(Bukkit.getPlayer(e.getWhoClicked().getName()))
                        .setItem(e.getCursor())
                        .setDenied(MainConfig.Plugin.preventNBT)
                        .setDeoped(MainConfig.Plugin.deop)
                        .setPunished(MainConfig.Plugin.nbtPunish)
                        .setRevertGM(MainConfig.Plugin.preventNBT)
                        .setNotifyConsole(true)
                        .setNotifyTrusted(true)
                        .setnotifyDiscord(MainConfig.Plugin.logNBT)
                        .execute();
            }
        }
    }

    private boolean isContainer(ItemStack itemStack) {
        return itemStack.getType() == Material.CHEST ||
                itemStack.getType() == Material.TRAPPED_CHEST ||
                itemStack.getType() == Material.FURNACE ||
                itemStack.getType() == Material.BLAST_FURNACE ||
                itemStack.getType() == Material.DROPPER ||
                itemStack.getType() == Material.DISPENSER ||
                itemStack.getType() == Material.HOPPER ||
                itemStack.getType() == Material.BARREL;
    }

    private Inventory getSubInventory(ItemStack containerItem) {
        ServerUtils.sendDebugMessage("NBT: GetSubInv checking item: " + containerItem);
        if (containerItem.getItemMeta() instanceof BlockStateMeta blockStateMeta) {
            ServerUtils.sendDebugMessage("NBT: subInv has (is) blockStateMeta: " + blockStateMeta);
            BlockState blockState = blockStateMeta.getBlockState();
            if (blockState instanceof Container) {
                ServerUtils.sendDebugMessage("NBT: subInv has (is) container: " + (Container) blockState);
                return ((Container) blockState).getInventory();
            }
        }
        ServerUtils.sendDebugMessage("NBT: Inv is null: " + containerItem);
        return null;
    }

    private boolean containerPasses(Inventory inventory) {
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null || itemStack.getType().isAir()) continue;
            if (!itemPasses(itemStack)) {
                ServerUtils.sendDebugMessage("NBT: No pass C(I)");
                return false;
            }
            if (!isContainer(itemStack)) continue;

            Inventory subInventory = getSubInventory(itemStack);
            if (!containerPasses(subInventory)) {
                ServerUtils.sendDebugMessage("NBT: No pass C(R)");
                return false;
            }
        }
        ServerUtils.sendDebugMessage("NBT: Item passes recursion check.");
        return true;
    }


    private boolean itemPasses(ItemStack i) {
        ServerUtils.sendDebugMessage("NBT: Checking if item passes: " + i.getItemMeta());
        if (i.getItemMeta() == null) {
            ServerUtils.sendDebugMessage("NBT: Item passes because of no meta");
            return true;
        }
        ServerUtils.sendDebugMessage("NBT: Item meta isn't null");
        ItemMeta meta = i.getItemMeta();
        Inventory inv = getSubInventory(i);
        if (inv != null) {
            ServerUtils.sendDebugMessage("NBT: Item has a SubInv: " + inv);
            if (!containerPasses(inv)) {
                ServerUtils.sendDebugMessage("NBT: No pass C");
                return false;
            }
        }
        if (!NBTConfig.allowName && meta.hasDisplayName()) {
            ServerUtils.sendDebugMessage("NBT: No pass N");
            return false;
        }
        if (!NBTConfig.allowLore && meta.hasLore()) {
            ServerUtils.sendDebugMessage("NBT: No Pass L ");
            return false;
        }
        if (!NBTConfig.allowAttributes && meta.hasAttributeModifiers()) {
            ServerUtils.sendDebugMessage("NBT: No pass A");
            return false;
        }
        if (NBTConfig.globalMaxEnchant != 0 && hasIllegalEnchants(i)) {
            ServerUtils.sendDebugMessage("NBT: No pass E");
            return false;
        }
        ServerUtils.sendDebugMessage("NBT: All checks passed");
        return true;
    }
    /*
    [01:23:03 INFO]: [Sentinel] [DEBUG]: NBT: Detected creative mode action
    [01:23:03 INFO]: [Sentinel] [DEBUG]: NBT: Enabled
    [01:23:03 INFO]: [Sentinel] [DEBUG]: NBT: Clicker is a player
    [01:23:03 INFO]: [Sentinel] [DEBUG]: NBT: Cursor isn't null
    [01:23:03 INFO]: [Sentinel] [DEBUG]: NBT: Not trusted
    [01:23:03 INFO]: [Sentinel] [DEBUG]: NBT: Cursor has meta
    [01:23:03 INFO]: [Sentinel] [DEBUG]: NBT: Item has meta
    [01:23:03 INFO]: [Sentinel] [DEBUG]: NBT: Checking if item passes: UNSPECIFIC_META:{meta-type=UNSPECIFIC, display-name={"italic":false,"color":"red","text":"Penguin's Flaming Fish!"}, lore=[{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"Penguin Almighty XXXMMDCCLXVII"}],"text":""}], enchants={FIRE_ASPECT=32767, KNOCKBACK=32767}, ItemFlags=[HIDE_ENCHANTS]}
    [01:23:03 INFO]: [Sentinel] [DEBUG]: NBT: Item meta isn't null
    [01:23:03 INFO]: [Sentinel] [DEBUG]: NBT: All checks passed
     */
    private boolean hasIllegalEnchants(ItemStack i) {
        ServerUtils.sendDebugMessage("NBT: Checking for illegal enchants");
        if (i.hasItemMeta() && i.getItemMeta().hasEnchants()) {
            final ItemMeta meta = i.getItemMeta();
            final Map<Enchantment, Integer> enchantments = meta.getEnchants();
            for (Integer value : enchantments.values()) {
                if (value > NBTConfig.globalMaxEnchant) {
                    return true;
                }
            }
            // ALL
            if (meta.hasEnchant(Enchantment.MENDING)) {
                final int level = meta.getEnchantLevel(Enchantment.MENDING);
                return level > NBTConfig.maxMending || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.DURABILITY)) {
                final int level = meta.getEnchantLevel(Enchantment.DURABILITY);
                return level > NBTConfig.maxUnbreaking || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.VANISHING_CURSE)) {
                final int level = meta.getEnchantLevel(Enchantment.VANISHING_CURSE);
                return level > NBTConfig.maxVanishing || level > NBTConfig.globalMaxEnchant;
            }

            // ARMOR
            if (meta.hasEnchant(Enchantment.BINDING_CURSE)) {
                final int level = meta.getEnchantLevel(Enchantment.BINDING_CURSE);
                return level > NBTConfig.maxCurseOfBinding || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.WATER_WORKER)) {
                final int level = meta.getEnchantLevel(Enchantment.WATER_WORKER);
                return level > NBTConfig.maxAquaAffinity || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
                return level > NBTConfig.maxProtection || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_EXPLOSIONS)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_EXPLOSIONS);
                return level > NBTConfig.maxBlastProtection || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.DEPTH_STRIDER)) {
                final int level = meta.getEnchantLevel(Enchantment.DEPTH_STRIDER);
                return level > NBTConfig.maxDepthStrider || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_FALL)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_FALL);
                return level > NBTConfig.maxFeatherFalling || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_FIRE)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_FIRE);
                return level > NBTConfig.maxFireProtection || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.FROST_WALKER)) {
                final int level = meta.getEnchantLevel(Enchantment.FROST_WALKER);
                return level > NBTConfig.maxFrostWalker || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_PROJECTILE)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_PROJECTILE);
                return level > NBTConfig.maxProjectileProtection || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.OXYGEN)) {
                final int level = meta.getEnchantLevel(Enchantment.OXYGEN);
                return level > NBTConfig.maxRespiration || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.SOUL_SPEED)) {
                final int level = meta.getEnchantLevel(Enchantment.SOUL_SPEED);
                return level > NBTConfig.maxSoulSpeed || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.THORNS)) {
                final int level = meta.getEnchantLevel(Enchantment.THORNS);
                return level > NBTConfig.maxThorns || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.SWEEPING_EDGE)) {
                final int level = meta.getEnchantLevel(Enchantment.SWEEPING_EDGE);
                return level > NBTConfig.maxSweepingEdge || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.FROST_WALKER)) {
                final int level = meta.getEnchantLevel(Enchantment.FROST_WALKER);
                return level > NBTConfig.maxFrostWalker || level > NBTConfig.globalMaxEnchant;
            }

            // MELEE WEAPONS
            if (meta.hasEnchant(Enchantment.DAMAGE_ARTHROPODS)) {
                final int level = meta.getEnchantLevel(Enchantment.DAMAGE_ARTHROPODS);
                return level > NBTConfig.maxBaneOfArthropods || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.FIRE_ASPECT)) {
                final int level = meta.getEnchantLevel(Enchantment.FIRE_ASPECT);
                return level > NBTConfig.maxFireAspect || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LOOT_BONUS_MOBS)) {
                final int level = meta.getEnchantLevel(Enchantment.LOOT_BONUS_MOBS);
                return level > NBTConfig.maxLooting || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.IMPALING)) {
                final int level = meta.getEnchantLevel(Enchantment.IMPALING);
                return level > NBTConfig.maxImpaling || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.KNOCKBACK)) {
                final int level = meta.getEnchantLevel(Enchantment.KNOCKBACK);
                return level > NBTConfig.maxKnockback || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.DAMAGE_ALL)) {
                final int level = meta.getEnchantLevel(Enchantment.DAMAGE_ALL);
                return level > NBTConfig.maxSharpness || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.DAMAGE_UNDEAD)) {
                final int level = meta.getEnchantLevel(Enchantment.DAMAGE_UNDEAD);
                return level > NBTConfig.maxSmite || level > NBTConfig.globalMaxEnchant;
            }

            // RANGED WEAPONS
            if (meta.hasEnchant(Enchantment.CHANNELING)) {
                final int level = meta.getEnchantLevel(Enchantment.CHANNELING);
                return level > NBTConfig.maxChanneling || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.ARROW_FIRE)) {
                final int level = meta.getEnchantLevel(Enchantment.ARROW_FIRE);
                return level > NBTConfig.maxFlame || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.ARROW_INFINITE)) {
                final int level = meta.getEnchantLevel(Enchantment.ARROW_INFINITE);
                return level > NBTConfig.maxInfinity || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LOYALTY)) {
                final int level = meta.getEnchantLevel(Enchantment.LOYALTY);
                return level > NBTConfig.maxLoyalty || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.RIPTIDE)) {
                final int level = meta.getEnchantLevel(Enchantment.RIPTIDE);
                return level > NBTConfig.maxRiptide || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.MULTISHOT)) {
                final int level = meta.getEnchantLevel(Enchantment.MULTISHOT);
                return level > NBTConfig.maxMultishot || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PIERCING)) {
                final int level = meta.getEnchantLevel(Enchantment.PIERCING);
                return level > NBTConfig.maxPiercing || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.ARROW_DAMAGE)) {
                final int level = meta.getEnchantLevel(Enchantment.ARROW_DAMAGE);
                return level > NBTConfig.maxPower || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.ARROW_KNOCKBACK)) {
                final int level = meta.getEnchantLevel(Enchantment.ARROW_KNOCKBACK);
                return level > NBTConfig.maxPunch || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.QUICK_CHARGE)) {
                final int level = meta.getEnchantLevel(Enchantment.QUICK_CHARGE);
                return level > NBTConfig.maxQuickCharge || level > NBTConfig.globalMaxEnchant;
            }

            // TOOLS
            if (meta.hasEnchant(Enchantment.DIG_SPEED)) {
                final int level = meta.getEnchantLevel(Enchantment.DIG_SPEED);
                return level > NBTConfig.maxEfficiency || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
                final int level = meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
                return level > NBTConfig.maxFortune || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LUCK)) {
                final int level = meta.getEnchantLevel(Enchantment.LUCK);
                return level > NBTConfig.maxLuckOfTheSea || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LURE)) {
                final int level = meta.getEnchantLevel(Enchantment.LURE);
                return level > NBTConfig.maxLure || level > NBTConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.SILK_TOUCH)) {
                final int level = meta.getEnchantLevel(Enchantment.SILK_TOUCH);
                return level > NBTConfig.maxSilkTouch || level > NBTConfig.globalMaxEnchant;
            }
        }
        return false;
    }
}

