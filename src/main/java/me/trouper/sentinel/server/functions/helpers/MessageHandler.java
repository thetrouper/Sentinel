package me.trouper.sentinel.server.functions.helpers;

import io.github.itzispyder.pdk.utils.ServerUtils;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.Main;
import me.trouper.sentinel.server.commands.SentinelCommand;
import me.trouper.sentinel.server.events.violations.players.ChatEvent;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import java.util.*;

public class MessageHandler implements Main {
    public final Map<UUID,UUID> replyMap = new HashMap<>();
    public void messagePlayer(Player sender, Player receiver, String message) {
        AsyncChatEvent checkEvent = new AsyncChatEvent(true,sender, new HashSet<>(Arrays.asList(receiver, sender)), ChatRenderer.defaultRenderer(),Component.text(message),Component.text(message), SignedMessage.system(message,Component.text(message)));
        if (checkEvent.isCancelled()) return;
        new ChatEvent().handleEvent(checkEvent);
        if (checkEvent.isCancelled()) return;

        sender.sendMessage(main.dir().io.lang.playerInteraction.messageSent.formatted(receiver.getName(),message)); // This "sendMessage" call is correct
        receiver.sendMessage(main.dir().io.lang.playerInteraction.messageReceived.formatted(sender.getName(),message)); // This "sendMessage" call is correct
        replyMap.put(receiver.getUniqueId(),sender.getUniqueId());
        sendSpy(sender,receiver,message);
    }

    public void sendSpy(Player sender, Player receiver, String message) {
        ServerUtils.forEachPlayer(player -> {

            if (SentinelCommand.spyMap.getOrDefault(player.getUniqueId(),false)) {
                TextComponent notification = Component
                        .text(main.dir().io.lang.socialSpy.spyMessage.formatted(sender.getName(),receiver.getName()))
                        .hoverEvent(Component.text(main.dir().io.lang.socialSpy.spyMessageHover.formatted(sender.getName(),receiver.getName(),message)));
                player.sendMessage(notification); // This "sendMessage" call is correct
            }
        });
    }
}
