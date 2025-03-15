package me.trouper.sentinel.server.functions.chatfilter.profanity;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfanityFilter {
    public static Map<UUID, Integer> scoreMap = new HashMap<>();

    public static void handleProfanityFilter(AsyncChatEvent event) {
        if (event.isCancelled()) {
            ServerUtils.verbose("Anti Profanity Opening: Event is canceled.");
        }
        Player player = event.getPlayer();
        ProfanityResponse response = new ProfanityResponse(null,null,null,null,null,false,false).generate(event);
        Severity severity = response.getSeverity();
        ServerUtils.verbose("Response came back.");
        if (severity == null) return;
        ServerUtils.verbose("Not null, its severity is " + severity);
        if (severity.equals(Severity.SAFE)) return;
        ServerUtils.verbose("Not null or safe, canceling event");
        response.setBlocked(true);

        scoreMap.putIfAbsent(player.getUniqueId(), 0);
        int previousScore = scoreMap.get(player.getUniqueId());

        int newScore = previousScore + severity.getScore();
        scoreMap.put(player.getUniqueId(), newScore);

        if (newScore > Sentinel.getInstance().getDirector().io.mainConfig.chat.profanityFilter.punishScore || Severity.SLUR.equals(severity)) {
            response.setPunished(true);
            new ProfanityAction().run(response);
            return;
        }

        new ProfanityAction().run(response);
    }

    public static void decayScore() {
        for (UUID uuid : scoreMap.keySet()) {
            int score = scoreMap.get(uuid);
            if (score > 0) {
                score = score - Sentinel.getInstance().getDirector().io.mainConfig.chat.profanityFilter.scoreDecay;
                scoreMap.put(uuid, Math.max(0, score));
            }
        }
    }

}
