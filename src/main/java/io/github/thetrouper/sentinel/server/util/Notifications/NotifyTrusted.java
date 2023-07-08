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
    public static void command(Player p, String command, boolean denied, boolean deoped, boolean banned, boolean logged) {
        TextComponent notification = new TextComponent(TextUtils.prefix(TextUtils.color("&b&n" + p.getName() + "&7 Has just attempted a dangerous command!")));
        notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                TextUtils.color(
                        "&8]==-- &d&lSentinel &8--==[" +
                                "\n&bPlayer: &f" + p.getName() +
                                "\n&bCommand: &f" + command +
                                "\n&bDenied: " + TextUtils.boolString(denied,"&a✔","&c✘") +
                                "\n&bDeoped: " + TextUtils.boolString(deoped,"&a✔","&c✘") +
                                "\n&bBanned: " + TextUtils.boolString(banned,"&a✔","&c✘") +
                                "\n&bLogged: " + TextUtils.boolString(logged,"&a✔","&c✘")
                )
        )));

        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(notification);
            }
        }
    }
    public static void NBT(Player p, ItemStack item, boolean removed, boolean deoped, boolean gms, boolean banned, boolean logged) {
        TextComponent notification = new TextComponent(TextUtils.prefix(TextUtils.color("&b&n" + p.getName() + "&7 Has just attempted to use a dangerous NBT item!")));
        notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                TextUtils.color(
                        "&8]==-- &d&lSentinel &8--==[" +
                                "\n&bPlayer: &f" + p.getName() +
                                "\n&bItemType: &f" + item.getType() +
                                "\n&bRemoved: " + TextUtils.boolString(removed,"&a✔","&c✘") +
                                "\n&bDeoped: " + TextUtils.boolString(deoped,"&a✔","&c✘") +
                                "\n&bRevert GM: " + TextUtils.boolString(gms, "&a✔", "&c✘") +
                                "\n&bBanned: " + TextUtils.boolString(banned,"&a✔","&c✘") +
                                "\n&bLogged: " + TextUtils.boolString(logged,"&a✔","&c✘") +
                                "\n&7(Click to copy NBT)"

                ))));
        notification.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, new String(item.getType().toString().toLowerCase() + item.getItemMeta().getAsString())));
        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(notification);
            }
        }
    }
    public static void placeBlock(Player p, Block b, boolean removed, boolean deoped, boolean banned, boolean logged) {
        TextComponent notification = new TextComponent(TextUtils.prefix(TextUtils.color("&b&n" + p.getName() + "&7 Has just attempted to place a dangerous block!")));
        notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextUtils.color(
                "&8]==-- &d&lSentinel &8--==[" +
                        "\n&bPlayer: &f" + p.getName() +
                        "\n&bBlockType: &f" + b.getType() +
                        "\n&bLocation: " + b.getX() + " " + b.getY() + " " + b.getZ() +
                        "\n&bRemoved: " + TextUtils.boolString(removed,"&a✔","&c✘") +
                        "\n&bDeoped: " + TextUtils.boolString(deoped,"&a✔","&c✘") +
                        "\n&bBanned: " + TextUtils.boolString(banned,"&a✔","&c✘") +
                        "\n&bLogged: " + TextUtils.boolString(logged,"&a✔","&c✘") +
                        "\n&7(Click to Teleport)"
        ))));
        notification.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "tp "  + p.getName()));
        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(notification);
            }
        }
    }
    public static void usedBlock(Player p, Block b, boolean denied, boolean deoped, boolean banned, boolean logged) {
        TextComponent notification = new TextComponent(TextUtils.prefix(TextUtils.color("&b&n" + p.getName() + "&7 Has just attempted to use a dangerous block!")));
        notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextUtils.color(
                "&8]==-- &d&lSentinel &8--==[" +
                        "\n&bPlayer: &f" + p.getName() +
                        "\n&bBlockType: &f" + b.getType() +
                        "\n&bLocation: " + b.getX() + " " + b.getY() + " " + b.getZ() +
                        "\n&bDenied: " + TextUtils.boolString(denied,"&a✔","&c✘") +
                        "\n&bDeoped: " + TextUtils.boolString(deoped,"&a✔","&c✘") +
                        "\n&bBanned: " + TextUtils.boolString(banned,"&a✔","&c✘") +
                        "\n&bLogged: " + TextUtils.boolString(logged,"&a✔","&c✘") +
                        "\n&7(Click to Teleport)"
        ))));
        notification.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "tp "  + p.getName()));
        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(notification);
            }
        }
    }
    public static void usedEntity(Player p, Entity e, boolean denied, boolean deoped, boolean banned, boolean logged) {
        TextComponent notification = new TextComponent(TextUtils.prefix(TextUtils.color("&b&n" + p.getName() + "&7 Has just attempted to use a dangerous entity!")));
        notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextUtils.color(
                "&8]==-- &d&lSentinel &8--==[" +
                        "\n&bPlayer: &f" + p.getName() +
                        "\n&bEntityType: &f" + e.getType() +
                        "\n&bLocation: " + e.getLocation().getX() + " " + e.getLocation().getY() + " " + e.getLocation().getZ() +
                        "\n&bDenied: " + TextUtils.boolString(denied,"&a✔","&c✘") +
                        "\n&bDeoped: " + TextUtils.boolString(deoped,"&a✔","&c✘") +
                        "\n&bBanned: " + TextUtils.boolString(banned,"&a✔","&c✘") +
                        "\n&bLogged: " + TextUtils.boolString(logged,"&a✔","&c✘") +
                        "\n&7(Click to Teleport)"
        ))));
        notification.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "tp "  + p.getName()));
        for (Player trustedPlayer : Bukkit.getOnlinePlayers()) {
            if (Sentinel.isTrusted(trustedPlayer)) {
                trustedPlayer.spigot().sendMessage(notification);
            }
        }
    }
}
