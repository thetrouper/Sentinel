package io.github.thetrouper.sentinel.server.functions;

import io.github.itzispyder.pdk.utils.ServerUtils;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.cmds.SocialSpyCommand;
import io.github.thetrouper.sentinel.events.ChatEvent;
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
        ChatEvent.handleChatEvent(checkEvent);
        if (checkEvent.isCancelled()) return;

        sender.sendMessage(Sentinel.language.get("message-sent").formatted(receiver.getName(),message));
        receiver.sendMessage(Sentinel.language.get("message-received").formatted(sender.getName(),message));
        replyMap.put(receiver.getUniqueId(),sender.getUniqueId());
        sendSpy(sender,receiver,message);
    }

    public static void sendSpy(Player sender, Player receiver, String message) {
        ServerUtils.forEachPlayer(player -> {
            if (SocialSpyCommand.spyMap.getOrDefault(player.getUniqueId(),false)) {
                TextComponent notification = new TextComponent(Sentinel.language.get("spy-message").formatted(sender.getName(),receiver.getName()));
                notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Sentinel.language.get("spy-message-hover").formatted(sender.getName(),receiver.getName(),message))));
                player.spigot().sendMessage(notification);
            }
        });
    }
}
