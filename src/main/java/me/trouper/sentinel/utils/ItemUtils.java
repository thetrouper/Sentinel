package me.trouper.sentinel.utils;

import me.trouper.sentinel.Sentinel;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

import static org.bukkit.enchantments.Enchantment.MENDING;

public class ItemUtils {

    public static boolean isContainer(ItemStack itemStack) {
        return itemStack.getType() == Material.CHEST ||
                itemStack.getType() == Material.TRAPPED_CHEST ||
                itemStack.getType() == Material.FURNACE ||
                itemStack.getType() == Material.BLAST_FURNACE ||
                itemStack.getType() == Material.DROPPER ||
                itemStack.getType() == Material.DISPENSER ||
                itemStack.getType() == Material.HOPPER ||
                itemStack.getType() == Material.BARREL;
    }

    public static Inventory getSubInventory(ItemStack containerItem) {
        ServerUtils.verbose("NBT: GetSubInv checking item: " + containerItem);
        if (containerItem.getItemMeta() instanceof BlockStateMeta blockStateMeta) {
            ServerUtils.verbose("NBT: subInv has (is) blockStateMeta: " + blockStateMeta);
            BlockState blockState = blockStateMeta.getBlockState();
            if (blockState instanceof Container) {
                ServerUtils.verbose("NBT: subInv has (is) container: " + (Container) blockState);
                return ((Container) blockState).getInventory();
            }
        }
        ServerUtils.verbose("NBT: Inv is null: " + containerItem);
        return null;
    }

    public static boolean containerPasses(Inventory inventory) {
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null || itemStack.getType().isAir()) continue;
            if (!itemPasses(itemStack)) {
                ServerUtils.verbose("NBT: No pass C(I)");
                return false;
            }
            if (!isContainer(itemStack)) continue;

            Inventory subInventory = getSubInventory(itemStack);
            if (!containerPasses(subInventory)) {
                ServerUtils.verbose("NBT: No pass C(R)");
                return false;
            }
        }
        ServerUtils.verbose("NBT: Item passes recursion check.");
        return true;
    }


    public static boolean itemPasses(ItemStack i) {
        ServerUtils.verbose("NBT: Checking if item passes: " + i.getItemMeta());
        if (i.getItemMeta() == null) {
            ServerUtils.verbose("NBT: Item passes because of no meta");
            return true;
        }
        ServerUtils.verbose("NBT: Item meta isn't null");
        ItemMeta meta = i.getItemMeta();
        Inventory inv = getSubInventory(i);
        if (inv != null) {
            ServerUtils.verbose("NBT: Item has a SubInv: " + inv);
            if (!containerPasses(inv)) {
                ServerUtils.verbose("NBT: No pass C");
                return false;
            }
        }
        if (!Sentinel.nbtConfig.allowName && meta.hasDisplayName()) {
            ServerUtils.verbose("NBT: No pass N");
            return false;
        }
        if (!Sentinel.nbtConfig.allowLore && meta.hasLore()) {
            ServerUtils.verbose("NBT: No Pass L ");
            return false;
        }
        if (!Sentinel.nbtConfig.allowPotions && (i.getType().equals(Material.POTION) || i.getType().equals(Material.SPLASH_POTION) || i.getType().equals(Material.LINGERING_POTION))) {
            ServerUtils.verbose("NBT: No pass P");
            return false;
        }
        if (!Sentinel.nbtConfig.allowAttributes && meta.hasAttributeModifiers()) {
            ServerUtils.verbose("NBT: No pass A");
            return false;
        }
        if (Sentinel.nbtConfig.globalMaxEnchant != 0 && hasIllegalEnchants(i)) {
            ServerUtils.verbose("NBT: No pass E");
            return false;
        }
        ServerUtils.verbose("NBT: All checks passed");
        return true;
    }

    public static boolean hasIllegalEnchants(ItemStack i) {
        ServerUtils.verbose("NBT: Checking for illegal enchants");

        if (i.hasItemMeta() && i.getItemMeta().hasEnchants()) {
            final ItemMeta meta = i.getItemMeta();
            final Map<Enchantment, Integer> enchantments = meta.getEnchants();

            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();

                if (level > Sentinel.nbtConfig.globalMaxEnchant || isOverLimit(enchantment, level)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOverLimit(Enchantment enchantment, int level) {
        int maxLevel = Sentinel.nbtConfig.globalMaxEnchant;

        if (enchantment.equals(MENDING)) {
            maxLevel = Sentinel.nbtConfig.maxMending;
        } else if (enchantment.equals(Enchantment.UNBREAKING)) {
            maxLevel = Sentinel.nbtConfig.maxUnbreaking;
        } else if (enchantment.equals(Enchantment.VANISHING_CURSE)) {
            maxLevel = Sentinel.nbtConfig.maxCurseOfVanishing;
        } else if (enchantment.equals(Enchantment.BINDING_CURSE)) {
            maxLevel = Sentinel.nbtConfig.maxCurseOfBinding;
        } else if (enchantment.equals(Enchantment.AQUA_AFFINITY)) {
            maxLevel = Sentinel.nbtConfig.maxAquaAffinity;
        } else if (enchantment.equals(Enchantment.PROTECTION)) {
            maxLevel = Sentinel.nbtConfig.maxProtection;
        } else if (enchantment.equals(Enchantment.BLAST_PROTECTION)) {
            maxLevel = Sentinel.nbtConfig.maxBlastProtection;
        } else if (enchantment.equals(Enchantment.DEPTH_STRIDER)) {
            maxLevel = Sentinel.nbtConfig.maxDepthStrider;
        } else if (enchantment.equals(Enchantment.FEATHER_FALLING)) {
            maxLevel = Sentinel.nbtConfig.maxFeatherFalling;
        } else if (enchantment.equals(Enchantment.FIRE_PROTECTION)) {
            maxLevel = Sentinel.nbtConfig.maxFireProtection;
        } else if (enchantment.equals(Enchantment.FROST_WALKER)) {
            maxLevel = Sentinel.nbtConfig.maxFrostWalker;
        } else if (enchantment.equals(Enchantment.PROJECTILE_PROTECTION)) {
            maxLevel = Sentinel.nbtConfig.maxProjectileProtection;
        } else if (enchantment.equals(Enchantment.RESPIRATION)) {
            maxLevel = Sentinel.nbtConfig.maxRespiration;
        } else if (enchantment.equals(Enchantment.SOUL_SPEED)) {
            maxLevel = Sentinel.nbtConfig.maxSoulSpeed;
        } else if (enchantment.equals(Enchantment.THORNS)) {
            maxLevel = Sentinel.nbtConfig.maxThorns;
        } else if (enchantment.equals(Enchantment.SWEEPING_EDGE)) {
            maxLevel = Sentinel.nbtConfig.maxSweepingEdge;
        } else if (enchantment.equals(Enchantment.SWIFT_SNEAK)) {
            maxLevel = Sentinel.nbtConfig.maxSwiftSneak;
        } else if (enchantment.equals(Enchantment.BANE_OF_ARTHROPODS)) {
            maxLevel = Sentinel.nbtConfig.maxBaneOfArthropods;
        } else if (enchantment.equals(Enchantment.FIRE_ASPECT)) {
            maxLevel = Sentinel.nbtConfig.maxFireAspect;
        } else if (enchantment.equals(Enchantment.LOOTING)) {
            maxLevel = Sentinel.nbtConfig.maxLooting;
        } else if (enchantment.equals(Enchantment.IMPALING)) {
            maxLevel = Sentinel.nbtConfig.maxImpaling;
        } else if (enchantment.equals(Enchantment.KNOCKBACK)) {
            maxLevel = Sentinel.nbtConfig.maxKnockback;
        } else if (enchantment.equals(Enchantment.SHARPNESS)) {
            maxLevel = Sentinel.nbtConfig.maxSharpness;
        } else if (enchantment.equals(Enchantment.SMITE)) {
            maxLevel = Sentinel.nbtConfig.maxSmite;
        } else if (enchantment.equals(Enchantment.CHANNELING)) {
            maxLevel = Sentinel.nbtConfig.maxChanneling;
        } else if (enchantment.equals(Enchantment.FLAME)) {
            maxLevel = Sentinel.nbtConfig.maxFlame;
        } else if (enchantment.equals(Enchantment.INFINITY)) {
            maxLevel = Sentinel.nbtConfig.maxInfinity;
        } else if (enchantment.equals(Enchantment.LOYALTY)) {
            maxLevel = Sentinel.nbtConfig.maxLoyalty;
        } else if (enchantment.equals(Enchantment.RIPTIDE)) {
            maxLevel = Sentinel.nbtConfig.maxRiptide;
        } else if (enchantment.equals(Enchantment.MULTISHOT)) {
            maxLevel = Sentinel.nbtConfig.maxMultishot;
        } else if (enchantment.equals(Enchantment.PIERCING)) {
            maxLevel = Sentinel.nbtConfig.maxPiercing;
        } else if (enchantment.equals(Enchantment.POWER)) {
            maxLevel = Sentinel.nbtConfig.maxPower;
        } else if (enchantment.equals(Enchantment.PUNCH)) {
            maxLevel = Sentinel.nbtConfig.maxPunch;
        } else if (enchantment.equals(Enchantment.QUICK_CHARGE)) {
            maxLevel = Sentinel.nbtConfig.maxQuickCharge;
        } else if (enchantment.equals(Enchantment.EFFICIENCY)) {
            maxLevel = Sentinel.nbtConfig.maxEfficiency;
        } else if (enchantment.equals(Enchantment.FORTUNE)) {
            maxLevel = Sentinel.nbtConfig.maxFortune;
        } else if (enchantment.equals(Enchantment.LUCK_OF_THE_SEA)) {
            maxLevel = Sentinel.nbtConfig.maxLuckOfTheSea;
        } else if (enchantment.equals(Enchantment.LURE)) {
            maxLevel = Sentinel.nbtConfig.maxLure;
        } else if (enchantment.equals(Enchantment.SILK_TOUCH)) {
            maxLevel = Sentinel.nbtConfig.maxSilkTouch;
        } else if (enchantment.equals(Enchantment.BREACH)) {
            maxLevel = Sentinel.nbtConfig.maxBreach;
        } else if (enchantment.equals(Enchantment.DENSITY)) {
            maxLevel = Sentinel.nbtConfig.maxDensity;
        } else if (enchantment.equals(Enchantment.WIND_BURST)) {
            maxLevel = Sentinel.nbtConfig.maxWindBurst;
        }

        return level > maxLevel;
    }
}
