package me.trouper.sentinel.utils;

import me.trouper.sentinel.Sentinel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class PlayerUtils {
    public static boolean isTrusted(Player player) {
        return Sentinel.getInstance().getDirector().io.mainConfig.plugin.trustedPlayers.contains(player.getUniqueId().toString());
    }

    public static boolean isTrusted(String uuid) {
        return Sentinel.getInstance().getDirector().io.mainConfig.plugin.trustedPlayers.contains(uuid);
    }

    public static boolean isTrusted(UUID uuid) {
        return isTrusted(uuid.toString());
    }

    public static boolean isTrusted(CommandSender sender) {
        return (sender instanceof Player p && isTrusted(p)) || sender instanceof ConsoleCommandSender;
    }

    public static boolean playerCheck(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.permissions.playersOnly));
            return false;
        }
        return true;
    }

    public static boolean checkPermission(CommandSender sender, String permission) {
        if (sender instanceof ConsoleCommandSender || (sender instanceof Player p && p.hasPermission(permission))) return true;
        sender.sendMessage(Sentinel.getInstance().getDirector().io.lang.permissions.noPermission);
        return false;
    }

    public static List<Player> getPlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    public static List<Player> getStaff() {
        return getPlayers().stream().filter(Player -> Player.hasPermission("sentinel.staff")).toList();
    }
    public static List<Player> getTrusted() {
        return getPlayers().stream().filter(PlayerUtils::isTrusted).toList();
    }

    public static void forEachPlayer(Consumer<Player> consumer) {
        getPlayers().forEach(consumer);
    }

    public static void forEachStaff(Consumer<Player> consumer) {
        getStaff().forEach(consumer);
    }
    public static void forEachTrusted(Consumer<Player> consumer) {
        getStaff().forEach(consumer);
    }

    public static void dmEachPlayer(Predicate<Player> condition, String dm) {
        forEachPlayer(p -> {
            if (condition.test(p)) p.sendMessage(dm);
        });
    }

    public static void dmEachPlayer(String dm) {
        forEachPlayer(p -> p.sendMessage(dm));
    }

    public static void forEachSpecified(Iterable<Player> players, Consumer<Player> consumer) {
        players.forEach(consumer);
    }

    public static void forEachSpecified(Consumer<Player> consumer, Player... players) {
        Arrays.stream(players).forEach(consumer);
    }
    public static void forEachPlayerRun(Predicate<Player> condition, Consumer<Player> task) {
        forEachPlayer(p -> {
            if (condition.test(p)) {
                task.accept(p);
            }
        });
    }    
}
