package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.ActionType;
import io.github.thetrouper.sentinel.server.Action;
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
    public void onNBTPull(InventoryCreativeEvent e) {
        ServerUtils.sendDebugMessage("NBT: Detected creative mode action");
        if (!Sentinel.mainConfig.plugin.preventNBT) return;
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
        if (!(i.hasItemMeta() && i.getItemMeta() != null)) return;
        ServerUtils.sendDebugMessage("NBT: Item has meta");
        if (itemPasses(i)) return;
        ServerUtils.sendDebugMessage("NBT: Item doesn't pass, preforming action");
        Action a = new Action.Builder()
                .setEvent(e)
                .setAction(ActionType.NBT)
                .setPlayer(Bukkit.getPlayer(e.getWhoClicked().getName()))
                .setItem(e.getCursor())
                .setDenied(Sentinel.mainConfig.plugin.preventNBT)
                .setDeoped(Sentinel.mainConfig.plugin.deop)
                .setPunished(Sentinel.mainConfig.plugin.nbtPunish)
                .setRevertGM(Sentinel.mainConfig.plugin.preventNBT)
                .setNotifyConsole(true)
                .setNotifyTrusted(true)
                .setNotifyDiscord(Sentinel.mainConfig.plugin.logNBT)
                .execute();
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
        if (!Sentinel.nbtConfig.allowName && meta.hasDisplayName()) {
            ServerUtils.sendDebugMessage("NBT: No pass N");
            return false;
        }
        if (!Sentinel.nbtConfig.allowLore && meta.hasLore()) {
            ServerUtils.sendDebugMessage("NBT: No Pass L ");
            return false;
        }
        if (!Sentinel.nbtConfig.allowPotions && (i.getType().equals(Material.POTION) || i.getType().equals(Material.SPLASH_POTION) || i.getType().equals(Material.LINGERING_POTION))) {
            ServerUtils.sendDebugMessage("NBT: No pass P");
            return false;
        }
        if (!Sentinel.nbtConfig.allowAttributes && meta.hasAttributeModifiers()) {
            ServerUtils.sendDebugMessage("NBT: No pass A");
            return false;
        }
        if (Sentinel.nbtConfig.globalMaxEnchant != 0 && hasIllegalEnchants(i)) {
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
                if (value > Sentinel.nbtConfig.globalMaxEnchant) {
                    return true;
                }
            }
            // ALL
            if (meta.hasEnchant(Enchantment.MENDING)) {
                final int level = meta.getEnchantLevel(Enchantment.MENDING);
                return level > Sentinel.nbtConfig.maxMending || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.DURABILITY)) {
                final int level = meta.getEnchantLevel(Enchantment.DURABILITY);
                return level > Sentinel.nbtConfig.maxUnbreaking || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.VANISHING_CURSE)) {
                final int level = meta.getEnchantLevel(Enchantment.VANISHING_CURSE);
                return level > Sentinel.nbtConfig.maxVanishing || level > Sentinel.nbtConfig.globalMaxEnchant;
            }

            // ARMOR
            if (meta.hasEnchant(Enchantment.BINDING_CURSE)) {
                final int level = meta.getEnchantLevel(Enchantment.BINDING_CURSE);
                return level > Sentinel.nbtConfig.maxCurseOfBinding || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.WATER_WORKER)) {
                final int level = meta.getEnchantLevel(Enchantment.WATER_WORKER);
                return level > Sentinel.nbtConfig.maxAquaAffinity || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
                return level > Sentinel.nbtConfig.maxProtection || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_EXPLOSIONS)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_EXPLOSIONS);
                return level > Sentinel.nbtConfig.maxBlastProtection || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.DEPTH_STRIDER)) {
                final int level = meta.getEnchantLevel(Enchantment.DEPTH_STRIDER);
                return level > Sentinel.nbtConfig.maxDepthStrider || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_FALL)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_FALL);
                return level > Sentinel.nbtConfig.maxFeatherFalling || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_FIRE)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_FIRE);
                return level > Sentinel.nbtConfig.maxFireProtection || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.FROST_WALKER)) {
                final int level = meta.getEnchantLevel(Enchantment.FROST_WALKER);
                return level > Sentinel.nbtConfig.maxFrostWalker || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PROTECTION_PROJECTILE)) {
                final int level = meta.getEnchantLevel(Enchantment.PROTECTION_PROJECTILE);
                return level > Sentinel.nbtConfig.maxProjectileProtection || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.OXYGEN)) {
                final int level = meta.getEnchantLevel(Enchantment.OXYGEN);
                return level > Sentinel.nbtConfig.maxRespiration || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.SOUL_SPEED)) {
                final int level = meta.getEnchantLevel(Enchantment.SOUL_SPEED);
                return level > Sentinel.nbtConfig.maxSoulSpeed || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.THORNS)) {
                final int level = meta.getEnchantLevel(Enchantment.THORNS);
                return level > Sentinel.nbtConfig.maxThorns || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.SWEEPING_EDGE)) {
                final int level = meta.getEnchantLevel(Enchantment.SWEEPING_EDGE);
                return level > Sentinel.nbtConfig.maxSweepingEdge || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.FROST_WALKER)) {
                final int level = meta.getEnchantLevel(Enchantment.FROST_WALKER);
                return level > Sentinel.nbtConfig.maxFrostWalker || level > Sentinel.nbtConfig.globalMaxEnchant;
            }

            // MELEE WEAPONS
            if (meta.hasEnchant(Enchantment.DAMAGE_ARTHROPODS)) {
                final int level = meta.getEnchantLevel(Enchantment.DAMAGE_ARTHROPODS);
                return level > Sentinel.nbtConfig.maxBaneOfArthropods || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.FIRE_ASPECT)) {
                final int level = meta.getEnchantLevel(Enchantment.FIRE_ASPECT);
                return level > Sentinel.nbtConfig.maxFireAspect || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LOOT_BONUS_MOBS)) {
                final int level = meta.getEnchantLevel(Enchantment.LOOT_BONUS_MOBS);
                return level > Sentinel.nbtConfig.maxLooting || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.IMPALING)) {
                final int level = meta.getEnchantLevel(Enchantment.IMPALING);
                return level > Sentinel.nbtConfig.maxImpaling || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.KNOCKBACK)) {
                final int level = meta.getEnchantLevel(Enchantment.KNOCKBACK);
                return level > Sentinel.nbtConfig.maxKnockback || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.DAMAGE_ALL)) {
                final int level = meta.getEnchantLevel(Enchantment.DAMAGE_ALL);
                return level > Sentinel.nbtConfig.maxSharpness || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.DAMAGE_UNDEAD)) {
                final int level = meta.getEnchantLevel(Enchantment.DAMAGE_UNDEAD);
                return level > Sentinel.nbtConfig.maxSmite || level > Sentinel.nbtConfig.globalMaxEnchant;
            }

            // RANGED WEAPONS
            if (meta.hasEnchant(Enchantment.CHANNELING)) {
                final int level = meta.getEnchantLevel(Enchantment.CHANNELING);
                return level > Sentinel.nbtConfig.maxChanneling || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.ARROW_FIRE)) {
                final int level = meta.getEnchantLevel(Enchantment.ARROW_FIRE);
                return level > Sentinel.nbtConfig.maxFlame || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.ARROW_INFINITE)) {
                final int level = meta.getEnchantLevel(Enchantment.ARROW_INFINITE);
                return level > Sentinel.nbtConfig.maxInfinity || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LOYALTY)) {
                final int level = meta.getEnchantLevel(Enchantment.LOYALTY);
                return level > Sentinel.nbtConfig.maxLoyalty || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.RIPTIDE)) {
                final int level = meta.getEnchantLevel(Enchantment.RIPTIDE);
                return level > Sentinel.nbtConfig.maxRiptide || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.MULTISHOT)) {
                final int level = meta.getEnchantLevel(Enchantment.MULTISHOT);
                return level > Sentinel.nbtConfig.maxMultishot || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.PIERCING)) {
                final int level = meta.getEnchantLevel(Enchantment.PIERCING);
                return level > Sentinel.nbtConfig.maxPiercing || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.ARROW_DAMAGE)) {
                final int level = meta.getEnchantLevel(Enchantment.ARROW_DAMAGE);
                return level > Sentinel.nbtConfig.maxPower || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.ARROW_KNOCKBACK)) {
                final int level = meta.getEnchantLevel(Enchantment.ARROW_KNOCKBACK);
                return level > Sentinel.nbtConfig.maxPunch || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.QUICK_CHARGE)) {
                final int level = meta.getEnchantLevel(Enchantment.QUICK_CHARGE);
                return level > Sentinel.nbtConfig.maxQuickCharge || level > Sentinel.nbtConfig.globalMaxEnchant;
            }

            // TOOLS
            if (meta.hasEnchant(Enchantment.DIG_SPEED)) {
                final int level = meta.getEnchantLevel(Enchantment.DIG_SPEED);
                return level > Sentinel.nbtConfig.maxEfficiency || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
                final int level = meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
                return level > Sentinel.nbtConfig.maxFortune || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LUCK)) {
                final int level = meta.getEnchantLevel(Enchantment.LUCK);
                return level > Sentinel.nbtConfig.maxLuckOfTheSea || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.LURE)) {
                final int level = meta.getEnchantLevel(Enchantment.LURE);
                return level > Sentinel.nbtConfig.maxLure || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
            if (meta.hasEnchant(Enchantment.SILK_TOUCH)) {
                final int level = meta.getEnchantLevel(Enchantment.SILK_TOUCH);
                return level > Sentinel.nbtConfig.maxSilkTouch || level > Sentinel.nbtConfig.globalMaxEnchant;
            }
        }
        return false;
    }
}

