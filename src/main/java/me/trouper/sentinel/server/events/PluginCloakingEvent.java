package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PluginCloakingEvent implements CustomListener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PluginCloakingPacket.tabReplaceQueue.remove(e.getPlayer());
    }


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!Sentinel.mainConfig.plugin.pluginHider) return;
        Player p = e.getPlayer();
        if (PlayerUtils.isTrusted(p)) return;

        String message = e.getMessage();

        if (message.startsWith("/")) {
            message = message.substring(1);
        }

        for (String alias : Sentinel.advConfig.commandsWithPluginAccess) {
            if (!message.startsWith(alias)) continue;
            e.setCancelled(true);
            p.sendMessage(Text.color(Sentinel.lang.permissions.noPlugins));
        }
    }

    @EventHandler
    public void onTabComplete(PlayerCommandSendEvent e) {
        if (!Sentinel.mainConfig.plugin.pluginHider) return;
        Player p = e.getPlayer();
        if (PlayerUtils.isTrusted(p)) return;

        List<String> commands = e.getCommands().stream().toList();
        for (String command : commands) {
            if (command.contains(":")) {
                e.getCommands().remove(command);
                continue;
            }
            if (Sentinel.advConfig.commandsWithPluginAccess.contains(command)) {
                e.getCommands().remove(command);
                continue;
            }
        }
        ServerUtils.verbose("Removed all the plugin specific commands form the listing. It now contains %s".formatted(e.getCommands().stream().toList().toString()));
        for (String fakePlugin : Sentinel.advConfig.fakePlugins) {
            e.getCommands().add(fakePlugin + ":" + fakePlugin);
        }
        ServerUtils.verbose("Added the fake plugins, now it contains this: %s".formatted(e.getCommands().stream().toList().toString()));
    }
}
