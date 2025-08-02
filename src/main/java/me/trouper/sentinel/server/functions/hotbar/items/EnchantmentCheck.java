package me.trouper.sentinel.server.functions.hotbar.items;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.hotbar.AbstractCheck;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

import static org.bukkit.enchantments.Enchantment.MENDING;

public class EnchantmentCheck extends AbstractCheck<ItemStack> {

    @Override
    public boolean passes(ItemStack input) {
        return !hasIllegalEnchants(input);
    }

    public boolean hasIllegalEnchants(ItemStack item) {
        ServerUtils.verbose("Checking item for illegal enchants: ", item.getType().name());
        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            ItemMeta meta = item.getItemMeta();
            Map<Enchantment, Integer> enchantments = meta.getEnchants();
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();
                if (level > main.dir().io.nbtConfig.globalMaxEnchant || isOverLimit(enchantment, level)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOverLimit(Enchantment enchantment, int level) {
        int maxLevel = main.dir().io.nbtConfig.globalMaxEnchant;

        if (enchantment.equals(MENDING)) {
            maxLevel = main.dir().io.nbtConfig.maxMending;
        } else if (enchantment.equals(Enchantment.UNBREAKING)) {
            maxLevel = main.dir().io.nbtConfig.maxUnbreaking;
        } else if (enchantment.equals(Enchantment.VANISHING_CURSE)) {
            maxLevel = main.dir().io.nbtConfig.maxCurseOfVanishing;
        } else if (enchantment.equals(Enchantment.BINDING_CURSE)) {
            maxLevel = main.dir().io.nbtConfig.maxCurseOfBinding;
        } else if (enchantment.equals(Enchantment.AQUA_AFFINITY)) {
            maxLevel = main.dir().io.nbtConfig.maxAquaAffinity;
        } else if (enchantment.equals(Enchantment.PROTECTION)) {
            maxLevel = main.dir().io.nbtConfig.maxProtection;
        } else if (enchantment.equals(Enchantment.BLAST_PROTECTION)) {
            maxLevel = main.dir().io.nbtConfig.maxBlastProtection;
        } else if (enchantment.equals(Enchantment.DEPTH_STRIDER)) {
            maxLevel = main.dir().io.nbtConfig.maxDepthStrider;
        } else if (enchantment.equals(Enchantment.FEATHER_FALLING)) {
            maxLevel = main.dir().io.nbtConfig.maxFeatherFalling;
        } else if (enchantment.equals(Enchantment.FIRE_PROTECTION)) {
            maxLevel = main.dir().io.nbtConfig.maxFireProtection;
        } else if (enchantment.equals(Enchantment.FROST_WALKER)) {
            maxLevel = main.dir().io.nbtConfig.maxFrostWalker;
        } else if (enchantment.equals(Enchantment.PROJECTILE_PROTECTION)) {
            maxLevel = main.dir().io.nbtConfig.maxProjectileProtection;
        } else if (enchantment.equals(Enchantment.RESPIRATION)) {
            maxLevel = main.dir().io.nbtConfig.maxRespiration;
        } else if (enchantment.equals(Enchantment.SOUL_SPEED)) {
            maxLevel = main.dir().io.nbtConfig.maxSoulSpeed;
        } else if (enchantment.equals(Enchantment.THORNS)) {
            maxLevel = main.dir().io.nbtConfig.maxThorns;
        } else if (enchantment.equals(Enchantment.SWEEPING_EDGE)) {
            maxLevel = main.dir().io.nbtConfig.maxSweepingEdge;
        } else if (enchantment.equals(Enchantment.SWIFT_SNEAK)) {
            maxLevel = main.dir().io.nbtConfig.maxSwiftSneak;
        } else if (enchantment.equals(Enchantment.BANE_OF_ARTHROPODS)) {
            maxLevel = main.dir().io.nbtConfig.maxBaneOfArthropods;
        } else if (enchantment.equals(Enchantment.FIRE_ASPECT)) {
            maxLevel = main.dir().io.nbtConfig.maxFireAspect;
        } else if (enchantment.equals(Enchantment.LOOTING)) {
            maxLevel = main.dir().io.nbtConfig.maxLooting;
        } else if (enchantment.equals(Enchantment.IMPALING)) {
            maxLevel = main.dir().io.nbtConfig.maxImpaling;
        } else if (enchantment.equals(Enchantment.KNOCKBACK)) {
            maxLevel = main.dir().io.nbtConfig.maxKnockback;
        } else if (enchantment.equals(Enchantment.SHARPNESS)) {
            maxLevel = main.dir().io.nbtConfig.maxSharpness;
        } else if (enchantment.equals(Enchantment.SMITE)) {
            maxLevel = main.dir().io.nbtConfig.maxSmite;
        } else if (enchantment.equals(Enchantment.CHANNELING)) {
            maxLevel = main.dir().io.nbtConfig.maxChanneling;
        } else if (enchantment.equals(Enchantment.FLAME)) {
            maxLevel = main.dir().io.nbtConfig.maxFlame;
        } else if (enchantment.equals(Enchantment.INFINITY)) {
            maxLevel = main.dir().io.nbtConfig.maxInfinity;
        } else if (enchantment.equals(Enchantment.LOYALTY)) {
            maxLevel = main.dir().io.nbtConfig.maxLoyalty;
        } else if (enchantment.equals(Enchantment.RIPTIDE)) {
            maxLevel = main.dir().io.nbtConfig.maxRiptide;
        } else if (enchantment.equals(Enchantment.MULTISHOT)) {
            maxLevel = main.dir().io.nbtConfig.maxMultishot;
        } else if (enchantment.equals(Enchantment.PIERCING)) {
            maxLevel = main.dir().io.nbtConfig.maxPiercing;
        } else if (enchantment.equals(Enchantment.POWER)) {
            maxLevel = main.dir().io.nbtConfig.maxPower;
        } else if (enchantment.equals(Enchantment.PUNCH)) {
            maxLevel = main.dir().io.nbtConfig.maxPunch;
        } else if (enchantment.equals(Enchantment.QUICK_CHARGE)) {
            maxLevel = main.dir().io.nbtConfig.maxQuickCharge;
        } else if (enchantment.equals(Enchantment.EFFICIENCY)) {
            maxLevel = main.dir().io.nbtConfig.maxEfficiency;
        } else if (enchantment.equals(Enchantment.FORTUNE)) {
            maxLevel = main.dir().io.nbtConfig.maxFortune;
        } else if (enchantment.equals(Enchantment.LUCK_OF_THE_SEA)) {
            maxLevel = main.dir().io.nbtConfig.maxLuckOfTheSea;
        } else if (enchantment.equals(Enchantment.LURE)) {
            maxLevel = main.dir().io.nbtConfig.maxLure;
        } else if (enchantment.equals(Enchantment.SILK_TOUCH)) {
            maxLevel = main.dir().io.nbtConfig.maxSilkTouch;
        } else if (enchantment.equals(Enchantment.BREACH)) {
            maxLevel = main.dir().io.nbtConfig.maxBreach;
        } else if (enchantment.equals(Enchantment.DENSITY)) {
            maxLevel = main.dir().io.nbtConfig.maxDensity;
        } else if (enchantment.equals(Enchantment.WIND_BURST)) {
            maxLevel = main.dir().io.nbtConfig.maxWindBurst;
        }

        return level > maxLevel;
    }

    
}
