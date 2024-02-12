package io.github.thetrouper.sentinel.data.cmdblocks;

import io.papermc.paper.command.CommandBlockHolder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;

import java.util.UUID;

public record WhitelistedBlock(UUID owner, Location loc, CMDBlockType type, boolean active, String command) {

    public static org.bukkit.Location fromSerialized(Location loc) {
        World w = Bukkit.getWorld(loc.world());
        return new org.bukkit.Location(w,loc.x(),loc.y(),loc.z());
    }
}
