package io.github.thetrouper.sentinel.cmds;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.functions.SystemCheck;
import io.github.thetrouper.sentinel.server.functions.Telemetry;
import io.github.thetrouper.sentinel.server.util.CipherUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
@CommandRegistry(value = "sentinel",permission = @Permission("sentinel.debug"),printStackTrace = true)
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
            case "full-system-check" -> {
                p.sendMessage(Text.prefix("Initiating a full system check!"));
                SystemCheck.fullCheck(p);
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
                        p.sendMessage(Sentinel.language.get("exmaple-message"));
                    }
                    case "toggle" -> {
                        debugMode = !debugMode;
                        p.sendMessage(Text.prefix((debugMode ? "Enabled" : "Disabled") + " debug mode."));
                    }
                    case "encrypt" -> {
                        final String enc = CipherUtils.encrypt(args.getAll(2).toString());
                        final String check = CipherUtils.decrypt(enc);
                        final String main = Text.prefix("Successfully encrypted \"&e" + check + "&7\" using AES.\n &7> &b" + enc);
                        Sentinel.log.info(args.getAll(2).toString() + "\n" + enc + "\n" + check);
                        TextComponent message = new TextComponent();
                        message.setText(main);
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text("&bClick to copy!")));
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, enc));
                        p.spigot().sendMessage(message);
                    }
                }
            }
            case "getHeat" -> {
                Player target = Bukkit.getPlayer(args.get(1).toString());
                if (target == null) {
                    p.sendMessage(Text.prefix("Invalid Player!"));
                    return;
                }
                p.sendMessage(Text.prefix("Heat of " + target.getName() + ": &8(&c" + io.github.thetrouper.sentinel.server.functions.AntiSpam.heatMap.get(target) + "&7/&4" + Sentinel.mainConfig.chat.antiSpam.punishHeat + "&8)"));
            }
        }
    }

    @Override
    public void dispatchCompletions(CompletionBuilder b) {
        b.then(b.arg("reload","getheat","full-system-check"));
        b.then(b.arg("debug").then(
                b.arg("antiswear","antispam","lang","toggle")));
    }
}
