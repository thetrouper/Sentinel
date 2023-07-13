package io.github.thetrouper.sentinel.server.util.Notifications;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerEvent;
import org.bukkit.inventory.ItemStack;

public class NotifyTrusted {
    public static void specific(Player p, String command, boolean    denied, boolean deoped, boolean punished, boolean logged) {
        TextComponent notification = new TextComponent(TextUtils.prefix("§b§n" + p.getName() + "§7 Has just attempted a specific command!"));
        notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                "§8]==-- §d§lSentinel §8--==[" +
                        "\n§bPlayer: §f" + p.getName() +
                        "\n§bCommand: §f" + command +
                        "\n§bDenied: " + TextUtils.boolString(denied,"§a\u2714","§c\u2718") +
                        "\n§bDeoped: " + TextUtils.boolString(deoped,"§a\u2714","§c\u2718") +
                        "\n§bPunished: " + TextUtils.boolString(punished,"§a\u2714","§c\u2718") +
                        "\n§bLogged: " + TextUtils.boolString(logged,"§a\u2714","§c\u2718")

        )));
        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(notification);
            }
        }
    }
    public static void command(Player p, String command, boolean    denied, boolean deoped, boolean punished, boolean logged) {
        TextComponent notification = new TextComponent(TextUtils.prefix("§b§n" + p.getName() + "§7 Has just attempted a dangerous command!"));
        notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                        "§8]==-- §d§lSentinel §8--==[" +
                                "\n§bPlayer: §f" + p.getName() +
                                "\n§bCommand: §f" + command +
                                "\n§bDenied: " + TextUtils.boolString(denied,"§a\u2714","§c\u2718") +
                                "\n§bDeoped: " + TextUtils.boolString(deoped,"§a\u2714","§c\u2718") +
                                "\n§bPunished: " + TextUtils.boolString(punished,"§a\u2714","§c\u2718") +
                                "\n§bLogged: " + TextUtils.boolString(logged,"§a\u2714","§c\u2718")

        )));

        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(notification);
            }
        }
    }
    public static void NBT(Player p, ItemStack item, boolean removed, boolean deoped, boolean gms, boolean punished, boolean logged) {
        TextComponent notification = new TextComponent(TextUtils.prefix("§b§n" + p.getName() + "§7 Has just attempted to use a dangerous NBT item!"));
        notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                        "§8]==-- §d§lSentinel §8--==[" +
                                "\n§bPlayer: §f" + p.getName() +
                                "\n§bItemType: §f" + item.getType() +
                                "\n§bRemoved: " + TextUtils.boolString(removed,"§a\u2714","§c\u2718") +
                                "\n§bDeoped: " + TextUtils.boolString(deoped,"§a\u2714","§c\u2718") +
                                "\n§bRevert GM: " + TextUtils.boolString(gms, "§a\u2714","§c\u2718") +
                                "\n§bPunished: " + TextUtils.boolString(punished,"§a\u2714","§c\u2718") +
                                "\n§bLogged: " + TextUtils.boolString(logged,"§a\u2714","§c\u2718") +
                                "\n§7(Click to copy NBT)"

                )));
        notification.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, new String(item.getType().toString().toLowerCase() + item.getItemMeta().getAsString())));
        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(notification);
            }
        }
    }
    public static void placeBlock(Player p, Block b, boolean removed, boolean deoped, boolean punished, boolean logged) {
        TextComponent notification = new TextComponent(TextUtils.prefix("§b§n" + p.getName() + "§7 Has just attempted to place a dangerous block!"));
        notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                "§8]==-- §d§lSentinel §8--==[" +
                        "\n§bPlayer: §f" + p.getName() +
                        "\n§bBlockType: §f" + b.getType() +
                        "\n§bLocation: " + b.getX() + " " + b.getY() + " " + b.getZ() +
                        "\n§bRemoved: " + TextUtils.boolString(removed,"§a\u2714","§c\u2718") +
                        "\n§bDeoped: " + TextUtils.boolString(deoped,"§a\u2714","§c\u2718") +
                        "\n§bPunished: " + TextUtils.boolString(punished,"§a\u2714","§c\u2718") +
                        "\n§bLogged: " + TextUtils.boolString(logged,"§a\u2714","§c\u2718") +
                        "\n§7(Click to Teleport)"
        )));
        notification.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "tp "  + p.getName()));
        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(notification);
            }
        }
    }
    public static void usedBlock(Player p, Block b, boolean denied, boolean deoped, boolean punished, boolean logged) {
        TextComponent notification = new TextComponent(TextUtils.prefix("§b§n" + p.getName() + "§7 Has just attempted to use a dangerous block!"));
        notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                "§8]==-- §d§lSentinel §8--==[" +
                        "\n§bPlayer: §f" + p.getName() +
                        "\n§bBlockType: §f" + b.getType() +
                        "\n§bLocation: " + b.getX() + " " + b.getY() + " " + b.getZ() +
                        "\n§bDenied: " + TextUtils.boolString(denied,"§a\u2714","§c\u2718") +
                        "\n§bDeoped: " + TextUtils.boolString(deoped,"§a\u2714","§c\u2718") +
                        "\n§bPunished: " + TextUtils.boolString(punished,"§a\u2714","§c\u2718") +
                        "\n§bLogged: " + TextUtils.boolString(logged,"§a\u2714","§c\u2718") +
                        "\n§7(Click to Teleport)"
        )));
        notification.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "tp "  + p.getName()));
        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(notification);
            }
        }
    }
    public static void usedEntity(Player p, Entity e, boolean denied, boolean deoped, boolean punished, boolean logged) {
        TextComponent notification = new TextComponent(TextUtils.prefix("§b§n" + p.getName() + "§7 Has just attempted to use a dangerous entity!"));
        notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                "§8]==-- §d§lSentinel §8--==[" +
                        "\n§bPlayer: §f" + p.getName() +
                        "\n§bEntityType: §f" + e.getType() +
                        "\n§bLocation: " + e.getLocation().getX() + " " + e.getLocation().getY() + " " + e.getLocation().getZ() +
                        "\n§bDenied: " + TextUtils.boolString(denied,"§a\u2714","§c\u2718") +
                        "\n§bDeoped: " + TextUtils.boolString(deoped,"§a\u2714","§c\u2718") +
                        "\n§bPunished: " + TextUtils.boolString(punished,"§a\u2714","§c\u2718") +
                        "\n§bLogged: " + TextUtils.boolString(logged,"§a\u2714","§c\u2718") +
                        "\n§7(Click to Teleport)"
        )));
        notification.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "tp "  + p.getName()));
        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(notification);
            }
        }
    }
}
