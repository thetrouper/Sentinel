package me.trouper.sentinel.server.functions.chatfilter;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;

public interface FilterResponse {
    AsyncChatEvent getEvent();
    Player getPlayer();
    Report getReport();
    boolean isBlocked();
    boolean isPunished();
}