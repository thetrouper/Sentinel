package me.trouper.sentinel.server.functions.chatfilter.spam;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpamFilter {
    public static Map<UUID, Integer> heatMap = new HashMap<>();
    public static Map<UUID, String> lastMessageMap = new HashMap<>();

    public static void handleSpamFilter(AsyncChatEvent e) {
        if (e.isCancelled()) {
            ServerUtils.verbose("Anti Spam Opening: Event is canceled.");
        }
        Player p = e.getPlayer();
        String message = Text.removeFirstColor(LegacyComponentSerializer.legacySection().serialize(e.message()));
        for (String whitelistedMessage : Sentinel.mainConfig.chat.spamFilter.whitelist) {
            if (whitelistedMessage.equalsIgnoreCase(message)) return;
        }
        int currentHeat = heatMap.getOrDefault(p.getUniqueId(),0);

        SpamResponse response = SpamResponse.generate(e);
        lastMessageMap.put(e.getPlayer().getUniqueId(), message);

        int addHeat = response.getHeatAdded();

        ServerUtils.verbose("AntiSpam responded");
        response.getReport().getStepsTaken().put("Response came back", "Heat to add: %s".formatted(addHeat));

        if (currentHeat > Sentinel.mainConfig.chat.spamFilter.punishHeat) {
            response.setBlocked(true);
            response.getReport().getStepsTaken().put("Punished user", "Their final heat was %s".formatted(currentHeat));
            response.setPunished(true);
            new SpamAction().run(response);
            heatMap.put(p.getUniqueId(), currentHeat + addHeat);
            return;
        }

        if (currentHeat > Sentinel.mainConfig.chat.spamFilter.blockHeat) {
            response.setBlocked(true);
            response.getReport().getStepsTaken().put("Blocked message", "Their heat is %s".formatted(currentHeat));
            new SpamAction().run(response);
            heatMap.put(p.getUniqueId(), currentHeat + addHeat);
            return;
        }

        if (response.getSimilarity() > Sentinel.mainConfig.chat.spamFilter.blockSimilarity) {
            response.setBlocked(true);
            response.getReport().getStepsTaken().put("Blocked message", "The similarity was too high! %s".formatted(response.getSimilarity()));
            new SpamAction().run(response);
            heatMap.put(p.getUniqueId(), currentHeat + addHeat);
            return;
        }

        if (e.isCancelled()) {
            ServerUtils.verbose("Anti spam closing: Event is canceled.");
        }
        heatMap.put(p.getUniqueId(),currentHeat + addHeat);
    }

    public static void decayHeat() {
        for (UUID p : heatMap.keySet()) {
            int heat = heatMap.getOrDefault(p,0);
            if (heat > 0) {
                heat = heat - Sentinel.mainConfig.chat.spamFilter.heatDecay;
                heatMap.put(p, Math.max(0, heat));
            }
        }
    }
}
