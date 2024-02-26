package io.github.thetrouper.sentinel.cmds;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.cmdblocks.WhitelistedBlock;
import io.github.thetrouper.sentinel.server.functions.*;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@CommandRegistry(value = "sentinel",permission = @Permission("sentinel.staff"),printStackTrace = true)
public class SentinelCommand implements CustomCommand {
    public static boolean debugMode;
    @Override
    public void dispatchCommand(CommandSender commandSender, Args args) {
        Player p = (Player) commandSender;
        Sentinel instance = Sentinel.getInstance();
        switch (args.get(0).toString()) {
            case "commandblock", "cb" -> handleCommandBlock(p,args);
            case "reload" -> {
                if (!Sentinel.isTrusted(p)) {
                    p.sendMessage(Sentinel.lang.permissions.noTrust);
                    return;
                }
                p.sendMessage(Text.prefix("Reloading Sentinel!"));
                Sentinel.log.info("[Sentinel] Re-Initializing Sentinel!");
                instance.loadConfig();
            }
            case "full-system-check" -> {
                if (!Sentinel.isTrusted(p)) {
                    p.sendMessage(Sentinel.lang.permissions.noTrust);
                    return;
                }
                p.sendMessage(Text.prefix("Initiating a full system check!"));
                SystemCheck.fullCheck(p);
            }
            case "debug" -> handleDebugCommand(p,args);
            case "false-positive" -> {
                handleFalsePositive(p,args);
            }
        }
    }
    private void handleFalsePositive(Player p, Args args) {
        String falsePositive = args.getAll(2).toString();
        if (!p.hasPermission("sentinel.chat.antiswear.edit")) {
            p.sendMessage(Sentinel.lang.permissions.noPermission);
            return;
        }
        switch (args.get(1).toString()) {
            case "add" -> {
                Sentinel.fpConfig.swearWhitelist.add(falsePositive);
                p.sendMessage(Text.prefix("&7Successfully added &a%s&7 to the false positive list!".formatted(falsePositive)));
            }
            case "remove" -> {
                Sentinel.fpConfig.swearWhitelist.remove(falsePositive);
                p.sendMessage(Text.prefix("&7Successfully removed &c%s&7 from the false positive list!".formatted(falsePositive)));
            }
        }
        Sentinel.fpConfig.save();
    }
    private void handleCommandBlock(Player p, Args args) {
        if (!Sentinel.isTrusted(p)) {
            p.sendMessage(Sentinel.lang.permissions.noTrust);
            return;
        }
        Block target = p.getTargetBlock(Set.of(Material.AIR),10);
        switch (args.get(1).toString()) {
            case "add" -> {
                if (target.getType().equals(Material.COMMAND_BLOCK) || target.getType().equals(Material.REPEATING_COMMAND_BLOCK) || target.getType().equals(Material.CHAIN_COMMAND_BLOCK)) {
                    CommandBlock cb = (CommandBlock) target.getState();
                    CMDBlockWhitelist.add(cb,p.getUniqueId());
                    p.sendMessage(Text.prefix("Successfully whitelisted a &b" + Text.cleanName(cb.getType().toString()) + "&7 with the command &a" + cb.getCommand() + "&7."));
                    return;
                }
                p.sendMessage(Text.prefix("Could not whitelist the &b" + Text.cleanName(target.getType().toString()) + "&7 it is not a command block!"));
            }
            case "remove" -> {
                WhitelistedBlock wb = CMDBlockWhitelist.get(target.getLocation());
                if (wb != null) {
                    CMDBlockWhitelist.remove(target.getLocation());
                    p.sendMessage(Text.prefix("Successfully removed 1 &b" + Text.cleanName(WhitelistedBlock.fromSerialized(wb.loc()).getBlock().getType().toString()) + "&7 with the command &a" + wb.command() + "&7."));
                    return;
                }
                p.sendMessage(Text.prefix("Could not un-whitelist the &b" + Text.cleanName(target.getType().toString()) + "&7 it wasn't whitelisted in the first place!"));
            }
        }
    }

    private void handleDebugCommand(Player p, Args args) {
        if (!p.hasPermission("sentinel.debug")) {
            p.sendMessage(Sentinel.lang.permissions.noPermission);
            return;
        }
        switch (args.get(1).toString()) {
            case "lang" -> {
                p.sendMessage(Sentinel.lang.brokenLang);
            }
            case "toggle" -> {
                debugMode = !debugMode;
                p.sendMessage(Text.prefix((debugMode ? "Enabled" : "Disabled") + " debug mode."));
            }
            case "chat" -> {
                AsyncPlayerChatEvent message = new AsyncPlayerChatEvent(true,p,args.getAll(2).toString(), Set.of(p));
                AdvancedBlockers.handleAdvanced(message, ReportFalsePositives.initializeReport(message));
                AntiSpam.handleAntiSpam(message,ReportFalsePositives.initializeReport(message));
                ProfanityFilter.handleProfanityFilter(message,ReportFalsePositives.initializeReport(message));
                if (!message.isCancelled()) p.sendMessage(Text.prefix("Message did not get flagged."));
            }
        }
    }

    @Override
    public void dispatchCompletions(CompletionBuilder b) {
        List<String> players = new ArrayList<>();
        for (Player player : ServerUtils.getPlayers()) {
            players.add(player.getName());
        }
        b.then(b.arg("reload"))
        .then(b.arg("full-system-check"))
        .then(b.arg("false-positive")
                .then(b.arg("add"))
                .then(b.arg("remove")))
        .then(b.arg("debug")
                .then(b.arg("lang"))
                .then(b.arg("toggle"))
                .then(b.arg("chat")))
        .then(b.arg("commandblock"));
    }
}
