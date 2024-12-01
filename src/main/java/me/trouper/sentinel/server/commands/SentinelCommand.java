package me.trouper.sentinel.server.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.chatfilter.regex.AntiRegex;
import me.trouper.sentinel.server.functions.chatfilter.profanity.AntiProfanity;
import me.trouper.sentinel.server.functions.chatfilter.spam.AntiSpam;
import me.trouper.sentinel.data.config.MainConfig;
import me.trouper.sentinel.server.functions.CBWhitelistManager;
import me.trouper.sentinel.data.types.WhitelistedBlock;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.startup.Auth;
import me.trouper.sentinel.startup.Load;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.Text;
import me.trouper.sentinel.utils.trees.ConsoleFormatter;
import me.trouper.sentinel.utils.trees.EmbedFormatter;
import me.trouper.sentinel.utils.trees.Node;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;
import java.util.UUID;

@CommandRegistry(value = "sentinel",permission = @Permission("sentinel.staff"),printStackTrace = true)
public class SentinelCommand implements CustomCommand {

    public static String liteMode = Text.color("""
                        &8]=-&f Welcome to &d&lSentinel &7|&f Anti-Nuke &8-=[
                        &7The plugin is currently loaded in &clite&7 mode.
                        
                        &7Your License Key is &a%s&7.
                        &7Your server ID is &6%s&7.
                        &7You are &6%s&7.
                        
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
                        """.formatted(Auth.getLicenseKey(),Auth.getServerID(), MainConfig.username));

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String s, Args args) {
        if (Load.lite) {
            if (!args.isEmpty() && args.get(0).toString().equals("reload")) {
                if (!(sender instanceof ConsoleCommandSender) && !PlayerUtils.isTrusted((Player) sender)) {
                    sender.sendMessage(Text.prefix(Sentinel.lang.permissions.noTrust));
                    return;
                }
                Sentinel.log.info("Sentinel is now Reloading the config.");
                sender.sendMessage(Text.prefix("Reloading the config."));
                Sentinel.loadConfig();
                String serverID = Auth.getServerID();
                String license = Sentinel.mainConfig.plugin.license;
                String nonce = Auth.getNonce();
                Auth.getPort();

                Sentinel.log.info("\n]====---- Requesting Authentication ----====[ \n- License Key: %s\n- Server ID: %s\n- Nonce: %s\n".formatted(license, serverID, nonce));
                if (Auth.canLoad()) {
                    Load.startup(false);
                    return;
                }
                Sentinel.log.info("Re-authentication Failed.");
            } else {
                sender.sendMessage(liteMode);
            }
            return;
        }
        if (args.getSize() > 0 == args.get(0).toString().equals("reload")) {
            if (sender instanceof Player p) {
                if (!PlayerUtils.isTrusted(p)) {
                    p.sendMessage(Text.prefix(Sentinel.lang.permissions.noTrust));
                    return;
                }
            }
            Sentinel.log.info("Sentinel is now Reloading the config.");
            sender.sendMessage(Text.prefix("Reloading the config."));
            Sentinel.loadConfig();
        }
        Player p = (Player) sender;
        if (!p.hasPermission("sentinel.staff")) return;
        switch (args.get(0).toString()) {
            case "config" -> {
                if (!PlayerUtils.isTrusted(p)) return;
                if (!MainGUI.verify(p)) return;
                p.openInventory(new MainGUI().home.getInventory());
            }
            case "commandblock", "cb" -> handleCommandBlock(p,args);
            case "debug" -> handleDebugCommand(p,args);
            case "false-positive" -> handleFalsePositive(p,args);
        }
    }

    @Override
    public void dispatchCompletions(CommandSender commandSender, Command command, String s, CompletionBuilder b) {
        b.then(b.arg("config"));
        b.then(b.arg("reload"));
        b.then(b.arg("false-positive").then(b.arg("add","remove")));
        b.then(b.arg("debug").then(
                b.arg("lang","toggle","chat")));
        b.then(b.arg("commandblock","cb").then(b.arg("add","remove","auto"))
                .then(b.arg("restore")
                        .then(b.arg("<player>","all")))
                .then(b.arg("clear")
                        .then(b.arg("<player>","all"))));
    }

    private void handleFalsePositive(Player p, Args args) {
        if (!p.hasPermission("sentinel.chat.antiswear.edit")) {
            p.sendMessage(Sentinel.lang.permissions.noPermission);
            return;
        }
        String falsePositive = args.getAll(2).toString();
        Node root = new Node("Sentinel");
        root.addTextLine("False Positive Management Log");
        Node info = new Node("Info");
        info.addKeyValue("User",p.getName());
        switch (args.get(1).toString()) {
            case "add" -> {
                Sentinel.fpConfig.swearWhitelist.add(falsePositive);
                p.sendMessage(Text.prefix("&7Successfully added &a%s&7 to the false positive list!".formatted(falsePositive)));
                info.addKeyValue("Action","Add");
            }
            case "remove" -> {
                Sentinel.fpConfig.swearWhitelist.remove(falsePositive);
                p.sendMessage(Text.prefix("&7Successfully removed &c%s&7 from the false positive list!".formatted(falsePositive)));
                info.addKeyValue("Action","Remove");
            }
        }
        info.addKeyValue("False Positive Edited", falsePositive);
        root.addChild(info);
        Sentinel.fpConfig.save();
        Sentinel.log.info(ConsoleFormatter.format(root));
        EmbedFormatter.sendEmbed(EmbedFormatter.format(root));
    }

    private void handleCommandBlock(Player p, Args args) {
        if (!PlayerUtils.isTrusted(p)) {
            p.sendMessage(Text.prefix(Sentinel.lang.permissions.noTrust));
            return;
        }
        Block target = p.getTargetBlock(Set.of(Material.AIR),10);
        switch (args.get(1).toString()) {
            case "add" -> {
                if (target.getType().equals(Material.COMMAND_BLOCK) || target.getType().equals(Material.REPEATING_COMMAND_BLOCK) || target.getType().equals(Material.CHAIN_COMMAND_BLOCK)) {
                    CommandBlock cb = (CommandBlock) target.getState();
                    CBWhitelistManager.add(cb,p.getUniqueId());
                    return;
                }
                p.sendMessage(Text.prefix("Could not whitelist the &b" + Text.cleanName(target.getType().toString()) + "&7 it is not a command block!"));
            }
            case "remove" -> {
                WhitelistedBlock wb = CBWhitelistManager.get(target.getLocation());
                if (wb != null) {
                    CBWhitelistManager.remove(target.getLocation());
                    p.sendMessage(Text.prefix("Successfully removed 1 &b" + Text.cleanName(WhitelistedBlock.fromSerialized(wb.loc()).getBlock().getType().toString()) + "&7 with the command &a" + wb.command() + "&7."));
                    return;
                }
                p.sendMessage(Text.prefix("Could not un-whitelist the &b" + Text.cleanName(target.getType().toString()) + "&7 it wasn't whitelisted in the first place!"));
            }
            case "auto" -> {
                if (CBWhitelistManager.autoWhitelist.contains(p.getUniqueId())) {
                    CBWhitelistManager.autoWhitelist.remove(p.getUniqueId());
                    p.sendMessage(Text.prefix("Successfully toggled &bauto whitelist&7 off for you."));
                } else {
                    CBWhitelistManager.autoWhitelist.add(p.getUniqueId());
                    p.sendMessage(Text.prefix("Successfully toggled &bauto whitelist&7 on for you."));
                }
            }
            case "restore" -> {
                if (args.get(2).toString().equals("all")) {
                    int result = CBWhitelistManager.restoreAll();
                    p.sendMessage(Text.prefix("Successfully restored &b%s&7 command blocks.".formatted(result)));
                    return;
                }
                String who = args.get(2).toString();
                UUID id = Bukkit.getOfflinePlayer(who).getUniqueId();
                int result = CBWhitelistManager.restoreAll(id);
                p.sendMessage(Text.prefix("Successfully restored &b%s&7 command blocks from &e%s&7.".formatted(result,who)));
            }
            case "clear" -> {
                if (args.get(2).toString().equals("all")) {
                    int result = CBWhitelistManager.clearAll();
                    p.sendMessage(Text.prefix("Successfully cleared &b%s&7 command blocks.".formatted(result)));
                    return;
                }
                String who = args.get(2).toString();
                UUID id = Bukkit.getOfflinePlayer(who).getUniqueId();
                int result = CBWhitelistManager.clearAll(id);
                p.sendMessage(Text.prefix("Successfully cleared &b%s&7 command blocks from &e%s&7.".formatted(result,who)));
            }
        }
    }

    private void handleDebugCommand(Player p, Args args) {
        if (!PlayerUtils.isTrusted(p)) {
            p.sendMessage(Text.prefix(Sentinel.lang.permissions.noTrust));
            return;
        }
        switch (args.get(1).toString()) {
            case "lang" -> {
                p.sendMessage(Sentinel.lang.brokenLang);
            }
            case "toggle" -> {
                Sentinel.mainConfig.debugMode = !Sentinel.mainConfig.debugMode ;
                p.sendMessage(Text.prefix((Sentinel.mainConfig.debugMode  ? "Enabled" : "Disabled") + " debug mode."));
                Sentinel.mainConfig.save();
            }
            case "chat" -> {
                AsyncPlayerChatEvent message = new AsyncPlayerChatEvent(true,p,args.getAll(2).toString(), Set.of(p));
                AntiRegex.handleRegex(message);
                AntiSpam.handleAntiSpam(message);
                AntiProfanity.handleProfanityFilter(message);
                if (!message.isCancelled()) p.sendMessage(Text.prefix("Message did not get flagged."));
            }
        }
    }

}
