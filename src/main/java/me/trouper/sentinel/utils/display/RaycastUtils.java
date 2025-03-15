package me.trouper.sentinel.utils.display;

import me.trouper.sentinel.Sentinel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class RaycastUtils {

    public static Location raycast(Location start, Location end, Predicate<Location> hitCondition) {
        return raycast(start, end, 0.5, hitCondition);
    }

    public static Location raycast(Location start, Location end, double frequency, Predicate<Location> hitCondition) {
        return raycast(start, end.toVector().subtract(start.toVector()).normalize(), start.distance(end), frequency, hitCondition);
    }

    public static Location raycast(Location start, Vector rotation, double distance, Predicate<Location> hitCondition) {
        return raycast(start, rotation, distance, 0.5, hitCondition);
    }

    public static Location raycast(Location start, Vector rotation, double distance, double frequency, Predicate<Location> hitCondition) {
        for (double i = 0.0; i <= distance; i += frequency) {
            Location point = start.clone().add(rotation.clone().multiply(i));
            if (hitCondition.test(point)) {
                return point;
            }
        }
        return start.clone().add(rotation.clone().multiply(distance));
    }

    public static void raycast(Location start, Vector rotation, double distance, double frequency, long tickInterval, BiPredicate<Location, Double> hitCondition, Consumer<Location> onhit) {
        AtomicReference<Location> result = new AtomicReference<>();
        AtomicReference<Double> val = new AtomicReference<>(0.0);
        AtomicReference<Boolean> active = new AtomicReference<>(true);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Sentinel.getInstance(), () -> {
            if (val.get() <= distance && result.get() == null && active.get()) {
                Location point = start.clone().add(rotation.clone().multiply(val.get()));
                if (hitCondition.test(point, val.get())) {
                    result.set(point);
                }
                val.set(val.get() + frequency);
            }
            else if (result.get() == null) {
                result.set(start.clone().add(rotation.clone().multiply(distance)));
            }
            else if (active.get()) {
                onhit.accept(result.get());
                active.set(false);
            }
        }, 0, tickInterval);
    }

    public static Vector randomVector(Vector baseVector, double angle) {
        double randomAngle = Math.random() * angle;

        double randomAxisX = Math.random() - 0.5;
        double randomAxisY = Math.random() - 0.5;
        double randomAxisZ = Math.random() - 0.5;

        Vector rotationAxis = new Vector(randomAxisX, randomAxisY, randomAxisZ).normalize();

        double rotationAngle = Math.toRadians(randomAngle);

        return baseVector.clone().rotateAroundAxis(rotationAxis, rotationAngle);
    }
}
