package me.trouper.sentinel.utils;

import me.trouper.sentinel.Sentinel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class ServerUtils {
    
    public static boolean isCommandBlock(Block b) {
        return b.getType().equals(Material.COMMAND_BLOCK) || b.getType().equals(Material.REPEATING_COMMAND_BLOCK) || b.getType().equals(Material.CHAIN_COMMAND_BLOCK);
    }

    public static void sendCommand(String command) {
        ServerUtils.verbose("Getting scheduler");
        Bukkit.getScheduler().scheduleSyncDelayedTask(Sentinel.getInstance(), () -> {
            try {
                ServerUtils.verbose("Attempting to run command...");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        },1);
    }

    public static void verbose(int backtrace, String message, Object... args) {
        if (!Sentinel.getInstance().getDirector().io.mainConfig.debugMode) return;
        String callerInfo = "Unknown Caller";

        // Capture the stack trace to determine the caller
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 2 + backtrace) { // Ensure we have enough depth
            StackTraceElement caller = stackTrace[2 + backtrace]; // The method that called `verbose()`

            String className = caller.getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
            if (className.contains("-")) {
                callerInfo = "Protected";
            } else {
                callerInfo = className + "." + caller.getMethodName();
            }


        }

        String formattedMessage = message.formatted(args);
        String log = "[Sentinel] [DEBUG ^ %s] [%s]: %s".formatted(backtrace, callerInfo, formattedMessage);
        Sentinel.getInstance().getLogger().info(log);

        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (PlayerUtils.isTrusted(trustedPlayer)) {
                trustedPlayer.sendMessage("§d§lSentinel §7[§bDEBUG§7] §7[§e%s§7] §8» §7%s"
                        .formatted(callerInfo, formattedMessage));
            }
        }
    }

    public static void verbose(String message, Object... args) {
        if (!Sentinel.getInstance().getDirector().io.mainConfig.debugMode) return;
        String callerInfo = "Unknown Caller";

        // Capture the stack trace to determine the caller
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 2) { // Ensure we have enough depth
            StackTraceElement caller = stackTrace[2]; // The method that called `verbose()`

            String className = caller.getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
            if (className.contains("-")) {
                callerInfo = "Protected";
            } else {
                callerInfo = className + "." + caller.getMethodName();
            }
            

        }

        String formattedMessage = message.formatted(args);
        String log = "[Sentinel] [DEBUG] [%s]: %s".formatted(callerInfo, formattedMessage);
        Sentinel.getInstance().getLogger().info(log);

        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (PlayerUtils.isTrusted(trustedPlayer)) {
                trustedPlayer.sendMessage("§d§lSentinel §7[§bDEBUG§7] §7[§e%s§7] §8» §7%s"
                        .formatted(callerInfo, formattedMessage));
            }
        }
    }

    public static String getPublicIPAddress() {
        try {
            String apiUrl = "http://checkip.amazonaws.com";

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                return response.toString().trim();
            }
            connection.disconnect();
            return null;
        } catch (Exception e) {
            Sentinel.getInstance().getLogger().warning(e.getMessage());
            return null;
        }
    }

    public static int getPort() {
        return Bukkit.getPort();
    }
}
