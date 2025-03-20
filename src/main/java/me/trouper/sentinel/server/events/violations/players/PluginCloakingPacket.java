package me.trouper.sentinel.server.events.violations.players;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.chat.Node;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommandUnsigned;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDeclareCommands;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTabComplete;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PluginCloakingPacket implements PacketListener {

    public static final ConcurrentLinkedQueue<UUID> tabReplaceQueue = new ConcurrentLinkedQueue<>();    
    
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!Sentinel.getInstance().getDirector().io.mainConfig.plugin.pluginHider) return;
        switch (event.getPacketType()) {
            case PacketType.Play.Client.TAB_COMPLETE -> {
                WrapperPlayClientTabComplete wrapper = new WrapperPlayClientTabComplete(event);
                Player player = (Player) event.getPlayer();
                if (player == null) return;
                if (PlayerUtils.isTrusted(player)) return;

                String text = wrapper.getText();
                if (text.startsWith("/")) text = text.substring(1);
                text = text.split(" ")[0];

                List<String> intendedCommands = Sentinel.getInstance().getDirector().io.advConfig.intendedCommands;
                List<String> pluginTabCompletions = Sentinel.getInstance().getDirector().io.advConfig.pluginTabCompletions;

                if (Sentinel.getInstance().getDirector().io.advConfig.pluginCloakingWhitelist) {
                    boolean whitelisted = false;
                    for (String pattern : intendedCommands) {
                        if (text.matches(pattern)) {
                            whitelisted = true;
                            break;
                        }
                    }
                    if (!whitelisted) {
                        ServerUtils.verbose("Caught a non-whitelisted tab completion. (%s)".formatted(text));
                        tabReplaceQueue.add(player.getUniqueId());
                    }
                }

                for (String pattern : pluginTabCompletions) {
                    if (text.matches(pattern)) {
                        ServerUtils.verbose("Caught a plugin listing command tab completion. (%s -> %s)".formatted(text, pattern));
                        tabReplaceQueue.add(player.getUniqueId());
                        break;
                    }
                }
            }
            case PacketType.Play.Client.CHAT_COMMAND, PacketType.Play.Client.CHAT_COMMAND_UNSIGNED -> {
                WrapperPlayClientChatCommandUnsigned wrapper = new WrapperPlayClientChatCommandUnsigned(event);
                WrapperPlayClientChatCommand wrappers = new WrapperPlayClientChatCommand(event);
                wrapper.getCommand();
                wrappers.getCommand();
            }
            default -> {}
        }
    }




    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!Sentinel.getInstance().getDirector().io.mainConfig.plugin.pluginHider) return;

        Player player = (Player) event.getPlayer();
        if (player == null) return;
        if (PlayerUtils.isTrusted(player)) return;

        switch (event.getPacketType()) {
            case PacketType.Play.Server.TAB_COMPLETE -> {
                if (tabReplaceQueue.contains(player.getUniqueId())) {
                    tabReplaceQueue.remove(player.getUniqueId());
                    ServerUtils.verbose("Player was queued for replacement, setting tab completions.");
                    WrapperPlayServerTabComplete wrapper = new WrapperPlayServerTabComplete(event);
                    List<WrapperPlayServerTabComplete.CommandMatch> matches = new ArrayList<>();
                    for (String fakePlugin : Sentinel.getInstance().getDirector().io.advConfig.fakePlugins) {
                        matches.add(new WrapperPlayServerTabComplete.CommandMatch(fakePlugin));
                    }
                    wrapper.setCommandMatches(matches);
                }
            }
            case PacketType.Play.Server.DECLARE_COMMANDS -> {
                WrapperPlayServerDeclareCommands wrapper = new WrapperPlayServerDeclareCommands(event);
                List<Node> nodes = wrapper.getNodes();
                for (Node node : nodes) {
                    if (node.getName().isPresent() && node.getName().get().contains(":")) node.setName(Optional.of("sentineltab"));
                }
                wrapper.setNodes(nodes);
            }
            default -> {}
        }
    }
}
