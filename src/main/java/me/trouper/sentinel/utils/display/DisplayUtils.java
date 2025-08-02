package me.trouper.sentinel.utils.display;

import io.github.itzispyder.pdk.utils.misc.Randomizer;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.Selection;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;

public final class DisplayUtils {
    public static void drawSelection(Player player, Selection selection) {
        if (selection == null || !selection.isComplete()) return;

        Location pos1 = selection.getPos1();
        Location pos2 = selection.getPos2();

        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        // Define the 12 edges
        Location[] edgeStart = {
                new Location(world, minX, minY, minZ), new Location(world, maxX, minY, minZ),
                new Location(world, minX, minY, maxZ), new Location(world, maxX, minY, maxZ),

                new Location(world, minX, maxY, minZ), new Location(world, maxX, maxY, minZ),
                new Location(world, minX, maxY, maxZ), new Location(world, maxX, maxY, maxZ),

                new Location(world, minX, minY, minZ), new Location(world, minX, maxY, minZ),
                new Location(world, maxX, minY, minZ), new Location(world, maxX, maxY, minZ),

                new Location(world, minX, minY, maxZ), new Location(world, minX, maxY, maxZ),
                new Location(world, maxX, minY, maxZ), new Location(world, maxX, maxY, maxZ)
        };

        Location[] edgeEnd = {
                new Location(world, maxX, minY, minZ), new Location(world, maxX, minY, maxZ),
                new Location(world, minX, minY, maxZ), new Location(world, minX, minY, minZ),

                new Location(world, maxX, maxY, minZ), new Location(world, maxX, maxY, maxZ),
                new Location(world, minX, maxY, maxZ), new Location(world, minX, maxY, minZ),

                new Location(world, minX, maxY, minZ), new Location(world, minX, maxY, minZ),
                new Location(world, maxX, maxY, minZ), new Location(world, maxX, maxY, minZ),

                new Location(world, minX, maxY, maxZ), new Location(world, minX, maxY, maxZ),
                new Location(world, maxX, maxY, maxZ), new Location(world, maxX, maxY, maxZ)
        };

        for (int i = 0; i < edgeStart.length; i++) {
            BlockDisplayRaytracer.trace(Material.LIGHT_BLUE_STAINED_GLASS,edgeStart[i],edgeEnd[i],0.2,20,List.of(player));

        }
    }


    public static final Function<Particle, BiConsumer<Player, Location>> PLAYER_PARTICLE_FACTORY = particle -> (p,l) -> p.spawnParticle(particle, l, 1, 0, 0, 0, 0);

    public static final BiFunction<Color, Float, BiConsumer<Player, Location>> PLAYER_DUST_PARTICLE_FACTORY = (color, thickness) -> {
        Particle.DustOptions dust = new Particle.DustOptions(color, thickness);
        return (p,l) -> p.spawnParticle(Particle.DUST, l, 1, 0, 0, 0, 0, dust);
    };

    public static final Function<Boolean, BiConsumer<Player, Location>> PLAYER_FLAME_PARTICLE_FACTORY = soul -> {
        Particle flame = soul ? Particle.SOUL_FIRE_FLAME : Particle.FLAME;
        return (p,l) -> p.spawnParticle(flame, l, 1, 0, 0, 0, 0);
    };

    public static final Function<Particle, Consumer<Location>> PARTICLE_FACTORY = particle -> l -> l.getWorld().spawnParticle(particle, l, 1, 0, 0, 0, 0);

    public static final BiFunction<Color, Float, Consumer<Location>> DUST_PARTICLE_FACTORY = (color, thickness) -> {
        Particle.DustOptions dust = new Particle.DustOptions(color, thickness);
        return l -> l.getWorld().spawnParticle(Particle.DUST, l, 1, 0, 0, 0, 0, dust);
    };

    public static final Function<Boolean, Consumer<Location>> FLAME_PARTICLE_FACTORY = soul -> {
        Particle flame = soul ? Particle.SOUL_FIRE_FLAME : Particle.FLAME;
        return l -> l.getWorld().spawnParticle(flame, l, 1, 0, 0, 0, 0);
    };

    public static void ring(Location loc, double radius, Color color, float thickness) {
        ring(loc, radius, DUST_PARTICLE_FACTORY.apply(color, thickness));
    }

    public static void ring(Location loc, double radius, Consumer<Location> action) {
        for (int theta = 0; theta < 360; theta += 10) {
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;
            Location newLoc = loc.clone().add(x, 0, z);
            action.accept(newLoc);
        }
    }

    public static void wave(Location loc, double radius, Color color, float thickness, double gap) {
        wave(loc, radius, DUST_PARTICLE_FACTORY.apply(color, thickness), gap);
    }

    public static void wave(Location loc, double radius, Consumer<Location> action, double gap) {
        AtomicReference<Double> i = new AtomicReference<>(gap);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Sentinel.getInstance(), () -> {
            if (i.get() >= radius) {
                return;
            }
            ring(loc, i.get(), action);
            i.set(i.get() + gap);
        }, 0, 1);
    }

    public static void wave(Location center, double radius, double frequency, long interval, Consumer<Location> onPoint) {
        AtomicReference<Double> currentRadius = new AtomicReference<>(0.0);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Sentinel.getInstance(), () -> {
            if (currentRadius.get() <= radius) {
                ring(center, currentRadius.get(), onPoint, (point, angle) -> true);
                currentRadius.set(currentRadius.get() + frequency);
            }
        }, 0, interval);
    }


    public static void disc(Location loc, double radius, Consumer<Location> action, double gap) {
        for (double i = gap; i < radius; i += gap) {
            ring(loc, i, action);
        }
    }

    public static void helix(Location loc, double radius, Consumer<Location> action, double gap, int height) {
        int theta = 0;
        for (double y = 0; y <= height; y += gap) {
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;

            Location newLoc = loc.clone().add(x, y, z);
            action.accept(newLoc);
            theta += 10;
        }
    }

    public static void vortex(Location loc, double radius, Consumer<Location> action, double gapH, double gapV, int height) {
        double r = radius;
        int theta = 0;
        for (double y = 0; y <= height; y += gapV) {
            double x = Math.cos(Math.toRadians(theta)) * r;
            double z = Math.sin(Math.toRadians(theta)) * r;

            Location newLoc = loc.clone().add(x, y, z);
            action.accept(newLoc);
            r += gapH;
            theta += 10;
        }
    }

    public static void beam(Location loc, Consumer<Location> action, double gap, int height) {
        for (double y = 0; y <= height; y += gap) {
            Location newLoc = loc.clone().add(0, y, 0);
            action.accept(newLoc);
        }
    }

    public static void arc(Location loc, double radius, int angleFrom, int angleTo, Consumer<Location> action) {
        for (int theta = angleFrom; theta < angleTo; theta += 10) {
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;
            Location newLoc = loc.clone().add(x, 0, z);
            action.accept(newLoc);
        }
    }

    public static void fan(Location loc, double radius, int angleFrom, int angleTo, Consumer<Location> action, double gap) {
        for (double i = gap; i < radius; i += gap) {
            arc(loc, i, angleFrom, angleTo, action);
        }
    }

    public static void fanWave(Location loc, double radius, int sections, Consumer<Location> action, double gap) {
        double arcLength = 360.0 / sections;
        AtomicReference<Double> i = new AtomicReference<>(0.0);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Sentinel.getInstance(), () -> {
            if (i.get() >= 360) {
                return;
            }
            double start = i.get();
            fan(loc, radius, (int)start, (int)(start + arcLength), action, gap);
            i.set(i.get() + arcLength);
        }, 0, 5);
    }

    public static void fanWaveRandom(Location loc, double radius, int sections, Consumer<Location> action, double gap) {
        double arcLength = 360.0 / sections;
        List<Double> ints = new ArrayList<>();
        for (double start = 0; start < 360; start += arcLength) {
            ints.add(start);
        }

        AtomicInteger i = new AtomicInteger(0);
        Randomizer random = new Randomizer();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Sentinel.getInstance(), () -> {
            if (i.get() >= sections) {
                return;
            }
            double start = random.getRandomElement(ints);
            ints.remove(start);
            fan(loc, radius, (int)start, (int)(start + arcLength), action, gap);
            i.getAndIncrement();
        }, 0, 5);
    }


    public static void ring(Location center, double radius, Consumer<Location> onPoint, BiPredicate<Location, Integer> condition) {
        for (int i = 0; i <= 360; i ++) {
            Location point = center.clone().add(radius * Math.sin(i), 0, radius * Math.cos(i));
            if (condition.test(point, i)) {
                onPoint.accept(point);
            }
        }
    }
}
