package me.trouper.sentinel.server.functions;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.WhitelistedBlock;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CBWhitelistManager {

    public static Set<UUID> autoWhitelist = new HashSet<>();

    public static void add(CommandBlock cb, UUID owner) {
        ServerUtils.verbose("Adding a command block to the whitelist.");
        boolean alwaysActive = getNBTBoolean(cb, "auto");
        WhitelistedBlock wb = new WhitelistedBlock(owner.toString(),WhitelistedBlock.serialize(cb.getLocation()),getType(cb),alwaysActive,cb.getCommand());

        Location wbloc = WhitelistedBlock.fromSerialized(wb.loc());

        remove(wbloc);

        Sentinel.whitelist.whitelistedCMDBlocks.add(wb);
        Sentinel.whitelist.save();
        if (Bukkit.getPlayer(owner) != null && !Bukkit.getPlayer(owner).isOnline()) return;
        Bukkit.getPlayer(owner).sendMessage(Text.prefix("Successfully whitelisted a &b" + Text.cleanName(cb.getType().toString()) + "&7 with the command &a" + cb.getCommand() + "&7."));
    }

    public static void remove(Location where) {
        for (WhitelistedBlock cb : Sentinel.whitelist.whitelistedCMDBlocks) {
            Location cbl = WhitelistedBlock.fromSerialized(cb.loc());
            if (cbl.distance(where) < 0.5) {
                Sentinel.whitelist.whitelistedCMDBlocks.remove(cb);
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
            return PlayerUtils.isTrusted(cb.owner());
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

    public static int clearAll() {
        int total = 0;
        for (WhitelistedBlock cb : Sentinel.whitelist.whitelistedCMDBlocks) {
            Location remove = WhitelistedBlock.fromSerialized(cb.loc());
            remove(remove);
            remove.getBlock().setType(Material.AIR);
            total++;
        }
        return total;
    }

    public static int clearAll(UUID who) {
        int total = 0;
        for (WhitelistedBlock cb : Sentinel.whitelist.whitelistedCMDBlocks) {
            if (!cb.owner().equals(who.toString())) continue;
            Location remove = WhitelistedBlock.fromSerialized(cb.loc());
            remove(remove);
            remove.getBlock().setType(Material.AIR);
            total++;
        }
        return total;
    }

    public static int restoreAll() {
        int total = 0;
        for (WhitelistedBlock cb : Sentinel.whitelist.whitelistedCMDBlocks) {
            if (restore(WhitelistedBlock.fromSerialized(cb.loc()))) total++;
        }
        return total;
    }

    public static int restoreAll(UUID who) {
        int total = 0;
        for (WhitelistedBlock cb : Sentinel.whitelist.whitelistedCMDBlocks) {
            if (!cb.owner().equals(who.toString())) continue;
            if (restore(WhitelistedBlock.fromSerialized(cb.loc()))) total++;
        }
        return total;
    }


    public static boolean restore(Location where) {
        WhitelistedBlock wb = get(where);
        if (wb == null) {
            ServerUtils.verbose("No whitelisted command block found at the specified location.");
            return false;
        }

        Block block = where.getBlock();
        block.setType(getBlockType(wb.type()));
        if (!(block.getState() instanceof CommandBlock)) {
            ServerUtils.verbose("Block at the location was not a command block (You shouldn't be seeing this. Report it).");
            return false;
        }

        CommandBlock cb = (CommandBlock) block.getState();
        cb.setCommand(wb.command());
        cb.setType(getBlockType(wb.type()));
        setNBTBoolean(cb, "auto", wb.active());

        cb.update();
        ServerUtils.verbose("Command block at " + where.toString() + " has been restored.");
        return true;
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

    private static Material getBlockType(String type) {
        return switch (type) {
            case "impulse" -> Material.COMMAND_BLOCK;
            case "repeat" -> Material.REPEATING_COMMAND_BLOCK;
            case "chain" -> Material.CHAIN_COMMAND_BLOCK;
            default -> throw new IllegalArgumentException("Unknown command block type: " + type);
        };
    }

    private static void setNBTBoolean(CommandBlock cmdBlock, String key, boolean value) {
        cmdBlock.getPersistentDataContainer().set(
                getKey(key),
                PersistentDataType.BYTE,
                value ? (byte) 1 : (byte) 0
        );
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
