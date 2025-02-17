package me.trouper.sentinel.server.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.MainConfig;
import me.trouper.sentinel.data.types.WhitelistedBlock;
import me.trouper.sentinel.server.functions.CBWhitelistManager;
import me.trouper.sentinel.server.functions.chatfilter.profanity.ProfanityFilter;
import me.trouper.sentinel.server.functions.chatfilter.spam.SpamFilter;
import me.trouper.sentinel.server.functions.chatfilter.unicode.UnicodeFilter;
import me.trouper.sentinel.server.functions.chatfilter.url.UrlFilter;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.startup.Load;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.Text;
import me.trouper.sentinel.utils.trees.ConsoleFormatter;
import me.trouper.sentinel.utils.trees.EmbedFormatter;
import me.trouper.sentinel.utils.trees.Node;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@CommandRegistry(value = "sentinel",permission = @Permission("sentinel.staff"),printStackTrace = true)
public class SentinelCommand implements CustomCommand {

    public static Map<UUID, Boolean> spyMap = new HashMap<>();

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String s, Args args) {
        try {
            safety(sender,command,s,args);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Text.prefix(Sentinel.lang.plugin.invalidArgs));
        }
    }

    @Override
    public void dispatchCompletions(CommandSender commandSender, Command command, String s, CompletionBuilder b) {
        b.then(b.arg("socialspy"));
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


    private void safety(CommandSender sender, Command command, String label, Args args) {
        if (Load.lite) {
            handleLiteMessage(sender, args);
            return;
        }
        if (args.isEmpty()) {
            sender.sendMessage(Text.prefix("Usage: /sentinel <reload|config|false-positive|debug|commandblock|socialspy>"));
            return;
        }

        String subCommand = args.get(0).toString().toLowerCase();
        switch (subCommand) {
            case "reload" -> handleReload(sender);
            case "config" -> handleConfig(sender);
            case "commandblock", "cb" -> handleCommandBlock(sender, args);
            case "debug" -> handleDebugCommand(sender, args);
            case "false-positive" -> handleFalsePositive(sender, args);
            case "socialspy" -> handleSocialSpy(sender);
            default -> sender.sendMessage(Text.prefix("Invalid sub-command. Usage: /sentinel <reload|config|false-positive|debug|commandblock|socialspy>"));
        }
    }


    private void handleReload(CommandSender sender) {
        if (sender instanceof Player p) {
            if (!PlayerUtils.checkPermission(sender, "sentinel.reload") || !PlayerUtils.isTrusted(p)) {
                p.sendMessage(Text.prefix(Sentinel.lang.permissions.noTrust));
                return;
            }
        }
        Sentinel.log.info("Sentinel is now reloading the config.");
        sender.sendMessage(Text.prefix(Sentinel.lang.plugin.reloadingConfig));
        Sentinel.getInstance().loadConfig();
    }

    private void handleConfig(CommandSender sender) {
        if (!PlayerUtils.playerCheck(sender))
            return;
        Player p = (Player) sender;
        if (!PlayerUtils.checkPermission(sender, "sentinel.config") || !PlayerUtils.isTrusted(p))
            return;
        if (!MainGUI.verify(p))
            return;
        p.openInventory(new MainGUI().home.getInventory());
    }

    private void handleCommandBlock(CommandSender sender, Args args) {
        if (!PlayerUtils.isTrusted(sender))
            return;

        if (args.getSize() < 2) {
            sender.sendMessage(Text.prefix("Usage: /sentinel commandblock <add|remove|auto|restore|clear>"));
            return;
        }
        String sub = args.get(1).toString().toLowerCase();
        switch (sub) {
            case "add" -> {
                if (!PlayerUtils.playerCheck(sender))
                    return;
                Player p = (Player) sender;
                Block target = p.getTargetBlock(Set.of(Material.AIR), 10);
                if (target.getType() == Material.COMMAND_BLOCK ||
                        target.getType() == Material.REPEATING_COMMAND_BLOCK ||
                        target.getType() == Material.CHAIN_COMMAND_BLOCK) {
                    CommandBlock cb = (CommandBlock) target.getState();
                    CBWhitelistManager.add(cb, p.getUniqueId());
                } else {
                    sender.sendMessage(Text.prefix(Sentinel.lang.commandBlock.notCommandBlock.formatted(Text.cleanName(target.getType().toString()))));
                }
            }
            case "remove" -> {
                if (!PlayerUtils.playerCheck(sender))
                    return;
                Player p = (Player) sender;
                Block target = p.getTargetBlock(Set.of(Material.AIR), 10);
                WhitelistedBlock wb = CBWhitelistManager.get(target.getLocation());
                if (wb != null) {
                    CBWhitelistManager.remove(target.getLocation());
                    String cleanedType = Text.cleanName(WhitelistedBlock.fromSerialized(wb.loc()).getBlock().getType().toString());
                    sender.sendMessage(Text.prefix(Sentinel.lang.commandBlock.removeSuccess.formatted(cleanedType, wb.command())));
                } else {
                    sender.sendMessage(Text.prefix(Sentinel.lang.commandBlock.notWhitelisted.formatted(Text.cleanName(target.getType().toString()))));
                }
            }
            case "auto" -> {
                if (!PlayerUtils.playerCheck(sender))
                    return;
                Player p = (Player) sender;
                if (CBWhitelistManager.autoWhitelist.contains(p.getUniqueId())) {
                    CBWhitelistManager.autoWhitelist.remove(p.getUniqueId());
                    sender.sendMessage(Text.prefix(Sentinel.lang.commandBlock.autoWhitelistOn));
                } else {
                    CBWhitelistManager.autoWhitelist.add(p.getUniqueId());
                    sender.sendMessage(Text.prefix(Sentinel.lang.commandBlock.autoWhitelistOff));
                }
            }
            case "restore" -> {
                if (args.getSize() < 3) {
                    sender.sendMessage(Text.prefix("Usage: /sentinel commandblock restore <all|player>"));
                    return;
                }
                String targetPlayer = args.get(2).toString();
                if (targetPlayer.equalsIgnoreCase("all")) {
                    int result = CBWhitelistManager.restoreAll();
                    sender.sendMessage(Text.prefix(Sentinel.lang.commandBlock.restoreSuccess.formatted(result)));
                } else {
                    UUID id = Bukkit.getOfflinePlayer(targetPlayer).getUniqueId();
                    int result = CBWhitelistManager.restoreAll(id);
                    sender.sendMessage(Text.prefix(Sentinel.lang.commandBlock.restorePlayerSuccess.formatted(result,targetPlayer)));
                }
            }
            case "clear" -> {
                if (args.getSize() < 3) {
                    sender.sendMessage(Text.prefix("Usage: /sentinel commandblock clear <all|player>"));
                    return;
                }
                String targetPlayer = args.get(2).toString();
                if (targetPlayer.equalsIgnoreCase("all")) {
                    int result = CBWhitelistManager.clearAll();
                    sender.sendMessage(Text.prefix(Sentinel.lang.commandBlock.clearSuccess.formatted(result)));
                } else {
                    UUID id = Bukkit.getOfflinePlayer(targetPlayer).getUniqueId();
                    int result = CBWhitelistManager.clearAll(id);
                    sender.sendMessage(Text.prefix(Sentinel.lang.commandBlock.clearPlayerSuccess.formatted(result,targetPlayer)));
                }
            }
            default -> sender.sendMessage(Text.prefix(Sentinel.lang.plugin.invalidSubCommand.formatted("commandblock")));
        }
    }

    private void handleDebugCommand(CommandSender sender, Args args) {
        if (!PlayerUtils.checkPermission(sender, "sentinel.debug"))
            return;
        if (args.getSize() < 2) {
            sender.sendMessage(Text.prefix("Usage: /sentinel debug <lang|toggle|chat>"));
            return;
        }
        String sub = args.get(1).toString().toLowerCase();
        switch (sub) {
            case "lang" -> sender.sendMessage(Sentinel.lang.brokenLang);
            case "toggle" -> {
                Sentinel.mainConfig.debugMode = !Sentinel.mainConfig.debugMode;
                Sentinel.mainConfig.debugMode = !Sentinel.mainConfig.debugMode;
                String message = Sentinel.mainConfig.debugMode
                        ? Sentinel.lang.debug.debugEnabled
                        : Sentinel.lang.debug.debugDisabled;
                sender.sendMessage(Text.prefix(message));
                Sentinel.mainConfig.save();
            }
            case "chat" -> {
                if (!PlayerUtils.playerCheck(sender))
                    return;
                if (args.getSize() < 3) {
                    sender.sendMessage(Text.prefix("Usage: /sentinel debug chat <message>"));
                    return;
                }
                Player p = (Player) sender;
                String messageText = args.getAll(2).toString();
                AsyncChatEvent message = new AsyncChatEvent(true,
                        p,
                        Set.of(p),
                        ChatRenderer.defaultRenderer(),
                        Component.text(messageText),
                        Component.text(messageText),
                        SignedMessage.system(messageText, Component.text(messageText))
                );
                UnicodeFilter.handleUnicodeFilter(message);
                UrlFilter.handleUrlFilter(message);
                SpamFilter.handleSpamFilter(message);
                ProfanityFilter.handleProfanityFilter(message);
                if (!message.isCancelled()) {
                    sender.sendMessage(Text.prefix(Sentinel.lang.debug.notFlagged));
                }
            }
            default -> sender.sendMessage(Text.prefix(Sentinel.lang.plugin.invalidSubCommand.formatted("debug")));
        }
    }

    private void handleFalsePositive(CommandSender sender, Args args) {
        if (args.getSize() < 2) {
            sender.sendMessage(Text.prefix("Usage: /sentinel false-positive <add|remove> <value>"));
            return;
        }
        if (!PlayerUtils.checkPermission(sender, "sentinel.false-positive"))
            return;
        String sub = args.get(1).toString().toLowerCase();
        String falsePositive = args.getAll(2).toString();
        Node root = new Node("Sentinel");
        root.addTextLine("False Positive Management Log");
        Node info = new Node("Info");
        info.addKeyValue("User", sender.getName());
        switch (sub) {
            case "add" -> {
                if (!PlayerUtils.checkPermission(sender,"sentinel.false-positive.add")) return;
                Sentinel.fpConfig.swearWhitelist.add(falsePositive);
                sender.sendMessage(Text.prefix(Sentinel.lang.falsePositive.addSuccess.formatted(falsePositive)));
                info.addKeyValue("Action", "Add");
            }
            case "remove" -> {
                if (!PlayerUtils.checkPermission(sender,"sentinel.false-positive.remove")) return;
                Sentinel.fpConfig.swearWhitelist.remove(falsePositive);
                sender.sendMessage(Text.prefix(Sentinel.lang.falsePositive.removeSuccess.formatted(falsePositive)));
                info.addKeyValue("Action", "Remove");
            }
            default -> {
                sender.sendMessage(Text.prefix(Sentinel.lang.plugin.invalidSubCommand.formatted("false-positive")));
                return;
            }
        }
        info.addKeyValue("False Positive Edited", falsePositive);
        root.addChild(info);
        Sentinel.fpConfig.save();
        Sentinel.log.info(ConsoleFormatter.format(root));
        EmbedFormatter.sendEmbed(EmbedFormatter.format(root));
    }

    private void handleSocialSpy(CommandSender sender) {
        if (!PlayerUtils.playerCheck(sender))
            return;
        if (!PlayerUtils.checkPermission(sender, "sentinel.socialspy"))
            return;
        Player p = (Player) sender;
        UUID senderID = p.getUniqueId();
        boolean enabled = spyMap.getOrDefault(senderID, false);
        if (!enabled) {
            sender.sendMessage(Text.prefix(Sentinel.lang.socialSpy.enabled));
            spyMap.put(senderID, true);
        } else {
            sender.sendMessage(Text.prefix(Sentinel.lang.socialSpy.disabled));
            spyMap.put(senderID, false);
        }
    }

    private void handleLiteMessage(CommandSender sender, Args args) {
        if (!args.isEmpty() && args.get(0).toString().equalsIgnoreCase("reload")) {
            if (sender instanceof Player p && !PlayerUtils.isTrusted(p)) {
                sender.sendMessage(Text.prefix(Sentinel.lang.permissions.noTrust));
                return;
            }
            Sentinel.log.info("Sentinel is now reloading the config in lite mode.");
            sender.sendMessage(Text.prefix(Sentinel.lang.plugin.reloadingConfigLite));
            Sentinel.getInstance().loadConfig();

            if (Load.load(Sentinel.getInstance().license, Sentinel.getInstance().identifier, false)) {
                return;
            }
            Sentinel.log.info("Re-authentication Failed.");
        } else {
            sender.sendMessage(Load.liteMode);
        }
    }
}
