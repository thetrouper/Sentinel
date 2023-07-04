package io.github.thetrouper.sentinel.server.util;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.discord.WebhookSender;
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
    private static boolean banned;
    private static boolean opRemoved;
    private static boolean denied;

    public static void logPunishment(Player p, String type, String reason) {

    }

    public static void handleDeniedAction(Player p, String command) {
        ServerUtils.sendDebugMessage(TextUtils.prefix("Handling denied command..."));
        if (!Config.logDangerousCommands) return;
        ServerUtils.sendDebugMessage(TextUtils.prefix("LDC is enabled"));
        logMessage = "]==-- Sentinel --==[\n" +
                "A Dangerous command has been attempted!\n" +
                "Player: " + p.getName() + "\n" +
                "Command: " + command + "\n";
        if (Config.deop) {
            ServerUtils.sendDebugMessage(TextUtils.prefix("Deoping player"));
            p.setOp(false);
            logMessage = logMessage + "Operator Removed: ✔\n";
            opRemoved = true;
        } else {
            logMessage = logMessage + "Operator Removed: ✘\n";
            opRemoved = false;
        }
        if (Config.ban) {
            ServerUtils.sendDebugMessage(TextUtils.prefix("Banning player"));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + p.getName() + " ]=- Sentinel Anti-Grief -=[ You have been banned for attempting a dangerous command. Contact an administrator if you believe this to be a mistake.");
            logMessage = logMessage + "Banned: ✔\n";
            banned = true;
        } else {
            logMessage = logMessage + "Banned: ✘\n";
            banned = false;
        }
        ServerUtils.sendDebugMessage(TextUtils.prefix("Sending log"));
        logMessage = logMessage + "Denied: ✔";
        denied = true;
        Sentinel.log.info(logMessage);
        notifyTrusted(p, command);
        WebhookSender.sendEmbedWarning(p.getName(),command,denied,opRemoved,banned);
    }
    public static void handleDeniedAction(Player p, Block block) {
        if (!Config.logCmdBlocks) return;
        logMessage = "]==-- Sentinel --==[\n" +
                "A Dangerous block usage has been detected!\n" +
                "Player: " + p.getName() + "\n" +
                "BlockType: " + block.getType() + "\n";
        if (Config.deop) {
            p.setOp(false);
            logMessage = logMessage + "Operator Removed: ✔\n";
            opRemoved = true;
        } else {
            logMessage = logMessage + "Operator Removed: ✘\n";
            opRemoved = false;
        }
        if (Config.ban) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + p.getName() + " ]=- Sentinel Anti-Grief -=[ You have been banned for attempting to use dangerous blocks. Contact an administrator if you believe this to be a mistake.");
            logMessage = logMessage + "Banned: ✔\n";
            banned = true;
        } else {
            logMessage = logMessage + "Banned: ✘\n";
            banned = false;
        }
        logMessage = logMessage + "Denied: ✔";
        denied = true;
        Sentinel.log.info(logMessage);
        notifyTrusted(p, block);
        WebhookSender.sendEmbedWarning(p.getName(),block,denied,opRemoved,banned);
    }
    public static void handleDeniedAction(Player p, ItemStack i) {
        if (!Config.logNBT) return;
        logMessage = "]==-- Sentinel --==[\n" +
                "A Dangerous item has been detected!\n" +
                "Player: " + p.getName() + "\n" +
                "ItemType: " + i.getType() + "\n";
        if (Config.deop) {
            p.setOp(false);
            logMessage = logMessage + "Operator Removed: ✔\n";
            opRemoved = true;
        } else {
            logMessage = logMessage + "Operator Removed: ✘\n";
            opRemoved = false;
        }
        if (Config.ban) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + p.getName() + " ]=- Sentinel Anti-Grief -=[ You have been banned for attempting to use an NBT item. Contact an administrator if you believe this to be a mistake.");
            logMessage = logMessage + "Banned: ✔\n";
            banned = true;
        } else {
            logMessage = logMessage + "Banned: ✘\n";
            banned = false;
        }
        logMessage = logMessage + "Denied: ✔";
        denied = true;
        Sentinel.log.info(logMessage);
        notifyTrusted(p, i);
        WebhookSender.sendEmbedWarning(p.getName(),i,denied,opRemoved,banned);
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
