package me.trouper.sentinel.data.misc;

import me.trouper.sentinel.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public record SerialLocation(String world, double x, double y, double z) {
    public static Location translate(SerialLocation loc) {
        World w = Bukkit.getWorld(loc.world());
        return new Location(w,loc.x(),loc.y(),loc.z());
    }

    public static SerialLocation translate(Location loc) {
        return new SerialLocation(loc.getWorld().getName(),loc.x(),loc.y(),loc.z());
    }

    public Location translate() {
        return translate(this);
    }

    public UUID toUIID() {
        return toUUID(this);
    }

    public boolean isUUID() {
        return this.world.equals("./Sentinel/ UUID$");
    }

    public boolean isSameLocation(Location loc) {
        if (this.isUUID()) return false;
        Location thisLoc = this.translate();
        return thisLoc.getWorld().equals(loc.getWorld()) &&
                thisLoc.getBlockX() == loc.getBlockX() &&
                thisLoc.getBlockY() == loc.getBlockY() &&
                thisLoc.getBlockZ() == loc.getBlockZ();
    }

    public boolean isSameLocation(SerialLocation loc) {
        if (this.isUUID() && loc.isUUID()) {
            return loc.toUIID().equals(this.toUIID());
        } else if (this.isUUID() ^ loc.isUUID()) {
            return false;
        }
        return this.world.equals(loc.world) &&
                (int) this.x == (int) loc.x &&
                (int) this.y == (int) loc.y &&
                (int) this.z == (int) loc.z;
    }

    public static UUID toUUID(SerialLocation loc) {
        if (!loc.world.equals("./Sentinel/ UUID$")) throw new IllegalArgumentException("You can only get UUIDs from locations which hold them.");
        return MathUtils.doublesToUuid(new double[]{loc.x,loc.y,loc.z});
    }

    public static SerialLocation uuidToLocation(UUID uuid) {
        double[] doubles = MathUtils.uuidToDoubles(uuid);
        return new SerialLocation("./Sentinel/ UUID$",doubles[0],doubles[1],doubles[2]);
    }
}
