package io.github.thetrouper.sentinel.server.config;

import io.github.itzispyder.pdk.utils.FileValidationUtils;
import io.github.itzispyder.pdk.utils.misc.JsonSerializable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class NBTConfig implements JsonSerializable<NBTConfig> {
    @Override
    public File getFile() {
        return new File("plugins/Sentinel/NBTConfig.json");
    }

    public static boolean allowName = true;
    public static boolean allowLore = true;
    public static boolean allowAttributes = false;
    public static int globalMaxEnchant = 5;
    public static int maxMending = 1;
    public static int maxUnbreaking = 3;
    public static int maxVanishing = 1;
    public static int maxAquaAffinity = 1;
    public static int maxBlastProtection = 4;
    public static int maxCurseOfBinding = 1;
    public static int maxDepthStrider = 3;
    public static int maxFeatherFalling = 4;
    public static int maxFireProtection = 4;
    public static int maxFrostWalker = 2;
    public static int maxProjectileProtection = 4;
    public static int maxProtection = 4;
    public static int maxRespiration = 3;
    public static int maxSoulSpeed = 3;
    public static int maxThorns = 3;
    public static int maxSwiftSneak = 3;
    public static int maxBaneOfArthropods = 5;
    public static int maxEfficiency = 5;
    public static int maxFireAspect = 2;
    public static int maxLooting = 3;
    public static int maxImpaling = 5;
    public static int maxKnockback = 2;
    public static int maxSharpness = 5;
    public static int maxSmite = 5;
    public static int maxSweepingEdge = 3;
    public static int maxChanneling = 1;
    public static int maxFlame = 1;
    public static int maxInfinity = 1;
    public static int maxLoyalty = 3;
    public static int maxRiptide = 3;
    public static int maxMultishot = 1;
    public static int maxPiercing = 4;
    public static int maxPower = 5;
    public static int maxPunch = 2;
    public static int maxQuickCharge = 3;
    public static int maxFortune = 3;
    public static int maxLuckOfTheSea = 3;
    public static int maxLure = 3;
    public static int maxSilkTouch = 1;
}
