package me.trouper.sentinel.server.events.violations.whitelist;

import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.CommandBlockHolder;
import me.trouper.sentinel.server.events.violations.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.AntiNukeGUI;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CommandBlockExecute extends AbstractViolation {

    @EventHandler
    private void commandBlockExecute(ServerCommandEvent e) {
        //ServerUtils.verbose("Handling command block event: " + e.getCommand());
        if (!Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.enabled) return;
        //ServerUtils.verbose("Whitelist not disabled");
        if (!(e.getSender() instanceof BlockCommandSender s)) return;
        //ServerUtils.verbose("Sender is command block");

        Block block = s.getBlock();
        CommandBlock cb = (CommandBlock) block.getState();
        CommandBlockHolder holder = Sentinel.getInstance().getDirector().whitelistManager.getFromList(cb.getLocation());

        String label = cb.getCommand();
        ServerUtils.verbose("Command block is set to %s.".formatted(label));
        label = label.split(" ")[0];
        if (label.startsWith("/")) label = label.substring(1);
        ServerUtils.verbose("It's label is %s.".formatted(label));

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setBlock(block)
                .cancel(true)
                .destroyBlock(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.destroyBlock)
                .restoreBlock(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.attemptRestore)
                .logToDiscord(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.logToDiscord);
       
        if (Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.disabledCommands.contains(label)) {
            ServerUtils.verbose("Command block is using a restricted command.");

            runActions(
                    Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormat.formatted(Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.commandBlockRestriction),
                    Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormat.formatted( Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.commandBlockRestriction),
                    generateCommandBlockInfo(cb),
                    config
            );
        } else if (holder == null || !holder.isWhitelisted() || !holder.present()|| !PlayerUtils.isTrusted(UUID.fromString(holder.owner()))) {
            ServerUtils.verbose("Command block can't run. Not whitelisted and/or trusted.");

            runActions(
                    Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormat.formatted(Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.commandBlockWhitelist),
                    Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormat.formatted(Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.commandBlockWhitelist),
                    generateCommandBlockInfo(cb),
                    config
            );
        }
    }


    @Override
    public CustomGui getConfigGui() {
        return CustomGui.create()
                .title(Text.color("&6&lSentinel &8»&0 Command Block Whitelist"))
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

        ItemStack top = Items.RED;
        if (Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.enabled) {
            top = Items.GREEN;
        }

        for (int i = 0; i < 9; i++) {
            inv.setItem(i,top);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(4,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.enabled,Items.configItem("Check Toggle", Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(11,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.destroyBlock,Items.configItem("Destroy",Material.NETHERITE_PICKAXE,"Destroy the offending command-block")));
        inv.setItem(12,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.destroyCart,Items.configItem("Destroy",Material.TNT_MINECART,"Destroy the offending command-cart")));
        inv.setItem(13,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.attemptRestore,Items.configItem("Restore",Material.COMMAND_BLOCK,"Attempt to restore the block if a \nwhitelisted one exists at the location")));
        inv.setItem(14,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.logToDiscord,Items.configItem("Log",Material.OAK_LOG,"If this check will produce a log to discord")));
        inv.setItem(15,Items.stringListItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.disabledCommands,Material.CRIMSON_HANGING_SIGN,"Commands","Commands no command blocks can run. \nWorks even if whitelist is disabled. \nYou must add plugin specific versions yourself."));
    }
    
    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 4 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.enabled = !Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.enabled;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 11 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.destroyBlock = !Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.destroyBlock;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 12 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.destroyCart = !Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.destroyCart;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 13 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.attemptRestore = !Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.attemptRestore;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 14 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.logToDiscord = !Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.logToDiscord;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 15 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> {
                        cfg.commandBlockWhitelist.disabledCommands.add(args.getAll().toString());
                    },"" + Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.disabledCommands);
                    return;
                }
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.disabledCommands.clear();
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
        }
    }
}