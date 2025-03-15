package me.trouper.sentinel.server.functions.helpers;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.SerialLocation;
import me.trouper.sentinel.data.types.CommandBlockHolder;
import me.trouper.sentinel.server.events.admin.WandEvents;
import me.trouper.sentinel.data.types.Selection;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CBWhitelistManager {

    public Set<UUID> autoWhitelist = new HashSet<>();

    public CommandBlockHolder generateHolder(UUID owner, CommandMinecart cm) {
        return new CommandBlockHolder(owner.toString(),
                SerialLocation.uuidToLocation(cm.getUniqueId()),
                "minecart",
                cm.getType().name(),
                false,
                false,
                cm.getCommand()
        );
    }

    public CommandBlockHolder generateHolder(UUID owner, CommandBlock cb) {
        return new CommandBlockHolder(owner.toString(),
                SerialLocation.translate(cb.getLocation()),
                serializeFacing(cb.getBlock()),
                serializeType(cb),
                isAuto(cb),
                isConditional(cb),
                cb.getCommand()
        );
    }

    public void removeSelectionFromWhitelist(Player player) {
        Selection selection = WandEvents.selections.get(player.getUniqueId());
        if (selection == null || !selection.isComplete()) {
            player.sendMessage(Text.prefix("You must set 2 points first."));
            return;
        }
        AtomicInteger number = new AtomicInteger();
        selection.forEachBlock(block -> {
            if (block.getType().equals(Material.COMMAND_BLOCK) || block.getType().equals(Material.REPEATING_COMMAND_BLOCK) || block.getType().equals(Material.CHAIN_COMMAND_BLOCK)) {
                generateHolder(player.getUniqueId(),(CommandBlock) block.getState()).removeFromWhitelist();
                number.getAndIncrement();
            }
        });

        player.sendMessage(Text.prefix("Removed all &b%s&7 command blocks from the whitelist in your selection.".formatted(number.get())));
    }

    public void deleteSelection(Player player) {
        Selection selection = WandEvents.selections.get(player.getUniqueId());
        if (selection == null || !selection.isComplete()) {
            player.sendMessage(Text.prefix("You must set 2 points first."));
            return;
        }
        AtomicInteger number = new AtomicInteger();
        selection.forEachBlock(block -> {
            if (block.getType().equals(Material.COMMAND_BLOCK) || block.getType().equals(Material.REPEATING_COMMAND_BLOCK) || block.getType().equals(Material.CHAIN_COMMAND_BLOCK)) {
                generateHolder(player.getUniqueId(),(CommandBlock) block.getState()).destroy();
                number.getAndIncrement();
            }
        });

        player.sendMessage(Text.prefix("Deleted all &b%s&7 command blocks from the whitelist in your selection.".formatted(number.get())));
    }

    public void addSelectionToWhitelist(Player player) {
        Selection selection = WandEvents.selections.get(player.getUniqueId());
        if (selection == null || !selection.isComplete()) {
            player.sendMessage(Text.prefix("You must set 2 points first."));
            return;
        }

        AtomicInteger number = new AtomicInteger();
        selection.forEachBlock(block -> {
            if (ServerUtils.isCommandBlock(block)) {
                CommandBlock cb = (CommandBlock) block.getState();
                generateHolder(player.getUniqueId(),cb).addToWhitelist();
                number.getAndIncrement();
            }
        });

        player.sendMessage(Text.prefix("Whitelisted all &b%s&7 command blocks in your selection.".formatted(number.get())));
    }

    public int clearAll() {
        int total = 0;
        for (CommandBlockHolder cb : Sentinel.getInstance().getDirector().io.commandBlocks.holders) {
            cb.removeFromWhitelist();
            total++;

            if (cb.loc().isUUID()) continue;
            Location remove = SerialLocation.translate(cb.loc());
            remove.getBlock().setType(Material.AIR);
        }
        return total;
    }

    public int clearAll(UUID who) {
        int total = 0;
        for (CommandBlockHolder cb : Sentinel.getInstance().getDirector().io.commandBlocks.holders) {
            if (!cb.owner().equals(who.toString())) continue;
            cb.removeFromWhitelist();
            total++;

            if (cb.loc().isUUID()) continue;
            Location remove = SerialLocation.translate(cb.loc());
            remove.getBlock().setType(Material.AIR);
        }
        return total;
    }

    public int restoreAll() {
        int total = 0;
        for (CommandBlockHolder cb : Sentinel.getInstance().getDirector().io.commandBlocks.holders) {
            if (cb.isWhitelisted() && cb.restore()) total++;
        }
        return total;
    }

    public int restoreAll(UUID who) {
        int total = 0;
        for (CommandBlockHolder cb : Sentinel.getInstance().getDirector().io.commandBlocks.holders) {
            if (!cb.owner().equals(who.toString())) continue;
            if (cb.isWhitelisted() && cb.restore()) total++;
        }
        return total;
    }

    public String serializeFacing(Block block) {
        if (block.getBlockData() instanceof Directional directional) {
            return directional.getFacing().name();
        }
        return "UNKNOWN";
    }

    public String serializeType(CommandBlock cb) {
        return cb.getType().name();
    }

    public boolean isAuto(CommandBlock cb) {
        return cb.getPersistentDataContainer().getOrDefault(Sentinel.getInstance().getNamespace("auto"), PersistentDataType.BYTE,(byte) 0) == (byte) 1;
    }

    public boolean isConditional(CommandBlock cb) {
        return cb.getBlock().getBlockData() instanceof org.bukkit.block.data.type.CommandBlock cbs && cbs.isConditional();
    }
}
