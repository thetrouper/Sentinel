package io.github.thetrouper.sentinel.server.functions;

import io.github.itzispyder.pdk.utils.ServerUtils;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.cmds.SocialSpyCommand;
import io.github.thetrouper.sentinel.events.ChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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

        sender.sendMessage(Sentinel.lang.playerInteraction.messageSent.formatted(receiver.getName(),message));
        receiver.sendMessage(Sentinel.lang.playerInteraction.messageReceived.formatted(sender.getName(),message));
        replyMap.put(receiver.getUniqueId(),sender.getUniqueId());
        sendSpy(sender,receiver,message);
    }

    public static void sendSpy(Player sender, Player receiver, String message) {
        ServerUtils.forEachPlayer(player -> {

            if (SocialSpyCommand.spyMap.getOrDefault(player.getUniqueId(),false)) {
                TextComponent notification = Component
                        .text(Sentinel.lang.socialSpy.spyMessage.formatted(sender.getName(),receiver.getName()))
                        .hoverEvent(Component.text(Sentinel.lang.socialSpy.spyMessageHover.formatted(sender.getName(),receiver.getName(),message)));
                player.sendMessage(notification);
            }
        });
    }
}
