package me.trouper.sentinel.server.events.violations;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.config.ConfigUpdater;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.ViolationConfig;
import me.trouper.sentinel.server.events.QuickListener;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.utils.*;
import me.trouper.sentinel.utils.trees.ConsoleFormatter;
import me.trouper.sentinel.utils.trees.EmbedFormatter;
import me.trouper.sentinel.utils.trees.HoverFormatter;
import me.trouper.sentinel.utils.trees.Node;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.Format;
import java.util.function.BiConsumer;

public abstract class AbstractViolation implements QuickListener {

    public abstract CustomGui getConfigGui();
    public abstract void getMainPage(Inventory inv);
    public abstract void onClick(InventoryClickEvent e);
    
    public static ConfigUpdater<AsyncChatEvent, ViolationConfig> updater = new ConfigUpdater<>(main.dir().io.violationConfig);

    protected void queuePlayer(Player player, BiConsumer<ViolationConfig, Args> action, String currentValue) {
        MainGUI.awaitingCallback.add(player.getUniqueId());
        player.closeInventory();
        updater.queuePlayer(player, 20*60, (e)->{
            e.setCancelled(true);
            return LegacyComponentSerializer.legacySection().serialize(e.message());
        }, (cfg, newValue) -> {
            action.accept(cfg,new Args(newValue.split("\\s+")));
            cfg.save();
            successAny(player,"Value updated successfully");
            player.openInventory(getConfigGui().getInventory());
        });
        message(player,Component.text("Enter the new value in chat. The value is currently set to {0}. (Click to insert)").clickEvent(ClickEvent.suggestCommand(currentValue)),Component.text(currentValue));
    }

    public void runActions(Component rootName, Node violationInfo, ActionConfiguration.Builder configuration) {
        ActionConfiguration config = configuration.build();

        Node root = new Node("Sentinel");
        root.addTextLine(rootName);

        if (config.getPlayer() != null) root.addChild(generatePlayerInfo(config.getPlayer()));

        root.addChild(violationInfo);

        root.addChild(configuration.getActionNode());

        notifyTrusted(root, rootName);
        if (configuration.isLoggedToDiscord()) EmbedFormatter.sendEmbed(EmbedFormatter.format(root));
        Sentinel.getInstance().getLogger().info(ConsoleFormatter.format(root));
    }

    public void notifyTrusted(Node root, Component rootNamePlayer) {
        PlayerUtils.forEachTrusted(trusted -> {
                message(trusted,rootNamePlayer.hoverEvent(HoverFormatter.format(root).asHoverEvent()));
        });
    }

    public Node generatePlayerInfo(Player p) {
        Node playerInfo = new Node(main.dir().io.lang.violations.protections.infoNode.playerInfo);
        playerInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.name, p.getName());
        playerInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.uuid, p.getUniqueId().toString());
        playerInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.operator, p.isOp() ? main.dir().io.lang.generic.yes : main.dir().io.lang.generic.no);
        playerInfo.addField(Component.text(main.dir().io.lang.violations.protections.infoNode.locationField), FormatUtils.formatLoc(p.getLocation()));

        return playerInfo;
    }

    public static Node generateBlockInfo(Block block) {
        Node blockInfo = new Node(main.dir().io.lang.violations.protections.infoNode.blockInfo);
        blockInfo.addTextLine(FormatUtils.formatType(block.getType().toString()));
        blockInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.worldField,block.getWorld().getName());
        blockInfo.addField(Component.text(main.dir().io.lang.violations.protections.infoNode.blockLocationField),FormatUtils.formatLoc(block.getLocation()));

        return blockInfo;
    }

    public Node generateCommandBlockInfo(CommandBlock commandBlock) {
        Node commandBlockInfo = new Node(main.dir().io.lang.violations.protections.infoNode.blockInfo);
        commandBlockInfo.addTextLine(FormatUtils.formatType(commandBlock.getType().toString()));
        commandBlockInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.worldField,commandBlock.getWorld().getName());
        commandBlockInfo.addField(Component.text(main.dir().io.lang.violations.protections.infoNode.blockLocationField), FormatUtils.formatLoc(commandBlock.getLocation()));

        String command = commandBlock.getCommand();
        if (command == null || command.isBlank()) {
            return commandBlockInfo;
        } else if (command.length() <= 128) {
            commandBlockInfo.addField(main.dir().io.lang.violations.protections.infoNode.commandField, command);
        } else {
            commandBlockInfo.addField(main.dir().io.lang.violations.protections.infoNode.commandTooLargeField, FileUtils.createCommandLog(command));
        }

        return commandBlockInfo;
    }

    public Node generateMinecartInfo(CommandMinecart entity) {
        Node minecartInfo = new Node(main.dir().io.lang.violations.protections.infoNode.minecartInfo);
        minecartInfo.addTextLine(FormatUtils.formatType(entity.getType().toString()));
        minecartInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.worldField,entity.getWorld().getName());
        minecartInfo.addField(Component.text(main.dir().io.lang.violations.protections.infoNode.cartLocationField),FormatUtils.formatLoc(entity.getLocation()));

        String command = entity.getCommand();
        if (command == null || command.isBlank()) {
            return minecartInfo;
        } else if (command.length() <= 128) {
            minecartInfo.addField(main.dir().io.lang.violations.protections.infoNode.commandField, command);
        } else {
            minecartInfo.addField(main.dir().io.lang.violations.protections.infoNode.commandTooLargeField, FileUtils.createCommandLog(command));
        }

        return minecartInfo;
    }

    public Node generateItemInfo(ItemStack item) {
        Node itemInfo = new Node(main.dir().io.lang.violations.protections.infoNode.itemInfo);
        itemInfo.addTextLine(FormatUtils.formatType(item.getType().toString()));
        itemInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.hasMeta,item.hasItemMeta() ? main.dir().io.lang.generic.yes : main.dir().io.lang.generic.no);
        if (item.hasItemMeta()) {
            itemInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.hasName,item.getItemMeta().hasCustomName() ? main.dir().io.lang.generic.yes : main.dir().io.lang.generic.no);
            itemInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.hasLore,item.getItemMeta().hasLore() ? main.dir().io.lang.generic.yes : main.dir().io.lang.generic.no);
            itemInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.hasAttributes,item.getItemMeta().hasAttributeModifiers() ? main.dir().io.lang.generic.yes : main.dir().io.lang.generic.no);
            itemInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.hasEnchants,item.getItemMeta().hasEnchants() ? main.dir().io.lang.generic.yes : main.dir().io.lang.generic.no);
        }

        return itemInfo;
    }

    public Node generateCommandInfo(String command, Player executor) {
        Node commandInfo = new Node(main.dir().io.lang.violations.protections.infoNode.commandInfo);
        String name = command.split(" ")[0].substring(1);
        ServerUtils.verbose("Command Name: " + name);
        Command executed = Bukkit.getServer().getCommandMap().getCommand(name);

        commandInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.name,name);
        if (command.length() <= 128) {
            commandInfo.addField(main.dir().io.lang.violations.protections.infoNode.commandField, command);
        } else {
            commandInfo.addField(main.dir().io.lang.violations.protections.infoNode.commandTooLargeField, FileUtils.createCommandLog(command));
        }
        if (executed == null || executed.getPermission() == null) return commandInfo;
        commandInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.permissionRequired,executed.getPermission());
        commandInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.permissionSatisfied,executor.hasPermission(executed.getPermission()) ? main.dir().io.lang.generic.yes : main.dir().io.lang.generic.no);

        return commandInfo;
    }
}
