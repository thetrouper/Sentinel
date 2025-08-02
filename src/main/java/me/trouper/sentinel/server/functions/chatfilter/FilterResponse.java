package me.trouper.sentinel.server.functions.chatfilter;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.server.Main;
import me.trouper.sentinel.server.functions.helpers.Report;
import org.bukkit.entity.Player;

public interface FilterResponse extends Main {
    AsyncChatEvent getEvent();
    Player getPlayer();
    Report getReport();
    boolean isBlocked();
    boolean isPunished();
}