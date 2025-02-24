package me.trouper.sentinel.server.functions.helpers;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.FileUtils;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import me.trouper.sentinel.utils.trees.ConsoleFormatter;
import me.trouper.sentinel.utils.trees.EmbedFormatter;
import me.trouper.sentinel.utils.trees.HoverFormatter;
import me.trouper.sentinel.utils.trees.Node;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractViolation implements CustomListener {

    public void runActions(String rootName, String rootNamePlayer, Node violationInfo, ActionConfiguration.Builder configuration) {
        ActionConfiguration config = configuration.build();

        Node root = new Node("Sentinel");
        root.addTextLine(rootName);

        if (config.getPlayer() != null) root.addChild(generatePlayerInfo(config.getPlayer()));

        root.addChild(violationInfo);

        root.addChild(configuration.getActionNode());

        notifyTrusted(root,(rootNamePlayer == null || rootNamePlayer.isBlank()) ? rootName : rootNamePlayer);
        if (configuration.isLoggedToDiscord()) EmbedFormatter.sendEmbed(EmbedFormatter.format(root));
        Sentinel.log.info(ConsoleFormatter.format(root));
    }

    public void notifyTrusted(Node root, String rootNamePlayer) {
        ServerUtils.forEachPlayer(trusted -> {
            if (PlayerUtils.isTrusted(trusted)) {
                trusted.sendMessage(Component.text(Text.prefix(rootNamePlayer)).hoverEvent(Component.text(HoverFormatter.format(root)).asHoverEvent()));
            }
        });
    }

    public Node generatePlayerInfo(Player p) {
        Node playerInfo = new Node(Sentinel.lang.violations.protections.infoNode.playerInfo);
        playerInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.name, p.getName());
        playerInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.uuid, p.getUniqueId().toString());
        playerInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.operator, p.isOp() ? Sentinel.lang.generic.yes : Sentinel.lang.generic.no);
        playerInfo.addField(Sentinel.lang.violations.protections.infoNode.locationField, Sentinel.lang.violations.protections.infoNode.locationFormat.formatted(Math.round(p.getX()), Math.round(p.getY()), Math.round(p.getZ())));

        return playerInfo;
    }

    public static Node generateBlockInfo(Block block) {
        Node blockInfo = new Node(Sentinel.lang.violations.protections.infoNode.blockInfo);
        blockInfo.addTextLine(Text.cleanName(block.getType().toString()));
        blockInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.worldField,block.getWorld().getName());
        blockInfo.addField(Sentinel.lang.violations.protections.infoNode.blockLocationField,Sentinel.lang.violations.protections.infoNode.locationFormat.formatted(block.getX(), block.getY(), block.getZ()));

        return blockInfo;
    }

    public Node generateCommandBlockInfo(CommandBlock commandBlock) {
        Node commandBlockInfo = new Node(Sentinel.lang.violations.protections.infoNode.blockInfo);
        commandBlockInfo.addTextLine(Text.cleanName(commandBlock.getType().toString()));
        commandBlockInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.worldField,commandBlock.getWorld().getName());
        commandBlockInfo.addField(Sentinel.lang.violations.protections.infoNode.blockLocationField,Sentinel.lang.violations.protections.infoNode.locationFormat.formatted(commandBlock.getX(), commandBlock.getY(), commandBlock.getZ()));

        String command = commandBlock.getCommand();
        if (command == null || command.isBlank()) {
            return commandBlockInfo;
        } else if (command.length() <= 128) {
            commandBlockInfo.addField(Sentinel.lang.violations.protections.infoNode.commandField, command);
        } else {
            commandBlockInfo.addField(Sentinel.lang.violations.protections.infoNode.commandTooLargeField, FileUtils.createCommandLog(command));
        }

        return commandBlockInfo;
    }

    public Node generateMinecartInfo(Entity entity) {
        Node minecartInfo = new Node(Sentinel.lang.violations.protections.infoNode.minecartInfo);
        minecartInfo.addTextLine(Text.cleanName(entity.getType().toString()));
        minecartInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.worldField,entity.getWorld().getName());
        minecartInfo.addField(Sentinel.lang.violations.protections.infoNode.cartLocationField,Sentinel.lang.violations.protections.infoNode.locationFormat.formatted(Math.round(entity.getX()), Math.round(entity.getY()), Math.round(entity.getZ())));

        return minecartInfo;
    }

    public Node generateItemInfo(ItemStack item) {
        Node itemInfo = new Node(Sentinel.lang.violations.protections.infoNode.itemInfo);
        itemInfo.addTextLine(Text.cleanName(item.getType().toString()));
        itemInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.hasMeta,item.hasItemMeta() ? Sentinel.lang.generic.yes : Sentinel.lang.generic.no);
        if (item.hasItemMeta()) {
            itemInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.hasName,item.getItemMeta().hasCustomName() ? Sentinel.lang.generic.yes : Sentinel.lang.generic.no);
            itemInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.hasLore,item.getItemMeta().hasLore() ? Sentinel.lang.generic.yes : Sentinel.lang.generic.no);
            itemInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.hasAttributes,item.getItemMeta().hasAttributeModifiers() ? Sentinel.lang.generic.yes : Sentinel.lang.generic.no);
            itemInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.hasEnchants,item.getItemMeta().hasEnchants() ? Sentinel.lang.generic.yes : Sentinel.lang.generic.no);
            itemInfo.addField(Sentinel.lang.violations.protections.infoNode.nbtStored, FileUtils.createNBTLog(item));
        }

        return itemInfo;
    }

    public Node generateCommandInfo(String command, Player executor) {
        Node commandInfo = new Node(Sentinel.lang.violations.protections.infoNode.commandInfo);
        String name = command.split(" ")[0].substring(1);
        ServerUtils.verbose("Command Name: " + name);
        Command executed = Bukkit.getServer().getCommandMap().getCommand(name);

        commandInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.name,name);
        if (command.length() <= 128) {
            commandInfo.addField(Sentinel.lang.violations.protections.infoNode.commandField, command);
        } else {
            commandInfo.addField(Sentinel.lang.violations.protections.infoNode.commandTooLargeField, FileUtils.createCommandLog(command));
        }
        if (executed == null || executed.getPermission() == null) return commandInfo;
        commandInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.permissionRequired,executed.getPermission());
        commandInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.permissionSatisfied,executor.hasPermission(executed.getPermission()) ? Sentinel.lang.generic.yes : Sentinel.lang.generic.no);

        return commandInfo;
    }
}
