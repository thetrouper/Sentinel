package io.github.thetrouper.sentinel.data.cmdblocks;

import org.bukkit.Bukkit;
import org.bukkit.World;

public record WhitelistedBlock(String owner, Location loc, String type, boolean active, String command) {

    public static org.bukkit.Location fromSerialized(Location loc) {
        World w = Bukkit.getWorld(loc.world());
        return new org.bukkit.Location(w,loc.x(),loc.y(),loc.z());
    }
    public static Location serialize(org.bukkit.Location loc) {
        return new Location(loc.getWorld().getName(),loc.x(),loc.y(),loc.z());
    }
}
