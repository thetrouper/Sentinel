package io.github.thetrouper.sentinel.server.functions;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.cmdblocks.CMDBlockType;
import io.github.thetrouper.sentinel.data.cmdblocks.WhitelistStorage;
import io.github.thetrouper.sentinel.data.cmdblocks.WhitelistedBlock;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CommandBlock;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class CMDBlockWhitelist {
    public static void add(CommandBlock cb, UUID owner) {
        boolean alwaysActive = getNBTBoolean(cb, "auto");
        WhitelistedBlock wb = new WhitelistedBlock(owner,cb.getLocation(),getType(cb),alwaysActive,cb.getCommand());

        Location wbl = WhitelistedBlock.fromSerialized(wb.loc());

        for (WhitelistedBlock wl : Sentinel.whitelist.whitelistedCMDBlocks) {
            Location wll = WhitelistedBlock.fromSerialized(wl.loc());
            if (wll.distance(wbl) < 0.5) {
                Sentinel.whitelist.whitelistedCMDBlocks.remove(wb);
                break;
            }
        }

        Sentinel.whitelist.whitelistedCMDBlocks.add(wb);
        Sentinel.whitelist.save();
    }

    public static void remove(Location where) {
        for (WhitelistedBlock cb : Sentinel.whitelist.whitelistedCMDBlocks) {
            if (cb.loc().distance(where) < 0.5) {
                Sentinel.whitelist.whitelistedCMDBlocks.remove(cb);
                break;
            }
        }

        Sentinel.whitelist.save();
    }

    public static WhitelistedBlock get(Location where) {
        for (WhitelistedBlock cb : Sentinel.whitelist.whitelistedCMDBlocks) {
            if (cb.loc().distance(where) < 0.5) {
                return cb;
            }
        }
        return null;
    }

    public static CMDBlockType getType(CommandBlock cb) {
        switch (cb.getType()) {
            case COMMAND_BLOCK -> {
                return CMDBlockType.IMPULSE;
            }
            case REPEATING_COMMAND_BLOCK -> {
                return CMDBlockType.REPEAT;
            }
            case CHAIN_COMMAND_BLOCK -> {
                return CMDBlockType.CHAIN;
            }
        }
        return null;
    }

    private static boolean getNBTBoolean(CommandBlock cmdBlock, String key) {
        return cmdBlock.getPersistentDataContainer().has(
                getKey(key),
                PersistentDataType.BYTE
        ) && cmdBlock.getPersistentDataContainer().get(
                getKey(key),
                PersistentDataType.BYTE
        ) == 1;
    }

    // Helper method to get PersistentDataContainer key
    private static NamespacedKey getKey(String key) {
        return new NamespacedKey(Sentinel.getInstance(), key);
    }
}
