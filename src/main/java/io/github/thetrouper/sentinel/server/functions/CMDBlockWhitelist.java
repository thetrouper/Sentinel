package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.cmdblocks.CMDBlockType;
import io.github.thetrouper.sentinel.data.cmdblocks.WhitelistStorage;
import io.github.thetrouper.sentinel.data.cmdblocks.WhitelistedBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class CMDBlockWhitelist {
    public static void add(CommandBlock cb, UUID owner) {
        boolean alwaysActive = getNBTBoolean(cb, "auto");
        WhitelistedBlock wb = new WhitelistedBlock(owner.toString(),WhitelistedBlock.serialize(cb.getLocation()),getType(cb),alwaysActive,cb.getCommand());

        Location wbl = WhitelistedBlock.fromSerialized(wb.loc());

        for (WhitelistedBlock wl : Sentinel.whitelist.whitelistedCMDBlocks) {
            Location wll = WhitelistedBlock.fromSerialized(wl.loc());
            if (wll.distance(wbl) < 0.5) {
                Sentinel.whitelist.whitelistedCMDBlocks.remove(wb);
            }
        }

        Sentinel.whitelist.whitelistedCMDBlocks.add(wb);
        Sentinel.whitelist.save();
    }

    public static void remove(Location where) {
        for (WhitelistedBlock cb : Sentinel.whitelist.whitelistedCMDBlocks) {
            Location cbl = WhitelistedBlock.fromSerialized(cb.loc());
            if (cbl.distance(where) < 0.5) {
                Sentinel.whitelist.whitelistedCMDBlocks.remove(cb);
                break;
            }
        }

        Sentinel.whitelist.save();
    }

    public static boolean canRun(Block b) {
        CommandBlock test = (CommandBlock) b.getState();
        String command = test.getCommand();
        boolean alwaysActive = getNBTBoolean(test, "auto");
        for (WhitelistedBlock cb : Sentinel.whitelist.whitelistedCMDBlocks) {
            if (!(b.getLocation().distance(WhitelistedBlock.fromSerialized(cb.loc())) < 0.5)) continue;
            if (cb.active() != alwaysActive) return false;
            if (!cb.command().equals(command)) return false;
            if (!cb.type().equals(getType(test))) return false;
            if (!Sentinel.isTrusted(cb.owner())) return false;
            return true;
        }
        return false;
    }

    public static WhitelistedBlock get(Location where) {
        for (WhitelistedBlock cb : Sentinel.whitelist.whitelistedCMDBlocks) {
            Location cbl = WhitelistedBlock.fromSerialized(cb.loc());
            if (cbl.distance(where) < 0.5) {
                return cb;
            }
        }
        return null;
    }

    public static String getType(CommandBlock cb) {
        switch (cb.getType()) {
            case COMMAND_BLOCK -> {
                return "impulse";
            }
            case REPEATING_COMMAND_BLOCK -> {
                return "repeat";
            }
            case CHAIN_COMMAND_BLOCK -> {
                return "chain";
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

    private static NamespacedKey getKey(String key) {
        return new NamespacedKey(Sentinel.getInstance(), key);
    }
}
