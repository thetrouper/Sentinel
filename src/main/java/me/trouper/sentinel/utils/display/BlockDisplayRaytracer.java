package me.trouper.sentinel.utils.display;

import me.trouper.sentinel.Sentinel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

public class BlockDisplayRaytracer {

    private static final Sentinel system = Sentinel.getInstance();

    public static void outline(Material display, Location location, long stayTime, List<Player> viewers) {
        outline(display, location, 0.05, stayTime, viewers);
    }

    public static void outline(Material display, Location corner1, Location corner2, double thickness, long stayTime, List<Player> viewers) {
        World world = corner1.getWorld();

        // Use block coordinates
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        // Adjust max values by adding 1 so the outline is drawn at the block edges
        Location a1 = new Location(world, minX, minY, minZ);
        Location a2 = new Location(world, maxX + 1, minY, minZ);
        Location a3 = new Location(world, maxX + 1, minY, maxZ + 1);
        Location a4 = new Location(world, minX, minY, maxZ + 1);

        Location b1 = new Location(world, minX, maxY + 1, minZ);
        Location b2 = new Location(world, maxX + 1, maxY + 1, minZ);
        Location b3 = new Location(world, maxX + 1, maxY + 1, maxZ + 1);
        Location b4 = new Location(world, minX, maxY + 1, maxZ + 1);

        // Bottom face
        trace(display, a1, a2, thickness, stayTime, viewers);
        trace(display, a2, a3, thickness, stayTime, viewers);
        trace(display, a3, a4, thickness, stayTime, viewers);
        trace(display, a4, a1, thickness, stayTime, viewers);

        // Top face
        trace(display, b1, b2, thickness, stayTime, viewers);
        trace(display, b2, b3, thickness, stayTime, viewers);
        trace(display, b3, b4, thickness, stayTime, viewers);
        trace(display, b4, b1, thickness, stayTime, viewers);

        // Vertical edges
        trace(display, a1, b1, thickness, stayTime, viewers);
        trace(display, a2, b2, thickness, stayTime, viewers);
        trace(display, a3, b3, thickness, stayTime, viewers);
        trace(display, a4, b4, thickness, stayTime, viewers);
    }


    public static void outline(Material display, Location location, double thickness, long stayTime, List<Player> viewers) {
        Location og = location.getBlock().getLocation();

        Location a1 = og.clone().add(0, 0, 0);
        Location a2 = og.clone().add(1, 0, 0);
        Location a3 = og.clone().add(1, 0, 1);
        Location a4 = og.clone().add(0, 0, 1);

        Location b1 = og.clone().add(0, 1, 0);
        Location b2 = og.clone().add(1, 1, 0);
        Location b3 = og.clone().add(1, 1, 1);
        Location b4 = og.clone().add(0, 1, 1);

        trace(display, a1, a2, thickness, stayTime, viewers);
        trace(display, a2, a3, thickness, stayTime, viewers);
        trace(display, a3, a4, thickness, stayTime, viewers);
        trace(display, a4, a1, thickness, stayTime, viewers);

        trace(display, b1, b2, thickness, stayTime, viewers);
        trace(display, b2, b3, thickness, stayTime, viewers);
        trace(display, b3, b4, thickness, stayTime, viewers);
        trace(display, b4, b1, thickness, stayTime, viewers);

        trace(display, a1, b1, thickness, stayTime, viewers);
        trace(display, a2, b2, thickness, stayTime, viewers);
        trace(display, a3, b3, thickness, stayTime, viewers);
        trace(display, a4, b4, thickness, stayTime, viewers);
    }

    public static void trace(Material display, Location start, Location end, long stayTime, List<Player> viewers) {
        trace(display, start, end.toVector().subtract(start.toVector()), 0.05, end.distance(start), stayTime, viewers);
    }

    public static void trace(Material display, Location start, Location end, double thickness, long stayTime, List<Player> viewers) {
        trace(display, start, end.toVector().subtract(start.toVector()), thickness, end.distance(start), stayTime, viewers);
    }

    public static void trace(Material display, Location start, Vector direction, double thickness, double distance, long stayTime, List<Player> viewers) {
        World world = start.getWorld();

        BlockDisplay beam = world.spawn(start, BlockDisplay.class, entity -> {
            AxisAngle4f angle = new AxisAngle4f(0, 0, 0, 1);
            Vector3f transition = new Vector3f(-(float)(thickness / 2F));
            Vector3f scale = new Vector3f((float)thickness, (float)thickness, (float)distance);
            Transformation trans = new Transformation(transition, angle, scale, angle);
            Location vector = entity.getLocation();

            vector.setDirection(direction);
            entity.teleport(vector);
            entity.setBlock(display.createBlockData());
            entity.setBrightness(new Display.Brightness(15, 15));
            entity.setInterpolationDelay(0);
            entity.setTransformation(trans);
            entity.addScoreboardTag("./Sentinel/ Block Display");

            // Hide the entity from all players not in the viewers list
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!viewers.contains(player)) {
                    player.hideEntity(system, entity);
                }
            }

            Bukkit.getScheduler().runTaskLater(system, entity::remove, stayTime);
        });
    }

    public static void trace(Material display, Location start, Vector direction, double thickness, double distance, long stayTime, Consumer<BlockDisplay> onEntitySpawn, List<Player> viewers) {
        World world = start.getWorld();

        BlockDisplay beam = world.spawn(start, BlockDisplay.class, entity -> {
            AxisAngle4f angle = new AxisAngle4f(0, 0, 0, 1);
            Vector3f transition = new Vector3f(-(float)(thickness / 2F));
            Vector3f scale = new Vector3f((float)thickness, (float)thickness, (float)distance);
            Transformation trans = new Transformation(transition, angle, scale, angle);
            Location vector = entity.getLocation();

            vector.setDirection(direction);
            entity.teleport(vector);
            entity.setBlock(display.createBlockData());
            entity.setBrightness(new Display.Brightness(15, 15));
            entity.setInterpolationDelay(0);
            entity.setTransformation(trans);
            entity.addScoreboardTag("./Sentinel/ Block Display");

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!viewers.contains(player)) {
                    player.hideEntity(system, entity);
                }
            }

            Bukkit.getScheduler().runTaskLater(system, entity::remove, stayTime);
            Bukkit.getScheduler().runTaskLater(system, () -> onEntitySpawn.accept(entity), 5);
        });
    }
}
