package me.trouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;

import java.io.File;

public class NBTConfig implements JsonSerializable<NBTConfig> {
    @Override
    public File getFile() {
        File file = new File(Sentinel.dataFolder(), "/nbt-config.json");
        file.getParentFile().mkdirs();
        return file;
    }

    public boolean allowName = true;
    public boolean allowLore = true;
    public boolean allowAttributes = false;
    public boolean allowPotions = false;
    public int globalMaxEnchant = 5;
    public int maxMending = 1;
    public int maxUnbreaking = 3;
    public int maxVanishing = 1;
    public int maxAquaAffinity = 1;
    public int maxBlastProtection = 4;
    public int maxCurseOfBinding = 1;
    public int maxDepthStrider = 3;
    public int maxFeatherFalling = 4;
    public int maxFireProtection = 4;
    public int maxFrostWalker = 2;
    public int maxProjectileProtection = 4;
    public int maxProtection = 4;
    public int maxRespiration = 3;
    public int maxSoulSpeed = 3;
    public int maxThorns = 3;
    public int maxSwiftSneak = 3;
    public int maxBaneOfArthropods = 5;
    public int maxEfficiency = 5;
    public int maxFireAspect = 2;
    public int maxLooting = 3;
    public int maxImpaling = 5;
    public int maxKnockback = 2;
    public int maxSharpness = 5;
    public int maxSmite = 5;
    public int maxSweepingEdge = 3;
    public int maxChanneling = 1;
    public int maxFlame = 1;
    public int maxInfinity = 1;
    public int maxLoyalty = 3;
    public int maxRiptide = 3;
    public int maxMultishot = 1;
    public int maxPiercing = 4;
    public int maxPower = 5;
    public int maxPunch = 2;
    public int maxQuickCharge = 3;
    public int maxFortune = 3;
    public int maxLuckOfTheSea = 3;
    public int maxLure = 3;
    public int maxSilkTouch = 1;
}
