package me.trouper.sentinel.server.events.violations;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.events.CustomListener;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.config.ConfigUpdater;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.ViolationConfig;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.utils.FileUtils;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
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

import java.util.function.BiConsumer;

public abstract class AbstractViolation implements CustomListener {

    public abstract CustomGui getConfigGui();
    public abstract void getMainPage(Inventory inv);
    public abstract void onClick(InventoryClickEvent e);
    
    public static ConfigUpdater<AsyncChatEvent, ViolationConfig> updater = new ConfigUpdater<>(Sentinel.getInstance().getDirector().io.violationConfig);

    protected void queuePlayer(Player player, BiConsumer<ViolationConfig, Args> action, String currentValue) {
        MainGUI.awaitingCallback.add(player.getUniqueId());
        player.closeInventory();
        updater.queuePlayer(player, 20*60, (e)->{
            e.setCancelled(true);
            return LegacyComponentSerializer.legacySection().serialize(e.message());
        }, (cfg, newValue) -> {
            action.accept(cfg,new Args(newValue.split("\\s+")));
            cfg.save();
            player.sendMessage(Text.prefix("Value updated successfully"));
            player.openInventory(getConfigGui().getInventory());
        });
        player.sendMessage(Component.text(Text.prefix("Enter the new value in chat. The value is currently set to &b%s&7. (Click to insert)".formatted(currentValue))).clickEvent(ClickEvent.suggestCommand(currentValue)));
    }

    public void runActions(String rootName, String rootNamePlayer, Node violationInfo, ActionConfiguration.Builder configuration) {
        ActionConfiguration config = configuration.build();

        Node root = new Node("Sentinel");
        root.addTextLine(rootName);

        if (config.getPlayer() != null) root.addChild(generatePlayerInfo(config.getPlayer()));

        root.addChild(violationInfo);

        root.addChild(configuration.getActionNode());

        notifyTrusted(root,(rootNamePlayer == null || rootNamePlayer.isBlank()) ? rootName : rootNamePlayer);
        if (configuration.isLoggedToDiscord()) EmbedFormatter.sendEmbed(EmbedFormatter.format(root));
        Sentinel.getInstance().getLogger().info(ConsoleFormatter.format(root));
    }

    public void notifyTrusted(Node root, String rootNamePlayer) {
        PlayerUtils.forEachTrusted(trusted -> {
                trusted.sendMessage(Component.text(Text.prefix(rootNamePlayer)).hoverEvent(Component.text(HoverFormatter.format(root)).asHoverEvent()));
        });
    }

    public Node generatePlayerInfo(Player p) {
        Node playerInfo = new Node(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.playerInfo);
        playerInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.name, p.getName());
        playerInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.uuid, p.getUniqueId().toString());
        playerInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.operator, p.isOp() ? Sentinel.getInstance().getDirector().io.lang.generic.yes : Sentinel.getInstance().getDirector().io.lang.generic.no);
        playerInfo.addField(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.locationField, Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.locationFormat.formatted(Math.round(p.getX()), Math.round(p.getY()), Math.round(p.getZ())));

        return playerInfo;
    }

    public static Node generateBlockInfo(Block block) {
        Node blockInfo = new Node(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.blockInfo);
        blockInfo.addTextLine(Text.cleanName(block.getType().toString()));
        blockInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.worldField,block.getWorld().getName());
        blockInfo.addField(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.blockLocationField,Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.locationFormat.formatted(block.getX(), block.getY(), block.getZ()));

        return blockInfo;
    }

    public Node generateCommandBlockInfo(CommandBlock commandBlock) {
        Node commandBlockInfo = new Node(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.blockInfo);
        commandBlockInfo.addTextLine(Text.cleanName(commandBlock.getType().toString()));
        commandBlockInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.worldField,commandBlock.getWorld().getName());
        commandBlockInfo.addField(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.blockLocationField,Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.locationFormat.formatted(commandBlock.getX(), commandBlock.getY(), commandBlock.getZ()));

        String command = commandBlock.getCommand();
        if (command == null || command.isBlank()) {
            return commandBlockInfo;
        } else if (command.length() <= 128) {
            commandBlockInfo.addField(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.commandField, command);
        } else {
            commandBlockInfo.addField(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.commandTooLargeField, FileUtils.createCommandLog(command));
        }

        return commandBlockInfo;
    }

    public Node generateMinecartInfo(CommandMinecart entity) {
        Node minecartInfo = new Node(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.minecartInfo);
        minecartInfo.addTextLine(Text.cleanName(entity.getType().toString()));
        minecartInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.worldField,entity.getWorld().getName());
        minecartInfo.addField(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.cartLocationField,Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.locationFormat.formatted(Math.round(entity.getX()), Math.round(entity.getY()), Math.round(entity.getZ())));

        String command = entity.getCommand();
        if (command == null || command.isBlank()) {
            return minecartInfo;
        } else if (command.length() <= 128) {
            minecartInfo.addField(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.commandField, command);
        } else {
            minecartInfo.addField(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.commandTooLargeField, FileUtils.createCommandLog(command));
        }

        return minecartInfo;
    }

    public Node generateItemInfo(ItemStack item) {
        Node itemInfo = new Node(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.itemInfo);
        itemInfo.addTextLine(Text.cleanName(item.getType().toString()));
        itemInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.hasMeta,item.hasItemMeta() ? Sentinel.getInstance().getDirector().io.lang.generic.yes : Sentinel.getInstance().getDirector().io.lang.generic.no);
        if (item.hasItemMeta()) {
            itemInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.hasName,item.getItemMeta().hasCustomName() ? Sentinel.getInstance().getDirector().io.lang.generic.yes : Sentinel.getInstance().getDirector().io.lang.generic.no);
            itemInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.hasLore,item.getItemMeta().hasLore() ? Sentinel.getInstance().getDirector().io.lang.generic.yes : Sentinel.getInstance().getDirector().io.lang.generic.no);
            itemInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.hasAttributes,item.getItemMeta().hasAttributeModifiers() ? Sentinel.getInstance().getDirector().io.lang.generic.yes : Sentinel.getInstance().getDirector().io.lang.generic.no);
            itemInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.hasEnchants,item.getItemMeta().hasEnchants() ? Sentinel.getInstance().getDirector().io.lang.generic.yes : Sentinel.getInstance().getDirector().io.lang.generic.no);
        }

        return itemInfo;
    }

    public Node generateCommandInfo(String command, Player executor) {
        Node commandInfo = new Node(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.commandInfo);
        String name = command.split(" ")[0].substring(1);
        ServerUtils.verbose("Command Name: " + name);
        Command executed = Bukkit.getServer().getCommandMap().getCommand(name);

        commandInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.name,name);
        if (command.length() <= 128) {
            commandInfo.addField(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.commandField, command);
        } else {
            commandInfo.addField(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.commandTooLargeField, FileUtils.createCommandLog(command));
        }
        if (executed == null || executed.getPermission() == null) return commandInfo;
        commandInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.permissionRequired,executed.getPermission());
        commandInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.permissionSatisfied,executor.hasPermission(executed.getPermission()) ? Sentinel.getInstance().getDirector().io.lang.generic.yes : Sentinel.getInstance().getDirector().io.lang.generic.no);

        return commandInfo;
    }
}
