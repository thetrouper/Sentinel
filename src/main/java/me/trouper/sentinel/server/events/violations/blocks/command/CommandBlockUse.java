package me.trouper.sentinel.server.events.violations.blocks.command;

import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.data.types.CommandBlockHolder;
import me.trouper.sentinel.server.events.violations.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.AntiNukeGUI;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.OldTXT;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandBlockUse extends AbstractViolation {

    @EventHandler
    private void onCMDBlockUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getClickedBlock() == null) return;
        //ServerUtils.verbose("CommandBlockUse: Block isn't null");
        Block b = e.getClickedBlock();
        if (!(ServerUtils.isCommandBlock(b))) return;
        CommandBlock cb = (CommandBlock) b.getState();
        CommandBlockHolder holder = main.dir().whitelistManager.getFromList(cb.getLocation());
        if (holder == null) {
            holder = main.dir().whitelistManager.generateHolder(p.getUniqueId(),cb);
            holder.add();
        }
        if (PlayerUtils.isTrusted(p)) {
            if (main.dir().whitelistManager.autoWhitelist.contains(p.getUniqueId())) holder.setWhitelisted(true);
            
            holder.update(p);
            return;
        }

        if (!main.dir().io.violationConfig.commandBlockUse.enabled) {
            holder.update(p);
            return;
        }

        ServerUtils.verbose("CommandBlockUse: Not trusted, performing action");

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setPlayer(p)
                .deop(main.dir().io.violationConfig.commandBlockUse.deop)
                .cancel(true)
                .punish(main.dir().io.violationConfig.commandBlockUse.punish)
                .setPunishmentCommands(main.dir().io.violationConfig.commandBlockUse.punishmentCommands)
                .logToDiscord(main.dir().io.violationConfig.commandBlockUse.logToDiscord);

        runActions(
                Text.format(Text.Pallet.WARNING,main.dir().io.lang.violations.protections.rootName.rootNameFormatPlayer,p.getName(), main.dir().io.lang.violations.protections.rootName.use, main.dir().io.lang.violations.protections.rootName.commandBlock),
                generateCommandBlockInfo(cb),
                config
        );
    }

    @Override
    public CustomGui getConfigGui() {
        return CustomGui.create()
                .title(OldTXT.color("&6&lSentinel &8»&0 Command Block Use"))
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
        if (main.dir().io.violationConfig.commandBlockUse.enabled) {
            ring = Items.GREEN;
        }

        List<Integer> ringList = List.of(3,4,5,12,14,21,22,23);

        for (Integer i : ringList) {
            inv.setItem(i,ring);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(13,Items.booleanItem(main.dir().io.violationConfig.commandBlockUse.enabled,Items.configItem("Check Toggle", Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(2,Items.booleanItem(main.dir().io.violationConfig.commandBlockUse.deop,Items.configItem("De-Op",Material.END_CRYSTAL,"Remove the user's operator privileges")));
        inv.setItem(20,Items.booleanItem(main.dir().io.violationConfig.commandBlockUse.logToDiscord,Items.configItem("Log",Material.OAK_LOG,"If this check will produce a log to discord")));
        inv.setItem(6,Items.booleanItem(main.dir().io.violationConfig.commandBlockUse.punish,Items.configItem("Punish",Material.REDSTONE_TORCH,"Run the punishment commands")));
        inv.setItem(24,Items.stringListItem(main.dir().io.violationConfig.commandBlockUse.punishmentCommands,Material.DIAMOND_AXE,"Punishment Commands","Commands that will be ran \nif this check is flagged."));
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 13 -> {
                main.dir().io.violationConfig.commandBlockUse.enabled = !main.dir().io.violationConfig.commandBlockUse.enabled;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 2 -> {
                main.dir().io.violationConfig.commandBlockUse.deop = !main.dir().io.violationConfig.commandBlockUse.deop;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 20 -> {
                main.dir().io.violationConfig.commandBlockUse.logToDiscord = !main.dir().io.violationConfig.commandBlockUse.logToDiscord;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 6 -> {
                main.dir().io.violationConfig.commandBlockUse.punish = !main.dir().io.violationConfig.commandBlockUse.punish;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }

            case 24 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg, args) -> {
                        cfg.commandBlockUse.punishmentCommands.add(args.getAll().toString());
                    },"" + main.dir().io.violationConfig.commandBlockUse.punishmentCommands);
                    return;
                }
                main.dir().io.violationConfig.commandBlockUse.punishmentCommands.clear();
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }

        }
    }
}