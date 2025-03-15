package me.trouper.sentinel.server.events.violations.players;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.chat.Node;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
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

public class PluginCloakingPacket extends PacketListenerAbstract {

    public static final List<UUID> tabReplaceQueue = new ArrayList<>();

    public PluginCloakingPacket() {
        super(PacketListenerPriority.NORMAL);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!Sentinel.getInstance().getDirector().io.mainConfig.plugin.pluginHider) return;
        if (event.getPacketType() != PacketType.Play.Client.TAB_COMPLETE) return;

        WrapperPlayClientTabComplete wrapper = new WrapperPlayClientTabComplete(event);
        Player player = (Player) event.getPlayer();
        if (player == null) return;
        if (PlayerUtils.isTrusted(player)) return;

        String text = wrapper.getText();
        for (String versionAlias : Sentinel.getInstance().getDirector().io.advConfig.pluginTabCompletions) {
            if (!text.contains(versionAlias)) continue;
            ServerUtils.verbose("Caught a version command tab completion. (%s -> %s)".formatted(text,versionAlias));
            tabReplaceQueue.add(player.getUniqueId());
            break;
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
