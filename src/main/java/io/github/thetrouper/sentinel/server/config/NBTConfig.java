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

    public static boolean allowName;
    public static boolean allowLore;
    public static boolean allowAttributes;
    public static int globalMaxEnchant;
    public static int maxMending;
    public static int maxUnbreaking;
    public static int maxVanishing;
    public static int maxAquaAffinity;
    public static int maxBlastProtection;
    public static int maxCurseOfBinding;
    public static int maxDepthStrider;
    public static int maxFeatherFalling;
    public static int maxFireProtection;
    public static int maxFrostWalker;
    public static int maxProjectileProtection;
    public static int maxProtection;
    public static int maxRespiration;
    public static int maxSoulSpeed;
    public static int maxThorns;
    public static int maxSwiftSneak;
    public static int maxBaneOfArthropods;
    public static int maxEfficiency;
    public static int maxFireAspect;
    public static int maxLooting;
    public static int maxImpaling;
    public static int maxKnockback;
    public static int maxSharpness;
    public static int maxSmite;
    public static int maxSweepingEdge;
    public static int maxChanneling;
    public static int maxFlame;
    public static int maxInfinity;
    public static int maxLoyalty;
    public static int maxRiptide;
    public static int maxMultishot;
    public static int maxPiercing;
    public static int maxPower;
    public static int maxPunch;
    public static int maxQuickCharge;
    public static int maxFortune;
    public static int maxLuckOfTheSea;
    public static int maxLure;
    public static int maxSilkTouch;
}
