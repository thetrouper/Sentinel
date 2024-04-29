package io.github.thetrouper.sentinel.data;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.LinkedHashMap;

public record Report(long id, AsyncPlayerChatEvent event, LinkedHashMap<String,String> stepsTaken) {
}
