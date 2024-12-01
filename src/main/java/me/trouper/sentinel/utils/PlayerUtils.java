package me.trouper.sentinel.utils;

import me.trouper.sentinel.Sentinel;
import org.bukkit.entity.Player;

public class PlayerUtils {
    public static boolean isTrusted(Player player) {
        return Sentinel.mainConfig.plugin.trustedPlayers.contains(player.getUniqueId().toString());
    }

    public static boolean isTrusted(String uuid) {
        return Sentinel.mainConfig.plugin.trustedPlayers.contains(uuid);
    }
}
