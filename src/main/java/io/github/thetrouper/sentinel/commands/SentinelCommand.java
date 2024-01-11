/**
 * This file is for tutorial purposes made by ImproperIssues. Distribute if you want :)
 */

package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.server.functions.AntiSpam;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.util.ArrayUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Example command
 */
public class SentinelCommand extends CustomCommand {
    public static boolean debugmode;

    public SentinelCommand() {
        super("sentinel");
        this.setPrintStacktrace(true);
    }

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        Sentinel instance = Sentinel.getInstance();
        switch (args[0]) {
            case "reload" -> {
                if (!Sentinel.isTrusted(p)) return;
                p.sendMessage(Text.prefix("Reloading Sentinel!"));
                Sentinel.log.info("[Sentinel] Re-Initializing Sentinel!");
                instance.loadConfig();
            }
            case "debug" -> {
                switch (args[1]) {
                    case "antiswear" -> {
                        HashSet<Player> players = new HashSet<>();
                        players.add((Player) sender);
                        String msg = "";
                        for (int i = 1; i < args.length; i++) {
                            msg = msg.concat(" " + args[i]);
                        }
                        msg = msg.trim();
                        AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(true, (Player) sender, msg, players);
                        ProfanityFilter.handleProfanityFilter(e);
                    }
                    case "antispam" -> {
                        HashSet<Player> players = new HashSet<>();
                        players.add((Player) sender);
                        String msg = "";
                        for (int i = 1; i < args.length; i++) {
                            msg = msg.concat(" " + args[i]);
                        }
                        msg = msg.trim();
                        AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(true, (Player) sender, msg, players);
                        AntiSpam.handleAntiSpam(e);
                    }
                    case "lang" -> {
                        p.sendMessage(Sentinel.dict.get("exmaple-message"));
                    }
                    case "toggle" -> {
                        debugmode = !debugmode;
                        p.sendMessage(Text.prefix((debugmode ? "enabled" : "disabled") + " debug mode."));
                    }
                }
            }
            case "getHeat" -> {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    p.sendMessage(Text.prefix("Invalid Player!"));
                    return;
                }
                p.sendMessage(Text.prefix("Heat of " + target.getName() + ": &8(&c" + AntiSpam.heatMap.get(target) + "&7/&4" + Config.punishHeat + "&8)"));
            }
        }
    }

    @Override
    public void registerCompletions(CompletionBuilder builder) {
        builder.addCompletion(1, "debug",
                "getHeat",
                "reload");
        if (builder.args.length >= 2 && builder.args[1].equals("debug")) {
            builder.addCompletion(2, "antiswear",
                    "antispam",
                    "lang",
                    "toggle");
            //builder.addCompletion(2, (builder.args.length >= 1 && builder.args[1].equals("getHeat")), ArrayUtils.toNewList(Bukkit.getOnlinePlayers(), Player::getName));
        }
    }
}
