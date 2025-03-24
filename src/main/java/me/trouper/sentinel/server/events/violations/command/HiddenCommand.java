package me.trouper.sentinel.server.events.violations.command;

import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.server.events.violations.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.utils.trees.Node;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HiddenCommand extends AbstractViolation {
    // Track recent canceled messages per player
    private static final Map<UUID, List<String>> canceledMessages = new ConcurrentHashMap<>();
    private static final int THRESHOLD = 3; // Minimum messages to trigger detection
    private static final int CHECK_LENGTH = 2; // Check first N characters for similarity

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncChatEvent event) {
        if (!event.isCancelled()) return;

        Player player = event.getPlayer();
        String message = LegacyComponentSerializer.legacySection().serialize(event.message());
        UUID uuid = player.getUniqueId();

        // Add message to player's history
        canceledMessages.compute(uuid, (k, v) -> {
            if (v == null) v = new ArrayList<>();
            v.add(message);
            return v;
        });

        // Check if threshold is met
        List<String> messages = canceledMessages.get(uuid);
        if (messages.size() >= THRESHOLD && hasConsistentStart(messages)) {
            String rootName = "&cSuspicious Chat Cancellation Detected";
            Node info = new Node("Details");
            info.addKeyValue("Pattern", messages.get(0).substring(0, CHECK_LENGTH) + "*");
            info.addKeyValue("Count", String.valueOf(messages.size()));

            // Trigger action
            runActions(
                    rootName,
                    "Chat Backdoor Detection",
                    info,
                    new ActionConfiguration.Builder()
                            .setPlayer(player)
                            .logToDiscord(true)
            );

            // Reset tracking
            canceledMessages.remove(uuid);
        }
    }

    private boolean hasConsistentStart(List<String> messages) {
        if (messages.size() < THRESHOLD) return false;
        String prefix = messages.get(0).substring(0, Math.min(CHECK_LENGTH, messages.get(0).length()));
        return messages.stream()
                .allMatch(msg -> msg.startsWith(prefix));
    }

    @Override
    public CustomGui getConfigGui() {
        return null;
    }

    @Override
    public void getMainPage(Inventory inv) {

    }

    @Override
    public void onClick(InventoryClickEvent e) {

    }
}
