package io.github.thetrouper.sentinel.cmds;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.config.MainConfig;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
@CommandRegistry(value = "sentinel",permission = @Permission("sentinel.debug"))
public class SentinelCommand implements CustomCommand {
    public static boolean debugMode;
    @Override
    public void dispatchCommand(CommandSender commandSender, Args args) {
        Player p = (Player) commandSender;
        Sentinel instance = Sentinel.getInstance();
        switch (args.get(0).toString()) {
            case "reload" -> {
                if (!Sentinel.isTrusted(p)) return;
                p.sendMessage(Text.prefix("Reloading Sentinel!"));
                Sentinel.log.info("[Sentinel] Re-Initializing Sentinel!");
                instance.loadConfig();
            }
            case "debug" -> {
                switch (args.get(1).toString()) {
                    case "antiswear" -> {
                        HashSet<Player> players = new HashSet<>();
                        players.add(p);
                        String msg = args.getAll(1).toString().trim();
                        AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(true, p, msg, players);
                        ProfanityFilter.handleProfanityFilter(e);
                    }
                    case "antispam" -> {
                        HashSet<Player> players = new HashSet<>();
                        players.add(p);
                        String msg = args.getAll(1).toString().trim();
                        AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(true, p, msg, players);
                        io.github.thetrouper.sentinel.server.functions.AntiSpam.handleAntiSpam(e);
                    }
                    case "lang" -> {
                        p.sendMessage(Sentinel.dict.get("exmaple-message"));
                    }
                    case "toggle" -> {
                        debugMode = !debugMode;
                        p.sendMessage(Text.prefix((debugMode ? "Enabled" : "Disabled") + " debug mode."));
                    }
                }
            }
            case "getHeat" -> {
                Player target = Bukkit.getPlayer(args.get(1).toString());
                if (target == null) {
                    p.sendMessage(Text.prefix("Invalid Player!"));
                    return;
                }
                p.sendMessage(Text.prefix("Heat of " + target.getName() + ": &8(&c" + io.github.thetrouper.sentinel.server.functions.AntiSpam.heatMap.get(target) + "&7/&4" + MainConfig.Chat.AntiSpam.punishHeat + "&8)"));
            }
        }
    }

    @Override
    public void dispatchCompletions(CompletionBuilder b) {
        b.then(b.arg("reload","getheat"));
        b.then(b.arg("debug").then(
                b.arg("antiswear","antispam","lang","toggle")));
    }
}
