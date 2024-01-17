package io.github.thetrouper.sentinel.server.util;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.cmds.SentinelCommand;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ServerUtils {
    public static void sendCommand(String command) {
        ServerUtils.sendDebugMessage("Getting scheduler");
        Bukkit.getScheduler().scheduleSyncDelayedTask(Sentinel.getInstance(), () -> {
            try {
                ServerUtils.sendDebugMessage("Attempting to run command...");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        },1);
    }
    public static void sendDebugMessage(String message) {
        if (SentinelCommand.debugMode) {
            String log = "[Sentinel] [DEBUG]: " + message;
            Sentinel.log.info(log);
            for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
                if (Sentinel.isTrusted(trustedPlayer)) {
                    trustedPlayer.sendMessage("§d§lSentinel §7[§bDEBUG§7] §8» §7" + message);
                }
            }
        }
    }

    public static List<Player> getPlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    public static List<Player> getStaff() {
        return getPlayers().stream().filter(Player -> Player.hasPermission("sentinel.staff")).toList();
    }

    public static void forEachPlayer(Consumer<Player> consumer) {
        getPlayers().forEach(consumer);
    }

    public static void forEachStaff(Consumer<Player> consumer) {
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
    public static void sendActionBar(Player p, String msg) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
    }

    public static boolean hasBlockBelow(Player player, Material material) {
        for (int y = player.getLocation().getBlockY() - 1; y >= player.getLocation().getBlockY() - 12; y--) {
            if (player.getWorld().getBlockAt(player.getLocation().getBlockX(), y, player.getLocation().getBlockZ()).getType() == material) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    public static String[] unVanishedPlayers() {
        return io.github.itzispyder.pdk.utils.ServerUtils.players(ServerUtils::isVanished).stream().map(Player::getName).toArray(String[]::new);
    }
}
