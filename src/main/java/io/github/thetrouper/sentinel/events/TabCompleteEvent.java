package io.github.thetrouper.sentinel.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.plugin.Plugin;

public class TabCompleteEvent {
    public static final String[] VERSION_ALIASES = Sentinel.advConfig.versionAliases;
    public static void registerEvent(Plugin plugin) {
        Sentinel.protocolManager.addPacketListener(new PacketAdapter(
                plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.TAB_COMPLETE
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() != PacketType.Play.Client.TAB_COMPLETE) return;
                String input = event.getPacket().getStrings().read(0);
                input = input.replaceFirst("/","");
                if (input.length() < 2) {
                    String modifiedInput = input.replaceFirst(input, "sentineltab");
                    event.getPacket().getStrings().write(0, modifiedInput);
                    ServerUtils.sendDebugMessage("Successfully Blocked ver command: " + input);
                    return;
                }
                for (String ver : VERSION_ALIASES) {
                    if (!input.startsWith(ver + " ")) continue;
                    String modifiedInput = input.replaceFirst(ver, "sentineltab");
                    event.getPacket().getStrings().write(0, modifiedInput);
                    ServerUtils.sendDebugMessage("Successfully Blocked ver command: " + input);
                    return;
                }
            }
        });
    }

}
