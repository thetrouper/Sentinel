package io.github.thetrouper.sentinel.server.util.Notifications;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NotifyConsole {
    public static void specific(Player p, String command, boolean denied, boolean deoped, boolean punished, boolean logged) {
        final String log = (
                "Sentinel caught a specific command! \n]==-- Sentinel --==[" +
                        "\nPlayer: " + p.getName() +
                        "\nCommand: " + command +
                        "\nDenied: " + TextUtils.boolString(denied,"\u2714","\u2718") +
                        "\nDeoped: " + TextUtils.boolString(deoped,"\u2714","\u2718") +
                        "\nPunished: " + TextUtils.boolString(punished,"\u2714","\u2718") +
                        "\nLogged: " + TextUtils.boolString(logged,"\u2714","\u2718")
        );
        Sentinel.log.info(log);
    }
    public static void command(Player p, String command, boolean denied, boolean deoped, boolean punished, boolean logged) {
        final String log = (
                        "Sentinel caught a dangerous command! \n]==-- Sentinel --==[" +
                                "\nPlayer: " + p.getName() +
                                "\nCommand: " + command +
                                "\nDenied: " + TextUtils.boolString(denied,"\u2714","\u2718") +
                                "\nDeoped: " + TextUtils.boolString(deoped,"\u2714","\u2718") +
                                "\nPunished: " + TextUtils.boolString(punished,"\u2714","\u2718") +
                                "\nLogged: " + TextUtils.boolString(logged,"\u2714","\u2718")
                );
        Sentinel.log.info(log);
    }
    public static void logged(Player p, String command, boolean denied, boolean deoped, boolean punished, boolean logged) {
        final String log = (
                "A logged command has been executed. \n]==-- Sentinel --==[" +
                        "\nPlayer: " + p.getName() +
                        "\nCommand: " + command +
                        "\nDenied: " + TextUtils.boolString(denied,"\u2714","\u2718") +
                        "\nDeoped: " + TextUtils.boolString(deoped,"\u2714","\u2718") +
                        "\nPunished: " + TextUtils.boolString(punished,"\u2714","\u2718") +
                        "\nLogged: " + TextUtils.boolString(logged,"\u2714","\u2718")
        );
        Sentinel.log.info(log);
    }
    public static void NBT(Player p, ItemStack item, boolean removed, boolean deoped, boolean gms, boolean punished, boolean logged) {
        String log = (
                        "Sentinel caught a dangerous NBT! \n]==-- Sentinel --==[" +
                                "\nPlayer: " + p.getName() +
                                "\nItemType: " + item.getType() +
                                "\nRemoved: " + TextUtils.boolString(removed,"\u2714","\u2718") +
                                "\nDeoped: " + TextUtils.boolString(deoped,"\u2714","\u2718") +
                                "\nRevert GM: " + TextUtils.boolString(gms, "\u2714","\u2718") +
                                "\nPunished: " + TextUtils.boolString(punished,"\u2714","\u2718") +
                                "\nLogged: " + TextUtils.boolString(logged,"\u2714","\u2718")
        );
        Sentinel.log.info(log);
    }
    public static void placeBlock(Player p, Block b, boolean deleted, boolean deoped, boolean punished, boolean logged) {
       String log = (
                "Sentinel has caught the placing of a dangerous block! \n]==-- Sentinel --==[" +
                        "\nPlayer: " + p.getName() +
                        "\nBlockType: " + b.getType() +
                        "\nLocation: " + b.getX() + " " + b.getY() + " " + b.getZ() +
                        "\nDeleted: " + TextUtils.boolString(deleted,"\u2714","\u2718") +
                        "\nDeoped: " + TextUtils.boolString(deoped,"\u2714","\u2718") +
                        "\nPunished: " + TextUtils.boolString(punished,"\u2714","\u2718") +
                        "\nLogged: " + TextUtils.boolString(logged,"\u2714","\u2718")
       );
        Sentinel.log.info(log);
    }
    public static void usedBlock(Player p, Block b, boolean denied, boolean deoped, boolean punished, boolean logged) {
        String log = (
                "]==-- Sentinel --==[" +
                        "\nPlayer: " + p.getName() +
                        "\nBlockType: " + b.getType() +
                        "\nLocation: " + b.getX() + " " + b.getY() + " " + b.getZ() +
                        "\nDenied: " + TextUtils.boolString(denied,"\u2714","\u2718") +
                        "\nDeoped: " + TextUtils.boolString(deoped,"\u2714","\u2718") +
                        "\nPunished: " + TextUtils.boolString(punished,"\u2714","\u2718") +
                        "\nLogged: " + TextUtils.boolString(logged,"\u2714","\u2718")
        );
        Sentinel.log.info(log);
    }
    public static void usedEntity(Player p, Entity e, boolean denied, boolean deoped, boolean punished, boolean logged) {
        String log = (
                "]==-- Sentinel --==[" +
                        "\nPlayer: " + p.getName() +
                        "\nEntityType: " + e.getType() +
                        "\nLocation: " + e.getLocation().getX() + " " + e.getLocation().getY() + " " + e.getLocation().getZ() +
                        "\nDenied: " + TextUtils.boolString(denied,"\u2714","\u2718") +
                        "\nDeoped: " + TextUtils.boolString(deoped,"\u2714","\u2718") +
                        "\nPunished: " + TextUtils.boolString(punished,"\u2714","\u2718") +
                        "\nLogged: " + TextUtils.boolString(logged,"\u2714","\u2718")
        );
        Sentinel.log.info(log);
    }
}
