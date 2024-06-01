package io.github.thetrouper.sentinel.cmds;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.cmdblocks.WhitelistedBlock;
import io.github.thetrouper.sentinel.server.functions.*;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@CommandRegistry(value = "sentinel",printStackTrace = true)
public class SentinelCommand implements CustomCommand {
    public static boolean debugMode;
    public static List<UUID> autoWhitelist = new ArrayList<>();

    @Override
    public void dispatchCommand(CommandSender commandSender, Args args) {
        Sentinel instance = Sentinel.getInstance();
        if (Load.lite) {
            commandSender.sendMessage(Text.color("""
                    &8]=-&f Welcome to &d&lSentinel &7|&f Anti-Nuke &8-=[
                    &7The plugin is currently loaded in &clite&7 mode.
                    
                    &fIf you have just &apurchased&f the plugin:
                     &8- &7Join the &b&ndiscord&r&7 and open a ticket.
                     &8- &7https://discord.gg/Xh6BAzNtxY
                     &8- &7You will then receive a license key.
                    &fIf you have &cnot&f purchased the plugin:
                     &8- &7Then purchase it :D
                     &8- &7It wont do anything in this state!
                     &8- &7(Its only 5$)
                    &fIf you are reading this from a decompiler:
                     &8- &7Please stop trying to crack the plugin and purchase it!
                     &8- &7Your time spent trying trying to bypass my DRM could be spent at a minimum wage job.
                     &8- &7There you will make 7$ an hour! (As oppose to 5$ for multiple hours of cracking)
                    &fWoah! You read quite far!
                     &8- &7Want the plugin for cheaper, &nor even for free&r&7?
                     &8- &7DM &b@obvwolf&7 on discord and lets make a deal!
                    """));
            return;
        }
        Player p = (Player) commandSender;
        if (!p.hasPermission("sentinel.staff")) return;
        switch (args.get(0).toString()) {
            case "commandblock", "cb" -> handleCommandBlock(p,args);
            case "reload" -> {
                if (!Sentinel.isTrusted(p)) return;
                p.sendMessage(Text.prefix("Reloading Sentinel!"));
                Sentinel.log.info("[Sentinel] Re-Initializing Sentinel!");
                instance.loadConfig();
            }
            case "full-system-check" -> {
                if (!Sentinel.isTrusted(p)) return;
                p.sendMessage(Text.prefix("Initiating a full system check!"));
                SystemCheck.fullCheck(p);
            }
            case "debug" -> handleDebugCommand(p,args);
            case "false-positive" -> handleFalsePositive(p,args);
        }
    }
    private void handleFalsePositive(Player p, Args args) {
        String falsePositive = args.getAll(2).toString();
        switch (args.get(1).toString()) {
            case "add" -> {
                Sentinel.fpConfig.swearWhitelist.add(falsePositive);
                p.sendMessage(Text.prefix("&7Successfully added &a%s&7 to the false positive list!".formatted(falsePositive)));
            }
            case "remove" -> {
                Sentinel.fpConfig.swearWhitelist.remove(falsePositive);
                p.sendMessage(Text.prefix("&7Successfully removed &c%s&7 to the false positive list!".formatted(falsePositive)));
            }
        }
        Sentinel.fpConfig.save();
    }
    private void handleCommandBlock(Player p, Args args) {
        if (!Sentinel.isTrusted(p)) return;
        Block target = p.getTargetBlock(Set.of(Material.AIR),10);
        switch (args.get(1).toString()) {
            case "add" -> {
                if (target.getType().equals(Material.COMMAND_BLOCK) || target.getType().equals(Material.REPEATING_COMMAND_BLOCK) || target.getType().equals(Material.CHAIN_COMMAND_BLOCK)) {
                    CommandBlock cb = (CommandBlock) target.getState();
                    CMDBlockWhitelist.add(cb,p.getUniqueId());
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
            case "auto" -> {
                if (autoWhitelist.contains(p.getUniqueId())) {
                    autoWhitelist.remove(p.getUniqueId());
                    p.sendMessage(Text.prefix("Successfully toggled &bauto whitelist&7 off for you."));
                } else {
                    autoWhitelist.add(p.getUniqueId());
                    p.sendMessage(Text.prefix("Successfully toggled &bauto whitelist&7 on for you."));
                }
            }
            case "restore" -> {
                if (args.get(2).toString().equals("all")) {
                    int result = CMDBlockWhitelist.restoreAll();
                    p.sendMessage(Text.prefix("Successfully restored &b%s&7 command blocks.".formatted(result)));
                    return;
                }
                String who = args.get(2).toString();
                UUID id = Bukkit.getOfflinePlayer(who).getUniqueId();
                int result = CMDBlockWhitelist.restoreAll(id);
                p.sendMessage(Text.prefix("Successfully restored &b%s&7 command blocks from &e%s&7.".formatted(result,who)));
            }
            case "clear" -> {
                if (args.get(2).toString().equals("all")) {
                    int result = CMDBlockWhitelist.clearAll();
                    p.sendMessage(Text.prefix("Successfully cleared &b%s&7 command blocks.".formatted(result)));
                    return;
                }
                String who = args.get(2).toString();
                UUID id = Bukkit.getOfflinePlayer(who).getUniqueId();
                int result = CMDBlockWhitelist.clearAll(id);
                p.sendMessage(Text.prefix("Successfully cleared &b%s&7 command blocks from &e%s&7.".formatted(result,who)));
            }
        }
    }

    private void handleDebugCommand(Player p, Args args) {
        if (!p.hasPermission("sentinel.debug")) return;
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
        b.then(b.arg("reload","full-system-check"));
        b.then(b.arg("false-positive").then(b.arg("add","remove")));
        b.then(b.arg("debug").then(
                b.arg("lang","toggle","chat")));
        b.then(b.arg("commandblock").then(b.arg("add","remove","auto"))
                .then(b.arg("restore")
                        .then(b.arg("<player>","all")))
                .then(b.arg("clear")
                        .then(b.arg("<player>","all"))));
    }
}
