package io.github.thetrouper.sentinel.server.functions;

import io.github.itzispyder.pdk.utils.ServerUtils;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.cmds.SocialSpyCommand;
import io.github.thetrouper.sentinel.server.config.MainConfig;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class Message {
    public static final Map<UUID,UUID> replyMap = new HashMap<>();
    public static void messagePlayer(Player sender, Player receiver, String message) {
        HashSet<Player> receivers = new HashSet<>();
        receivers.add(receiver);
        receivers.add(sender);
        AsyncPlayerChatEvent checkEvent = new AsyncPlayerChatEvent(true,sender,message,receivers);
        if (checkEvent.isCancelled()) return;
        if (!Sentinel.isTrusted(sender) || !sender.hasPermission("sentinel.chat.antiswear.bypass")) if (Sentinel.mainConfig.chat.antiSwear.antiSwearEnabled) ProfanityFilter.handleProfanityFilter(checkEvent);
        if (!Sentinel.isTrusted(sender) || !sender.hasPermission("sentinel.chat.antispam.bypass")) if (Sentinel.mainConfig.chat.antiSpam.antiSpamEnabled) AntiSpam.handleAntiSpam(checkEvent);
        if (!Sentinel.isTrusted(sender) || !sender.hasPermission("sentinel.chat.antiunicode.bypass")) if (Sentinel.mainConfig.chat.antiUnicode) AntiUnicode.handleAntiUnicode(checkEvent);
        if (checkEvent.isCancelled()) return;

        sender.sendMessage(Sentinel.dict.get("message-sent").formatted(receiver.getName(),message));
        receiver.sendMessage(Sentinel.dict.get("message-received").formatted(sender.getName(),message));
        replyMap.put(receiver.getUniqueId(),sender.getUniqueId());
        sendSpy(sender,receiver,message);
    }

    public static void sendSpy(Player sender, Player receiver, String message) {
        ServerUtils.forEachPlayer(player -> {
            if (SocialSpyCommand.spyMap.getOrDefault(player.getUniqueId(),false)) {
                TextComponent notification = new TextComponent(Sentinel.dict.get("spy-message").formatted(sender.getName(),receiver.getName()));
                notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Sentinel.dict.get("spy-message-hover").formatted(sender.getName(),receiver.getName(),message))));
                player.spigot().sendMessage(notification);
            }
        });
    }
}
