package me.trouper.sentinel.server.functions.itemchecks;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

import static org.bukkit.enchantments.Enchantment.MENDING;

public class EnchantmentCheck {

    public boolean hasIllegalEnchants(ItemStack item) {
        ServerUtils.verbose("Checking item for illegal enchants: ", item.getType().name());
        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            ItemMeta meta = item.getItemMeta();
            Map<Enchantment, Integer> enchantments = meta.getEnchants();
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();
                if (level > Sentinel.getInstance().getDirector().io.nbtConfig.globalMaxEnchant || isOverLimit(enchantment, level)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOverLimit(Enchantment enchantment, int level) {
        int maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.globalMaxEnchant;

        if (enchantment.equals(MENDING)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxMending;
        } else if (enchantment.equals(Enchantment.UNBREAKING)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxUnbreaking;
        } else if (enchantment.equals(Enchantment.VANISHING_CURSE)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxCurseOfVanishing;
        } else if (enchantment.equals(Enchantment.BINDING_CURSE)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxCurseOfBinding;
        } else if (enchantment.equals(Enchantment.AQUA_AFFINITY)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxAquaAffinity;
        } else if (enchantment.equals(Enchantment.PROTECTION)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxProtection;
        } else if (enchantment.equals(Enchantment.BLAST_PROTECTION)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxBlastProtection;
        } else if (enchantment.equals(Enchantment.DEPTH_STRIDER)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxDepthStrider;
        } else if (enchantment.equals(Enchantment.FEATHER_FALLING)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxFeatherFalling;
        } else if (enchantment.equals(Enchantment.FIRE_PROTECTION)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxFireProtection;
        } else if (enchantment.equals(Enchantment.FROST_WALKER)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxFrostWalker;
        } else if (enchantment.equals(Enchantment.PROJECTILE_PROTECTION)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxProjectileProtection;
        } else if (enchantment.equals(Enchantment.RESPIRATION)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxRespiration;
        } else if (enchantment.equals(Enchantment.SOUL_SPEED)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxSoulSpeed;
        } else if (enchantment.equals(Enchantment.THORNS)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxThorns;
        } else if (enchantment.equals(Enchantment.SWEEPING_EDGE)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxSweepingEdge;
        } else if (enchantment.equals(Enchantment.SWIFT_SNEAK)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxSwiftSneak;
        } else if (enchantment.equals(Enchantment.BANE_OF_ARTHROPODS)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxBaneOfArthropods;
        } else if (enchantment.equals(Enchantment.FIRE_ASPECT)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxFireAspect;
        } else if (enchantment.equals(Enchantment.LOOTING)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxLooting;
        } else if (enchantment.equals(Enchantment.IMPALING)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxImpaling;
        } else if (enchantment.equals(Enchantment.KNOCKBACK)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxKnockback;
        } else if (enchantment.equals(Enchantment.SHARPNESS)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxSharpness;
        } else if (enchantment.equals(Enchantment.SMITE)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxSmite;
        } else if (enchantment.equals(Enchantment.CHANNELING)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxChanneling;
        } else if (enchantment.equals(Enchantment.FLAME)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxFlame;
        } else if (enchantment.equals(Enchantment.INFINITY)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxInfinity;
        } else if (enchantment.equals(Enchantment.LOYALTY)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxLoyalty;
        } else if (enchantment.equals(Enchantment.RIPTIDE)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxRiptide;
        } else if (enchantment.equals(Enchantment.MULTISHOT)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxMultishot;
        } else if (enchantment.equals(Enchantment.PIERCING)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxPiercing;
        } else if (enchantment.equals(Enchantment.POWER)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxPower;
        } else if (enchantment.equals(Enchantment.PUNCH)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxPunch;
        } else if (enchantment.equals(Enchantment.QUICK_CHARGE)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxQuickCharge;
        } else if (enchantment.equals(Enchantment.EFFICIENCY)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxEfficiency;
        } else if (enchantment.equals(Enchantment.FORTUNE)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxFortune;
        } else if (enchantment.equals(Enchantment.LUCK_OF_THE_SEA)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxLuckOfTheSea;
        } else if (enchantment.equals(Enchantment.LURE)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxLure;
        } else if (enchantment.equals(Enchantment.SILK_TOUCH)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxSilkTouch;
        } else if (enchantment.equals(Enchantment.BREACH)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxBreach;
        } else if (enchantment.equals(Enchantment.DENSITY)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxDensity;
        } else if (enchantment.equals(Enchantment.WIND_BURST)) {
            maxLevel = Sentinel.getInstance().getDirector().io.nbtConfig.maxWindBurst;
        }

        return level > maxLevel;
    }
}
