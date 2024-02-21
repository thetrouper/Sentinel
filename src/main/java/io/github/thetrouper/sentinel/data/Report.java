package io.github.thetrouper.sentinel.data;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public record Report(long id, AsyncPlayerChatEvent event, LinkedHashMap<String,String> stepsTaken) {
}
