package me.trouper.sentinel.server.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.SerialLocation;
import me.trouper.sentinel.data.types.CommandBlockHolder;
import me.trouper.sentinel.server.events.admin.WandEvents;
import me.trouper.sentinel.server.functions.chatfilter.profanity.ProfanityFilter;
import me.trouper.sentinel.server.functions.chatfilter.spam.SpamFilter;
import me.trouper.sentinel.server.functions.chatfilter.unicode.UnicodeFilter;
import me.trouper.sentinel.server.functions.chatfilter.url.UrlFilter;
import me.trouper.sentinel.data.types.Selection;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.startup.drm.Loader;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
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
import org.bukkit.entity.minecart.CommandMinecart;

import java.util.*;

@CommandRegistry(value = "sentinel", permission = @Permission("sentinel.staff"), printStackTrace = true)
public class SentinelCommand implements CustomCommand {

    // Constants for usage messages
    private static final String USAGE_SENTINEL = "Usage: /sentinel <wand|reload|config|false-positive|debug|commandblock|socialspy>";
    private static final String USAGE_COMMANDBLOCK = "Usage: /sentinel commandblock <selection|add|remove|auto|restore|clear>";
    private static final String USAGE_SELECTION = "Usage: /sentinel commandblock selection <add|remove|delete|deselect|pos1|pos2>";
    private static final String USAGE_RESTORE = "Usage: /sentinel commandblock restore <all|player>";
    private static final String USAGE_CLEAR = "Usage: /sentinel commandblock clear <all|player>";

    public static Map<UUID, Boolean> spyMap = new HashMap<>();

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String s, Args args) {
        try {
            processCommand(sender, command, s, args);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.plugin.invalidArgs));
        }
    }

    @Override
    public void dispatchCompletions(CommandSender sender, Command command, String s, CompletionBuilder b) {
        b.then(b.arg("socialspy"));
        b.then(b.arg("config"));
        b.then(b.arg("wand"));
        b.then(b.arg("reload"));
        b.then(b.arg("false-positive").then(b.arg("add", "remove")));
        b.then(b.arg("debug").then(b.arg("lang", "toggle", "chat")));
        b.then(b.arg("commandblock", "cb").then(
                        b.arg("add", "remove", "auto"))
                .then(b.arg("selection")
                        .then(b.arg("add", "remove", "delete", "deselect", "pos1", "pos2")))
                .then(b.arg("restore")
                        .then(b.arg("<player>", "all")))
                .then(b.arg("clear")
                        .then(b.arg("<player>", "all"))));
    }

    /* Main Command Processing */
    private void processCommand(CommandSender sender, Command command, String label, Args args) {
        // Lite mode check
        if (Sentinel.getInstance().getDirector().loader.isLite()) {
            handleLiteMessage(sender, args);
            return;
        }

        if (args.isEmpty()) {
            sender.sendMessage(Text.prefix(USAGE_SENTINEL));
            return;
        }

        String subCommand = args.get(0).toString().toLowerCase();
        switch (subCommand) {
            case "reload" -> handleReload(sender);
            case "wand" -> handleWand(sender);
            case "config" -> handleConfig(sender);
            case "commandblock", "cb" -> handleCommandBlock(sender, args);
            case "debug" -> handleDebugCommand(sender, args);
            case "false-positive" -> handleFalsePositive(sender, args);
            case "socialspy" -> handleSocialSpy(sender);
            default -> sender.sendMessage(Text.prefix("Invalid sub-command. " + USAGE_SENTINEL));
        }
    }

    /* Helper Method: Ensure Sender is a Player */
    private Player getPlayer(CommandSender sender) {
        if (sender instanceof Player p) {
            return p;
        }
        sender.sendMessage(Text.prefix("Only players can execute this command."));
        return null;
    }

    /* =======================
       Subcommand: RELOAD
       ======================= */
    private void handleReload(CommandSender sender) {
        if (!PlayerUtils.isTrusted(sender)) {
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.permissions.noTrust));
            return;
        }
        Sentinel.getInstance().getLogger().info("Sentinel is now reloading the config.");
        sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.plugin.reloadingConfig));
        Sentinel.getInstance().getDirector().io.loadConfig();
    }

    /* =======================
       Subcommand: WAND
       ======================= */
    private void handleWand(CommandSender sender) {
        if (!PlayerUtils.playerCheck(sender)) return;
        if (!PlayerUtils.isTrusted(sender)) return;
        Player p = (Player) sender;
        p.give(WandEvents.SELECTION_WAND);
        sender.sendMessage(Text.prefix("Given you a selection wand."));
    }

    /* =======================
       Subcommand: CONFIG
       ======================= */ 
    private void handleConfig(CommandSender sender) {
        Player p = getPlayer(sender);
        if (p == null) return;
        if (!PlayerUtils.isTrusted(p)) return;
        if (!MainGUI.verify(p)) return;

        p.openInventory(new MainGUI().home.getInventory());
    }

    /* =======================
       Subcommand: COMMANDBLOCK
       ======================= */
    private void handleCommandBlock(CommandSender sender, Args args) {
        if (!PlayerUtils.isTrusted(sender)) return;

        if (args.getSize() < 2) {
            sender.sendMessage(Text.prefix(USAGE_COMMANDBLOCK));
            return;
        }

        String sub = args.get(1).toString().toLowerCase();
        switch (sub) {
            case "selection" -> handleCommandBlockSelection(sender, args);
            case "add" -> handleCommandBlockAdd(sender);
            case "remove" -> handleCommandBlockRemove(sender);
            case "auto" -> handleCommandBlockAuto(sender);
            case "restore" -> handleCommandBlockRestore(sender, args);
            case "clear" -> handleCommandBlockClear(sender, args);
            default -> sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.plugin.invalidSubCommand.formatted("commandblock")));
        }
    }
    
    // --- CommandBlock -> SELECTION ---
    private void handleCommandBlockSelection(CommandSender sender, Args args) {
        if (args.getSize() < 3) {
            sender.sendMessage(Text.prefix(USAGE_SELECTION));
            return;
        }
        Player p = getPlayer(sender);
        if (p == null) return;

        String action = args.get(2).toString().toLowerCase();
        switch (action) {
            case "add" -> Sentinel.getInstance().getDirector().whitelistManager.addSelectionToWhitelist(p);
            case "remove" -> Sentinel.getInstance().getDirector().whitelistManager.removeSelectionFromWhitelist(p);
            case "delete" -> Sentinel.getInstance().getDirector().whitelistManager.deleteSelection(p);
            case "deselect", "desel" -> WandEvents.selections.remove(p.getUniqueId());
            case "pos1" -> {
                Selection selection = WandEvents.selections.computeIfAbsent(p.getUniqueId(), k -> new Selection());
                selection.setPos1(p.getLocation());
                p.sendMessage(Text.prefix("Position 1 set at " + Text.formatLoc(p.getLocation())));
            }
            case "pos2" -> {
                Selection selection = WandEvents.selections.computeIfAbsent(p.getUniqueId(), k -> new Selection());
                selection.setPos2(p.getLocation());
                p.sendMessage(Text.prefix("Position 2 set at " + Text.formatLoc(p.getLocation())));
            }
            default -> p.sendMessage(Text.prefix("Invalid selection action. " + USAGE_SELECTION));
        }
    }

    // --- CommandBlock -> ADD ---
    private void handleCommandBlockAdd(CommandSender sender) {
        Player p = getPlayer(sender);
        if (p == null) return;

        if (p.getTargetEntity(10) instanceof CommandMinecart cm) {
            Sentinel.getInstance().getDirector().whitelistManager
                    .generateHolder(p.getUniqueId(), cm).addAndWhitelist();
            return;
        }
        Block target = p.getTargetBlock(Set.of(Material.AIR), 10);
        if (ServerUtils.isCommandBlock(target)) {
            CommandBlock cb = (CommandBlock) target.getState();
            Sentinel.getInstance().getDirector().whitelistManager
                    .generateHolder(p.getUniqueId(), cb).addAndWhitelist();
        } else {
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.commandBlock.notCommandBlock
                    .formatted(Text.cleanName(target.getType().toString()))));
        }
    }

    // --- CommandBlock -> REMOVE ---
    private void handleCommandBlockRemove(CommandSender sender) {
        Player p = getPlayer(sender);
        if (p == null) return;

        if (p.getTargetEntity(10) instanceof CommandMinecart cm) {
            CommandBlockHolder wb = Sentinel.getInstance().getDirector().whitelistManager
                    .getFromList(cm.getUniqueId());
            if (wb != null) {
                wb.setWhitelisted(false);
                String cleanedType = Text.cleanName(SerialLocation.translate(wb.loc()).getBlock().getType().toString());
                sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.commandBlock.removeSuccess
                        .formatted(cleanedType, wb.command())));
            } else {
                sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.commandBlock.notWhitelisted
                        .formatted(Text.cleanName(cm.getType().toString()))));
            }
            return;
        }

        Block target = p.getTargetBlock(Set.of(Material.AIR), 10);
        CommandBlockHolder wb = Sentinel.getInstance().getDirector().whitelistManager
                .getFromList(target.getLocation());
        if (wb != null) {
            wb.setWhitelisted(false);
            String cleanedType = Text.cleanName(SerialLocation.translate(wb.loc()).getBlock().getType().toString());
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.commandBlock.removeSuccess
                    .formatted(cleanedType, wb.command())));
        } else {
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.commandBlock.notWhitelisted
                    .formatted(Text.cleanName(target.getType().toString()))));
        }
    }

    // --- CommandBlock -> AUTO ---
    private void handleCommandBlockAuto(CommandSender sender) {
        Player p = getPlayer(sender);
        if (p == null) return;

        var whitelistManager = Sentinel.getInstance().getDirector().whitelistManager;
        if (whitelistManager.autoWhitelist.contains(p.getUniqueId())) {
            whitelistManager.autoWhitelist.remove(p.getUniqueId());
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.commandBlock.autoWhitelistOff));
        } else {
            whitelistManager.autoWhitelist.add(p.getUniqueId());
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.commandBlock.autoWhitelistOn));
        }
    }

    // --- CommandBlock -> RESTORE ---
    private void handleCommandBlockRestore(CommandSender sender, Args args) {
        if (args.getSize() < 3) {
            sender.sendMessage(Text.prefix(USAGE_RESTORE));
            return;
        }
        String targetPlayer = args.get(2).toString();
        if (targetPlayer.equalsIgnoreCase("all")) {
            int result = Sentinel.getInstance().getDirector().whitelistManager.restoreAll();
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.commandBlock.restoreSuccess
                    .formatted(result)));
        } else {
            UUID id = Bukkit.getOfflinePlayer(targetPlayer).getUniqueId();
            int result = Sentinel.getInstance().getDirector().whitelistManager.restoreAll(id);
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.commandBlock.restorePlayerSuccess
                    .formatted(result, targetPlayer)));
        }
    }

    // --- CommandBlock -> CLEAR ---
    private void handleCommandBlockClear(CommandSender sender, Args args) {
        if (args.getSize() < 3) {
            sender.sendMessage(Text.prefix(USAGE_CLEAR));
            return;
        }
        String targetPlayer = args.get(2).toString();
        if (targetPlayer.equalsIgnoreCase("all")) {
            int result = Sentinel.getInstance().getDirector().whitelistManager.clearAll();
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.commandBlock.clearSuccess
                    .formatted(result)));
        } else {
            UUID id = Bukkit.getOfflinePlayer(targetPlayer).getUniqueId();
            int result = Sentinel.getInstance().getDirector().whitelistManager.clearAll(id);
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.commandBlock.clearPlayerSuccess
                    .formatted(result, targetPlayer)));
        }
    }

    /* =======================
       Subcommand: DEBUG
       ======================= */
    private void handleDebugCommand(CommandSender sender, Args args) {
        if (!PlayerUtils.checkPermission(sender, "sentinel.debug")) return;
        if (args.getSize() < 2) {
            sender.sendMessage(Text.prefix("Usage: /sentinel debug <lang|toggle|chat>"));
            return;
        }
        String sub = args.get(1).toString().toLowerCase();
        switch (sub) {
            case "lang" -> sender.sendMessage(Sentinel.getInstance().getDirector().io.lang.brokenLang);
            case "toggle" -> {
                Sentinel.getInstance().getDirector().io.mainConfig.debugMode = !Sentinel.getInstance().getDirector().io.mainConfig.debugMode;
                String message = Sentinel.getInstance().getDirector().io.mainConfig.debugMode
                        ? Sentinel.getInstance().getDirector().io.lang.debug.debugEnabled
                        : Sentinel.getInstance().getDirector().io.lang.debug.debugDisabled;
                sender.sendMessage(Text.prefix(message));
                Sentinel.getInstance().getDirector().io.mainConfig.save();
            }
            case "chat" -> handleDebugChat(sender, args);
            default -> sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.plugin.invalidSubCommand.formatted("debug")));
        }
    }

    private void handleDebugChat(CommandSender sender, Args args) {
        if (!PlayerUtils.playerCheck(sender)) return;
        if (args.getSize() < 3) {
            sender.sendMessage(Text.prefix("Usage: /sentinel debug chat <message>"));
            return;
        }
        Player p = (Player) sender;
        String messageText = args.getAll(2).toString();
        AsyncChatEvent chatEvent = new AsyncChatEvent(true,
                p,
                Set.of(p),
                ChatRenderer.defaultRenderer(),
                Component.text(messageText),
                Component.text(messageText),
                SignedMessage.system(messageText, Component.text(messageText))
        );
        UnicodeFilter.handleUnicodeFilter(chatEvent);
        UrlFilter.handleUrlFilter(chatEvent);
        SpamFilter.handleSpamFilter(chatEvent);
        ProfanityFilter.handleProfanityFilter(chatEvent);
        if (!chatEvent.isCancelled()) {
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.debug.notFlagged));
        }
    }

    /* =======================
       Subcommand: FALSE-POSITIVE
       ======================= */
    private void handleFalsePositive(CommandSender sender, Args args) {
        if (args.getSize() < 2) {
            sender.sendMessage(Text.prefix("Usage: /sentinel false-positive <add|remove> <value>"));
            return;
        }
        if (!PlayerUtils.checkPermission(sender, "sentinel.false-positive")) return;
        String sub = args.get(1).toString().toLowerCase();
        String falsePositive = args.getAll(2).toString();

        Node root = new Node("Sentinel");
        root.addTextLine("False Positive Management Log");
        Node info = new Node("Info");
        info.addKeyValue("User", sender.getName());

        switch (sub) {
            case "add" -> {
                if (!PlayerUtils.checkPermission(sender, "sentinel.false-positive.add")) return;
                Sentinel.getInstance().getDirector().io.fpConfig.swearWhitelist.add(falsePositive);
                sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.falsePositive.addSuccess.formatted(falsePositive)));
                info.addKeyValue("Action", "Add");
            }
            case "remove" -> {
                if (!PlayerUtils.checkPermission(sender, "sentinel.false-positive.remove")) return;
                Sentinel.getInstance().getDirector().io.fpConfig.swearWhitelist.remove(falsePositive);
                sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.falsePositive.removeSuccess.formatted(falsePositive)));
                info.addKeyValue("Action", "Remove");
            }
            default -> {
                sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.plugin.invalidSubCommand.formatted("false-positive")));
                return;
            }
        }
        info.addKeyValue("False Positive Edited", falsePositive);
        root.addChild(info);
        Sentinel.getInstance().getDirector().io.fpConfig.save();
        Sentinel.getInstance().getLogger().info(ConsoleFormatter.format(root));
        EmbedFormatter.sendEmbed(EmbedFormatter.format(root));
    }

    /* =======================
       Subcommand: SOCIALSPY
       ======================= */
    private void handleSocialSpy(CommandSender sender) {
        if (!PlayerUtils.playerCheck(sender)) return;
        if (!PlayerUtils.checkPermission(sender, "sentinel.socialspy")) return;
        Player p = (Player) sender;
        UUID senderID = p.getUniqueId();
        boolean enabled = spyMap.getOrDefault(senderID, false);
        if (!enabled) {
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.socialSpy.enabled));
            spyMap.put(senderID, true);
        } else {
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.socialSpy.disabled));
            spyMap.put(senderID, false);
        }
    }

    /* =======================
       Lite Mode Handler
       ======================= */
    private void handleLiteMessage(CommandSender sender, Args args) {
        if (!args.isEmpty() && args.get(0).toString().equalsIgnoreCase("reload")) {
            if (!PlayerUtils.isTrusted(sender)) {
                sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.permissions.noTrust));
                return;
            }
            Sentinel.getInstance().getLogger().info("Sentinel is now reloading the config in lite mode.");
            sender.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.plugin.reloadingConfigLite));
            Sentinel.getInstance().getDirector().io.loadConfig();

            if (Sentinel.getInstance().getDirector().loader.load(Sentinel.getInstance().license, Sentinel.getInstance().identifier, false)) {
                return;
            }
            Sentinel.getInstance().getLogger().info("Re-authentication Failed.");
        } else {
            sender.sendMessage(Loader.LITE_MODE);
        }
    }
}