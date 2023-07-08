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
    public static void command(Player p, String command, boolean denied, boolean deoped, boolean banned, boolean logged) {
        final String log = (
                        "Sentinel caught a dangerous command! \n]==-- &d&lSentinel --==[" +
                                "\nPlayer: " + p.getName() +
                                "\nCommand: " + command +
                                "\nDenied: " + TextUtils.boolString(denied,"✔","✘") +
                                "\nDeoped: " + TextUtils.boolString(deoped,"✔","✘") +
                                "\nBanned: " + TextUtils.boolString(banned,"✔","✘") +
                                "\nLogged: " + TextUtils.boolString(logged,"✔","✘")
                );
        Sentinel.log.info(log);
    }
    public static void NBT(Player p, ItemStack item, boolean removed, boolean deoped, boolean gms, boolean banned, boolean logged) {
        String log = (
                        "Sentinel caught a dangerous NBT! \n]==-- &d&lSentinel --==[" +
                                "\nPlayer: " + p.getName() +
                                "\nItemType: " + item.getType() +
                                "\nRemoved: " + TextUtils.boolString(removed,"✔","✘") +
                                "\nDeoped: " + TextUtils.boolString(deoped,"✔","✘") +
                                "\nRevert GM: " + TextUtils.boolString(gms, "✔", "✘") +
                                "\nBanned: " + TextUtils.boolString(banned,"✔","✘") +
                                "\nLogged: " + TextUtils.boolString(logged,"✔","✘")
        );
        Sentinel.log.info(log);
    }
    public static void placeBlock(Player p, Block b, boolean deleted, boolean deoped, boolean banned, boolean logged) {
       String log = (
                "Sentinel has caught the placing of a dangerous block! \n]==-- &d&lSentinel --==[" +
                        "\nPlayer: " + p.getName() +
                        "\nBlockType: " + b.getType() +
                        "\nLocation: " + b.getX() + " " + b.getY() + " " + b.getZ() +
                        "\nDeleted: " + TextUtils.boolString(deleted,"✔","✘") +
                        "\nDeoped: " + TextUtils.boolString(deoped,"✔","✘") +
                        "\nBanned: " + TextUtils.boolString(banned,"✔","✘") +
                        "\nLogged: " + TextUtils.boolString(logged,"✔","✘")
       );
        Sentinel.log.info(log);
    }
    public static void usedBlock(Player p, Block b, boolean denied, boolean deoped, boolean banned, boolean logged) {
        String log = (
                "]==-- &d&lSentinel --==[" +
                        "\nPlayer: " + p.getName() +
                        "\nBlockType: " + b.getType() +
                        "\nLocation: " + b.getX() + " " + b.getY() + " " + b.getZ() +
                        "\nDenied: " + TextUtils.boolString(denied,"✔","✘") +
                        "\nDeoped: " + TextUtils.boolString(deoped,"✔","✘") +
                        "\nBanned: " + TextUtils.boolString(banned,"✔","✘") +
                        "\nLogged: " + TextUtils.boolString(logged,"✔","✘")
        );
        Sentinel.log.info(log);
    }
    public static void usedEntity(Player p, Entity e, boolean denied, boolean deoped, boolean banned, boolean logged) {
        String log = (
                "]==-- &d&lSentinel --==[" +
                        "\nPlayer: " + p.getName() +
                        "\nEntityType: " + e.getType() +
                        "\nLocation: " + e.getLocation().getX() + " " + e.getLocation().getY() + " " + e.getLocation().getZ() +
                        "\nDenied: " + TextUtils.boolString(denied,"✔","✘") +
                        "\nDeoped: " + TextUtils.boolString(deoped,"✔","✘") +
                        "\nBanned: " + TextUtils.boolString(banned,"✔","✘") +
                        "\nLogged: " + TextUtils.boolString(logged,"✔","✘")
        );
        Sentinel.log.info(log);
    }
}
