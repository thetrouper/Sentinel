package io.github.thetrouper.sentinel.server.util;

import io.github.thetrouper.sentinel.Sentinel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

public class DeniedActions {
    private static String logMessage;
    public static void handleDeniedAction(Player p, String command) {
        if (!Sentinel.logDangerousCommands) return;
        logMessage = "]==-- Sentinel --==[\n" +
                "A Dangerous command has been attempted!\n" +
                "Player: " + p.getName() + "\n" +
                "Command: " + command + "\n";
        if (Sentinel.deop) {
            p.setOp(false);
            logMessage = logMessage + "Operator Removed: ✔\n";
        } else {
            logMessage = logMessage + "Operator Removed: ✘\n";
        }
        if (Sentinel.ban) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + p.getName() + " ]=- Sentinel Anti-Grief -=[ You have been banned for attempting a dangerous command. Contact an administrator if you believe this to be a mistake.");
            logMessage = logMessage + "Banned: ✔\n";
        } else {
            logMessage = logMessage + "Banned: ✘\n";
        }
        logMessage = logMessage + "Denied: ✔";
        Sentinel.log.info(logMessage);
        notifyTrusted(p, command);
    }
    public static void handleDeniedAction(Player p, Block block) {
        if (!Sentinel.logCmdBlocks) return;
        logMessage = "]==-- Sentinel --==[\n" +
                "A Dangerous block usage has been detected!\n" +
                "Player: " + p.getName() + "\n" +
                "BlockType: " + block.getType() + "\n";
        if (Sentinel.deop) {
            p.setOp(false);
            logMessage = logMessage + "Operator Removed: ✔\n";
        } else {
            logMessage = logMessage + "Operator Removed: ✘\n";
        }
        if (Sentinel.ban) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + p.getName() + " ]=- Sentinel Anti-Grief -=[ You have been banned for attempting to use dangerous blocks. Contact an administrator if you believe this to be a mistake.");
            logMessage = logMessage + "Banned: ✔\n";
        } else {
            logMessage = logMessage + "Banned: ✘\n";
        }
        logMessage = logMessage + "Denied: ✔";
        Sentinel.log.info(logMessage);
        notifyTrusted(p, block);
    }
    public static void handleDeniedAction(Player p, ItemStack i) {
        if (!Sentinel.logNBT) return;
        logMessage = "]==-- Sentinel --==[\n" +
                "A Dangerous item has been detected!\n" +
                "Player: " + p.getName() + "\n" +
                "ItemType: " + i.getType() + "\n";
        if (Sentinel.deop) {
            p.setOp(false);
            logMessage = logMessage + "Operator Removed: ✔\n";
        } else {
            logMessage = logMessage + "Operator Removed: ✘\n";
        }
        if (Sentinel.ban) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + p.getName() + " ]=- Sentinel Anti-Grief -=[ You have been banned for attempting a dangerous command. Contact an administrator if you believe this to be a mistake.");
            logMessage = logMessage + "Banned: ✔\n";
        } else {
            logMessage = logMessage + "Banned: ✘\n";
        }
        logMessage = logMessage + "Denied: ✔";
        Sentinel.log.info(logMessage);
        notifyTrusted(p, i);
    }
    private static void notifyTrusted(Player p, String command) {
        TextComponent message = new TextComponent(TextUtils.prefix(p.getName() + " has attempted a dangerous command!"));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                "§8]==-- §d§lSentinel §8--==[\n" +
                        "§7Player: §b" + p.getName() + "\n" +
                        "§7Command: §b" + command + "\n" +
                        "§7Trusted: §cfalse\n" +
                        "§7Denied: §atrue")));
        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(message);
            }
        }
    }
    private static void notifyTrusted(Player p, Block b) {
        TextComponent message = new TextComponent(TextUtils.prefix(p.getName() + " has attempted to use a dangerous block!"));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                "§8]==-- §d§lSentinel §8--==[\n" +
                        "§7Player: §b" + p.getName() + "\n" +
                        "§7BlockType: §b" + b.getType() + "\n" +
                        "§7Trusted: §cfalse\n" +
                        "§7Denied: §atrue")));
        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(message);
            }
        }
    }
    private static void notifyTrusted(Player p, ItemStack i) {
        TextComponent message = new TextComponent(TextUtils.prefix(p.getName() + " has attempted to use a dangerous item"));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                "§8]==-- §d§lSentinel §8--==[\n" +
                        "§7Player: §b" + p.getName() + "\n" +
                        "§7ItemType: §b" + i.getType() + "\n" +
                        "§7Trusted: §cfalse\n" +
                        "§7Denied: §atrue\n" +
                        "§8(Click to copy ItemMeta)")));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, new String(
                i.getItemMeta().toString()
        )));
        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(message);
            }
        }
    }
}
