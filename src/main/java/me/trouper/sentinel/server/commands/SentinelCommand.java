package me.trouper.sentinel.server.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.data.types.CommandBlockHolder;
import me.trouper.sentinel.data.types.NBTHolder;
import me.trouper.sentinel.data.types.Selection;
import me.trouper.sentinel.data.types.SerialLocation;
import me.trouper.sentinel.server.events.admin.WandEvents;
import me.trouper.sentinel.server.events.violations.players.CreativeHotbar;
import me.trouper.sentinel.server.functions.chatfilter.profanity.ProfanityFilter;
import me.trouper.sentinel.server.functions.chatfilter.spam.SpamFilter;
import me.trouper.sentinel.server.functions.chatfilter.unicode.UnicodeFilter;
import me.trouper.sentinel.server.functions.chatfilter.url.UrlFilter;
import me.trouper.sentinel.server.functions.hotbar.items.ItemCheck;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.startup.drm.Loader;
import me.trouper.sentinel.utils.*;
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
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@CommandRegistry(value = "sentinel", permission = @Permission("sentinel.staff"), printStackTrace = true)
public class SentinelCommand implements QuickCommand {

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
            errorAny(sender, main.lang().plugin.invalidArgs);
        }
    }

    @Override
    public void dispatchCompletions(CommandSender sender, Command command, String s, CompletionBuilder b) {
        b.then(b.arg("socialspy"));
        b.then(b.arg("config"));
        b.then(b.arg("wand"));
        b.then(b.arg("reload"));

        b.then(
                b.arg("false-positive")
                        .then(b.arg("add", "remove"))
        );

        b.then(
                
                b.arg("debug")
                        .then(b.arg("lang", "toggle", "chat"))
                        .then(b.arg("nbt")
                                        .then(b.arg("system","store","filter")))
        );

        b.then(
                b.arg("commandblock", "cb")
                        .then(b.arg("add", "remove", "auto"))
                        .then(
                                b.arg("selection")
                                        .then(b.arg("add", "remove", "delete", "deselect", "pos1", "pos2"))
                        )
                        .then(
                                b.arg("restore")
                                        .then(b.arg("<player>", "all"))
                        )
                        .then(
                                b.arg("clear")
                                        .then(b.arg("<player>", "all"))
                        )
        );
    }

    /* Main Command Processing */
    private void processCommand(CommandSender sender, Command command, String label, Args args) {
        // Lite mode check
        if (main.dir().loader.isLite()) {
            handleLiteMessage(sender, args);
            return;
        }

        if (args.isEmpty()) {
            infoAny(sender, USAGE_SENTINEL);
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
            default -> errorAny(sender, "Invalid sub-command. %s", USAGE_SENTINEL);
        }
    }

    private Player getPlayer(CommandSender sender) {
        if (sender instanceof Player p) {
            return p;
        }
        errorAny(sender, "Only players can execute this command.");
        return null;
    }

    /* =======================
       Subcommand: RELOAD
       ======================= */
    private void handleReload(CommandSender sender) {
        if (!PlayerUtils.isTrusted(sender)) {
            errorAny(sender, main.lang().permissions.noTrust);
            return;
        }
        main.getLogger().info("Sentinel is now reloading the config.");
        infoAny(sender, main.lang().plugin.reloadingConfig);
        main.dir().io.loadConfig();
    }

    /* =======================
       Subcommand: WAND
       ======================= */
    private void handleWand(CommandSender sender) {
        if (PlayerUtils.isConsoleCheck(sender)) return;
        if (!PlayerUtils.isTrusted(sender)) return;
        Player p = (Player) sender;
        p.give(WandEvents.SELECTION_WAND);
        successAny(sender, "Given you a selection wand.");
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
            infoAny(sender, USAGE_COMMANDBLOCK);
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
            default -> errorAny(sender, main.lang().plugin.invalidSubCommand, "commandblock");
        }
    }

    // --- CommandBlock -> SELECTION ---
    private void handleCommandBlockSelection(CommandSender sender, Args args) {
        if (args.getSize() < 3) {
            infoAny(sender, USAGE_SELECTION);
            return;
        }
        Player p = getPlayer(sender);
        if (p == null) return;

        String action = args.get(2).toString().toLowerCase();
        switch (action) {
            case "add" -> main.dir().whitelistManager.addSelectionToWhitelist(p);
            case "remove" -> main.dir().whitelistManager.removeSelectionFromWhitelist(p);
            case "delete" -> main.dir().whitelistManager.deleteSelection(p);
            case "deselect", "desel" -> WandEvents.selections.remove(p.getUniqueId());
            case "pos1" -> {
                Selection selection = WandEvents.selections.computeIfAbsent(p.getUniqueId(), k -> new Selection());
                selection.setPos1(p.getLocation());
                success(p, Component.text("Position 1 set at {0}."), FormatUtils.formatLoc(p.getLocation()));
            }
            case "pos2" -> {
                Selection selection = WandEvents.selections.computeIfAbsent(p.getUniqueId(), k -> new Selection());
                selection.setPos2(p.getLocation());
                success(p, Component.text("Position 2 set at {0}."), FormatUtils.formatLoc(p.getLocation()));
            }
            default -> errorAny(p, "Invalid selection action. %s", USAGE_SELECTION);
        }
    }

    // --- CommandBlock -> ADD ---
    private void handleCommandBlockAdd(CommandSender sender) {
        Player p = getPlayer(sender);
        if (p == null) return;

        if (p.getTargetEntity(10) instanceof CommandMinecart cm) {
            main.dir().whitelistManager
                    .generateHolder(p.getUniqueId(), cm).addAndWhitelist();
            return;
        }
        Block target = p.getTargetBlock(Set.of(Material.AIR), 10);
        if (ServerUtils.isCommandBlock(target)) {
            CommandBlock cb = (CommandBlock) target.getState();
            main.dir().whitelistManager
                    .generateHolder(p.getUniqueId(), cb).addAndWhitelist();
        } else {
            errorAny(sender, main.lang().commandBlock.notCommandBlock, FormatUtils.formatType(target.getType().toString()));
        }
    }

    // --- CommandBlock -> REMOVE ---
    private void handleCommandBlockRemove(CommandSender sender) {
        Player p = getPlayer(sender);
        if (p == null) return;

        if (p.getTargetEntity(10) instanceof CommandMinecart cm) {
            CommandBlockHolder wb = main.dir().whitelistManager
                    .getFromList(cm.getUniqueId());
            if (wb != null) {
                wb.setWhitelisted(false);
                String cleanedType = FormatUtils.formatType(SerialLocation.translate(wb.loc()).getBlock().getType().toString());
                successAny(sender, main.lang().commandBlock.removeSuccess, cleanedType, wb.command());
            } else {
                errorAny(sender, main.lang().commandBlock.notWhitelisted, FormatUtils.formatType(cm.getType().toString()));
            }
            return;
        }

        Block target = p.getTargetBlock(Set.of(Material.AIR), 10);
        CommandBlockHolder wb = main.dir().whitelistManager
                .getFromList(target.getLocation());
        if (wb != null) {
            wb.setWhitelisted(false);
            String cleanedType = FormatUtils.formatType(SerialLocation.translate(wb.loc()).getBlock().getType().toString());
            successAny(sender, main.lang().commandBlock.removeSuccess, cleanedType, wb.command());
        } else {
            errorAny(sender, main.lang().commandBlock.notWhitelisted, FormatUtils.formatType(target.getType().toString()));
        }
    }

    // --- CommandBlock -> AUTO ---
    private void handleCommandBlockAuto(CommandSender sender) {
        Player p = getPlayer(sender);
        if (p == null) return;

        var whitelistManager = main.dir().whitelistManager;
        if (whitelistManager.autoWhitelist.contains(p.getUniqueId())) {
            whitelistManager.autoWhitelist.remove(p.getUniqueId());
            infoAny(sender, main.lang().commandBlock.autoWhitelistOff);
        } else {
            whitelistManager.autoWhitelist.add(p.getUniqueId());
            infoAny(sender, main.lang().commandBlock.autoWhitelistOn);
        }
    }

    // --- CommandBlock -> RESTORE ---
    private void handleCommandBlockRestore(CommandSender sender, Args args) {
        if (args.getSize() < 3) {
            infoAny(sender, USAGE_RESTORE);
            return;
        }
        String targetPlayer = args.get(2).toString();
        if (targetPlayer.equalsIgnoreCase("all")) {
            int result = main.dir().whitelistManager.restoreAll();
            successAny(sender, main.lang().commandBlock.restoreSuccess, result);
        } else {
            UUID id = Bukkit.getOfflinePlayer(targetPlayer).getUniqueId();
            int result = main.dir().whitelistManager.restoreAll(id);
            successAny(sender, main.lang().commandBlock.restorePlayerSuccess, result, targetPlayer);
        }
    }

    // --- CommandBlock -> CLEAR ---
    private void handleCommandBlockClear(CommandSender sender, Args args) {
        if (args.getSize() < 3) {
            infoAny(sender, USAGE_CLEAR);
            return;
        }
        String targetPlayer = args.get(2).toString();
        if (targetPlayer.equalsIgnoreCase("all")) {
            int result = main.dir().whitelistManager.clearAll();
            successAny(sender, main.lang().commandBlock.clearSuccess, result);
        } else {
            UUID id = Bukkit.getOfflinePlayer(targetPlayer).getUniqueId();
            int result = main.dir().whitelistManager.clearAll(id);
            successAny(sender, main.lang().commandBlock.clearPlayerSuccess, result, targetPlayer);
        }
    }

    /* =======================
       Subcommand: DEBUG
       ======================= */
    private void handleDebugCommand(CommandSender sender, Args args) {
        if (!PlayerUtils.checkPermission(sender, "sentinel.debug")) return;
        if (args.getSize() < 2) {
            infoAny(sender, "Usage: /sentinel debug <lang|toggle|chat|nbt>");
            return;
        }
        String sub = args.get(1).toString().toLowerCase();
        switch (sub) {
            case "lang" -> errorAny(sender, main.lang().brokenLang);
            case "toggle" -> {
                main.dir().io.mainConfig.debugMode = !main.dir().io.mainConfig.debugMode;
                String message = main.dir().io.mainConfig.debugMode
                        ? main.lang().debug.debugEnabled
                        : main.lang().debug.debugDisabled;
                infoAny(sender, message);
                main.dir().io.mainConfig.save();
            }
            case "chat" -> handleDebugChat(sender, args);
            case "nbt" -> handleDebugNbt(sender, args);
            default -> errorAny(sender, main.lang().plugin.invalidSubCommand, "debug");
        }
    }
    
    private void handleDebugNbt(CommandSender sender, Args args) {
        if (PlayerUtils.isConsoleCheck(sender)) return;
        if (args.getSize() < 3) {
            infoAny(sender, "Usage: /sentinel debug nbt <filter|store|system>");
            return;
        }
        
        Player p = (Player) sender;
        String sub = args.get(2).toString().toLowerCase();
        switch (sub) {
            case "filter" -> {
                boolean passes = new ItemCheck().passes(p.getInventory().getItemInMainHand());
                if (passes) {
                    success(sender,Component.text("Item passes filter."));
                } else {
                    warning(sender,Component.text("Item flags filter."));
                }
            }
            case "store" -> {
                if (p.getInventory().getItemInMainHand().isEmpty()) {
                    error(sender,Component.text("You must hold the item you wish to store."));
                    return;
                }
                main.dir().io.nbtStorage.storeItem(p.getInventory().getItemInMainHand(),p.getUniqueId());
                success(sender,Component.text("Your item is now visible in the NBT Honeypot."));
            }
            case "system" -> {
                ItemStack i = p.getInventory().getItemInMainHand();
                if (i == null || i.isEmpty()) {
                    error(sender,Component.text("You must hold an item to test it."));
                    return;
                }
                new CreativeHotbar().scan(new InventoryCreativeEvent(p.openInventory(p.getInventory()), InventoryType.SlotType.QUICKBAR,p.getInventory().getHeldItemSlot(),i),p,i);
                p.closeInventory();
                success(sender,Component.text("Scanned your item."));
            }
        }        
    }

    private void handleDebugChat(CommandSender sender, Args args) {
        if (PlayerUtils.isConsoleCheck(sender)) return;
        if (args.getSize() < 3) {
            infoAny(sender, "Usage: /sentinel debug chat <message>");
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
            successAny(sender, main.lang().debug.notFlagged);
        }
    }

    /* =======================
       Subcommand: FALSE-POSITIVE
       ======================= */
    private void handleFalsePositive(CommandSender sender, Args args) {
        if (args.getSize() < 2) {
            infoAny(sender, "Usage: /sentinel false-positive <add|remove> <value>");
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
                main.dir().io.falsePositiveList.swearWhitelist.add(falsePositive);
                successAny(sender, main.lang().falsePositive.addSuccess, falsePositive);
                info.addKeyValue("Action", "Add");
            }
            case "remove" -> {
                if (!PlayerUtils.checkPermission(sender, "sentinel.false-positive.remove")) return;
                main.dir().io.falsePositiveList.swearWhitelist.remove(falsePositive);
                successAny(sender, main.lang().falsePositive.removeSuccess, falsePositive);
                info.addKeyValue("Action", "Remove");
            }
            default -> {
                errorAny(sender, main.lang().plugin.invalidSubCommand, "false-positive");
                return;
            }
        }
        info.addKeyValue("False Positive Edited", falsePositive);
        root.addChild(info);
        main.dir().io.falsePositiveList.save();
        main.getLogger().info(ConsoleFormatter.format(root));
        EmbedFormatter.sendEmbed(EmbedFormatter.format(root));
    }

    /* =======================
       Subcommand: SOCIALSPY
       ======================= */
    private void handleSocialSpy(CommandSender sender) {
        if (PlayerUtils.isConsoleCheck(sender)) return;
        if (!PlayerUtils.checkPermission(sender, "sentinel.socialspy")) return;
        Player p = (Player) sender;
        UUID senderID = p.getUniqueId();
        boolean enabled = spyMap.getOrDefault(senderID, false);
        if (!enabled) {
            infoAny(sender, main.lang().socialSpy.enabled);
            spyMap.put(senderID, true);
        } else {
            infoAny(sender, main.lang().socialSpy.disabled);
            spyMap.put(senderID, false);
        }
    }

    /* =======================
       Lite Mode Handler
       ======================= */
    private void handleLiteMessage(CommandSender sender, Args args) {
        if (!args.isEmpty() && args.get(0).toString().equalsIgnoreCase("reload")) {
            if (!PlayerUtils.isTrusted(sender)) {
                errorAny(sender, main.lang().permissions.noTrust);
                return;
            }
            main.getLogger().info("Sentinel is now reloading the config in lite mode.");
            infoAny(sender, main.lang().plugin.reloadingConfigLite);
            main.dir().io.loadConfig();

            if (main.dir().loader.load(main.getPlugin().license, main.getPlugin().identifier, false)) {
                return;
            }
            main.getLogger().info("Re-authentication Failed.");
        } else {
            warningAny(sender, Loader.LITE_MODE);
        }
    }
}