package me.trouper.sentinel.server.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class PluginCloakingEvents implements CustomListener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!Sentinel.doNoPlugins) return;
        Player p = e.getPlayer();
        if (PlayerUtils.isTrusted(p)) return;

        String message = e.getMessage();

        if (message.startsWith("/")) {
            message = message.substring(1);
        }

        for (String alias : Sentinel.advConfig.versionAliases) {
            if (!message.startsWith(alias)) continue;
            e.setCancelled(true);
            p.sendMessage(Text.color(Sentinel.lang.permissions.noPlugins));
        }
    }

    @EventHandler
    public void onTabComplete(PlayerCommandSendEvent e) {
        if (!Sentinel.doNoPlugins) return;
        Player p = e.getPlayer();
        if (PlayerUtils.isTrusted(p)) return;

        List<String> commands = e.getCommands().stream().toList();
        for (String command : commands) {
            if (command.contains(":")) {
                e.getCommands().remove(command);
                continue;
            }
            if (Sentinel.advConfig.versionAliases.contains(command)) {
                e.getCommands().remove(command);
                continue;
            }
        }
        //ServerUtils.verbose("Removed all the plugin specific commands form the listing. It now contains %s".formatted(e.getCommands().stream().toList().toString()));
        for (String fakePlugin : Sentinel.advConfig.fakePlugins) {
            e.getCommands().add(fakePlugin + ":" + fakePlugin);
        }
        //ServerUtils.verbose("Added the fake plugins, now it contains this: %s".formatted(e.getCommands().stream().toList().toString()));
    }

    public static void registerEvent(Plugin plugin) {
        Sentinel.protocolManager.addPacketListener(new PacketAdapter(
                plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.TAB_COMPLETE
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (!Sentinel.doNoPlugins) return;
                if (PlayerUtils.isTrusted(event.getPlayer())) return;
                ServerUtils.verbose("Type: §b%s§7 Index 0: §b%s§7".formatted(
                        event.getPacket().getType(),
                        event.getPacket().getStrings().read(0)
                ));

                String command = event.getPacket().getStrings().read(0);
                if (command.startsWith("/")) command = command.replaceFirst("/","");
                ServerUtils.verbose("Command is " + command);
                for (String alias : Sentinel.advConfig.versionAliases) {
                    alias = alias.trim();
                    command = command.trim();
                    if (alias.equals(command) || command.startsWith(alias)) {
                        ServerUtils.verbose("Caught a command we can replace: %s".formatted(command));
                        event.getPacket().getStrings().write(0,"/sentineltab ");
                        return;
                    }
                }
            }
        });
    }


    /*
    Sentinel.protocolManager.addPacketListener(new PacketAdapter(
                plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.COMMANDS
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (PlayerUtils.isTrusted(event.getPlayer())) return;
                ServerUtils.verbose("Index 0: §b%s§7".formatted(
                        event.getPacket().getStrings().read(0)
                ));
                event.setCancelled(true);
            }
        });
     */


    /*
    String input = event.getPacket().getStrings().read(0);
                if (input == null || input.isEmpty()) return;

                input = input.replaceFirst("/", "");
                if (input.length() < 2) {
                    event.getPacket().getStrings().write(0, "sentineltab");
                    ServerUtils.verbose("Successfully Blocked ver command: " + input);
                    return;
                }

                for (String ver : Sentinel.advConfig.versionAliases) {
                    if (!input.startsWith(ver + " ")) continue;
                    String modifiedInput = input.replaceFirst(ver, "sentineltab");
                    event.getPacket().getStrings().write(0, modifiedInput);
                    ServerUtils.verbose("Successfully Blocked ver command: " + input);
                    return;
                }
     */
}
