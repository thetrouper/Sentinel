package me.trouper.sentinel.server.events.violations.blocks.command;

import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.data.types.CommandBlockHolder;
import me.trouper.sentinel.server.events.violations.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.AntiNukeGUI;
import me.trouper.sentinel.utils.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandBlockBreak extends AbstractViolation {

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (!(ServerUtils.isCommandBlock(b))) return;
        ServerUtils.verbose("CommandBlockBreak: Block is a command block");
        Player p = e.getPlayer();
        CommandBlock cb = (CommandBlock) b.getState();
        CommandBlockHolder holder = main.dir().whitelistManager.getFromList(cb.getLocation());
        if (PlayerUtils.isTrusted(e.getPlayer())) {
            if (main.dir().whitelistManager.autoWhitelist.contains(p.getUniqueId())) {
                ServerUtils.verbose("Auto Whitelist is on, un-whitelisting the command block.");
                holder.setWhitelisted(false);
                holder.delete();
            }
            return;
        }

        if (!main.dir().io.violationConfig.commandBlockBreak.enabled) {
            ServerUtils.verbose("Not enabled, deletion allowed.");
            if (!holder.isWhitelisted()) holder.delete();
            return;
        }

        ServerUtils.verbose("CommandBlockBreak: is enabled, performing action");

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setPlayer(p)
                .deop(main.dir().io.violationConfig.commandBlockBreak.deop)
                .cancel(true)
                .punish(main.dir().io.violationConfig.commandBlockBreak.punish)
                .setPunishmentCommands(main.dir().io.violationConfig.commandBlockBreak.punishmentCommands)
                .logToDiscord(main.dir().io.violationConfig.commandBlockBreak.logToDiscord);
        
        runActions(
                Text.format(Text.Pallet.WARNING,main.dir().io.lang.violations.protections.rootName.rootNameFormatPlayer,p.getName(), main.dir().io.lang.violations.protections.rootName.brake, main.dir().io.lang.violations.protections.rootName.commandBlock),
                generateCommandBlockInfo(cb),
                config
        );
    }

    @Override
    public CustomGui getConfigGui() {
        return CustomGui.create()
                .title(OldTXT.color("&6&lSentinel &8»&0 Command Block Break"))
                .size(27)
                .onDefine(this::getMainPage)
                .defineMain(this::onClick)
                .define(26, Items.BACK, e->{
                    e.getWhoClicked().openInventory(new AntiNukeGUI().home.getInventory());
                })
                .build();
    }

    @Override
    public void getMainPage(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i,Items.BLANK);
        }

        ItemStack ring = Items.RED;
        if (main.dir().io.violationConfig.commandBlockBreak.enabled) {
            ring = Items.GREEN;
        }

        List<Integer> ringList = List.of(3,4,5,12,14,21,22,23);

        for (Integer i : ringList) {
            inv.setItem(i,ring);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(13,Items.booleanItem(main.dir().io.violationConfig.commandBlockBreak.enabled,Items.configItem("Check Toggle",Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(2,Items.booleanItem(main.dir().io.violationConfig.commandBlockBreak.deop,Items.configItem("De-Op",Material.END_CRYSTAL,"Remove the user's operator privileges")));
        inv.setItem(20,Items.booleanItem(main.dir().io.violationConfig.commandBlockBreak.logToDiscord,Items.configItem("Log",Material.OAK_LOG,"If this check will produce a log to discord")));
        inv.setItem(6,Items.booleanItem(main.dir().io.violationConfig.commandBlockBreak.punish,Items.configItem("Punish",Material.REDSTONE_TORCH,"Run the punishment commands")));
        inv.setItem(24,Items.stringListItem(main.dir().io.violationConfig.commandBlockBreak.punishmentCommands,Material.DIAMOND_AXE,"Punishment Commands","Commands that will be ran \nif this check is flagged."));
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 13 -> {
                main.dir().io.violationConfig.commandBlockBreak.enabled = !main.dir().io.violationConfig.commandBlockBreak.enabled;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 2 -> {
                main.dir().io.violationConfig.commandBlockBreak.deop = !main.dir().io.violationConfig.commandBlockBreak.deop;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 20 -> {
                main.dir().io.violationConfig.commandBlockBreak.logToDiscord = !main.dir().io.violationConfig.commandBlockBreak.logToDiscord;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 6 -> {
                main.dir().io.violationConfig.commandBlockBreak.punish = !main.dir().io.violationConfig.commandBlockBreak.punish;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }

            case 24 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg, args) -> {
                        cfg.commandBlockBreak.punishmentCommands.add(args.getAll().toString());
                    },"" + main.dir().io.violationConfig.commandBlockBreak.punishmentCommands);
                    return;
                }
                main.dir().io.violationConfig.commandBlockBreak.punishmentCommands.clear();
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }

        }
    }
}
