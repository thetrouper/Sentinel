package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.commands.MessageCommand;
import io.github.thetrouper.sentinel.commands.SocialSpyCommand;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class Message {
    private static Map<UUID,UUID> replyMap = MessageCommand.replyMap;
    public static void messagePlayer(Player sender, Player receiver, String message) {
        HashSet<Player> receivers = new HashSet<>();
        receivers.add(receiver);
        receivers.add(sender);
        AsyncPlayerChatEvent checkEvent = new AsyncPlayerChatEvent(true,sender,message,receivers);
        if (checkEvent.isCancelled()) return;
        if (!Sentinel.isTrusted(sender) || !sender.hasPermission("sentinel.chat.antiswear.bypass")) if (Config.antiSwearEnabled) ProfanityFilter.handleProfanityFilter(checkEvent);
        if (!Sentinel.isTrusted(sender) || !sender.hasPermission("sentinel.chat.antispam.bypass")) if (Config.antiSpamEnabled) AntiSpam.handleAntiSpam(checkEvent);
        if (!Sentinel.isTrusted(sender) || !sender.hasPermission("sentinel.chat.antiunicode.bypass")) if (Config.antiUnicode) AntiUnicode.handleAntiUnicode(checkEvent);
        if (checkEvent.isCancelled()) {
            return;
        }

        sender.sendMessage("§d§lMessage §8» §b[§fYou §e>§f " + receiver.getName() + "§b] §7" + message);
        receiver.sendMessage("§d§lMessage §8» §b[§f" + sender.getName() + " §e>§f You§b] §7" + message);
        replyMap.put(receiver.getUniqueId(),sender.getUniqueId());
        sendSpy(sender,receiver,message);
    }

    public static void sendSpy(Player sender, Player receiver, String message) {
        ServerUtils.forEachPlayer(player -> {
            if (SocialSpyCommand.spyMap.getOrDefault(player.getUniqueId(),false)) {
                TextComponent notification = new TextComponent("§d§lSpy §8» §b§n" + sender.getName() + "§7 has messaged §b§n " + receiver.getName() + "§7.");
                notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                        "§8]==-- §d§lSocialSpy §8--==[" +
                                "\n§bSender: §f" + sender.getName() +
                                "\n§bReceiver: §f" + receiver.getName() +
                                "\n§bMessage: §f" + message
                )));
                player.spigot().sendMessage(notification);
            }
        });
    }
}
