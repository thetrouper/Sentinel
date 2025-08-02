package me.trouper.sentinel.server.events.violations.blocks.structure;

import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.Sentinel;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class StructureBlockUse extends AbstractViolation {
    @EventHandler
    public void onPlace(PlayerInteractEvent e) {
        if (!main.dir().io.violationConfig.commandBlockPlace.enabled) return;
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        if (b == null) return;
        if (!Material.STRUCTURE_BLOCK.equals(b.getType())) return;
        ServerUtils.verbose("StructureBlockUse: Block is a Structure block");
        if (PlayerUtils.isTrusted(p)) return;
        
        ServerUtils.verbose("StructureBlockUse: Not trusted, performing action");


        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setPlayer(p)
                .deop(main.dir().io.violationConfig.structureBlockUse.deop)
                .cancel(true)
                .setEvent(e)
                .punish(main.dir().io.violationConfig.structureBlockUse.punish)
                .setPunishmentCommands(main.dir().io.violationConfig.structureBlockUse.punishmentCommands)
                .logToDiscord(main.dir().io.violationConfig.structureBlockUse.logToDiscord);

        runActions(
                Text.format(Text.Pallet.WARNING,main.dir().io.lang.violations.protections.rootName.rootNameFormatPlayer,p.getName(), main.dir().io.lang.violations.protections.rootName.use, main.dir().io.lang.violations.protections.rootName.structureBlock),
                generateBlockInfo(b),
                config
        );
    }

    @Override
    public CustomGui getConfigGui() {
        return CustomGui.create()
                .title(OldTXT.color("&6&lSentinel &8»&0 Structure Block Use"))
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
        if (main.dir().io.violationConfig.structureBlockUse.enabled) {
            ring = Items.GREEN;
        }

        List<Integer> ringList = List.of(3,4,5,12,14,21,22,23);

        for (Integer i : ringList) {
            inv.setItem(i,ring);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(13,Items.booleanItem(main.dir().io.violationConfig.structureBlockUse.enabled,Items.configItem("Check Toggle",Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(2,Items.booleanItem(main.dir().io.violationConfig.structureBlockUse.deop,Items.configItem("De-Op",Material.END_CRYSTAL,"Remove the user's operator privileges")));
        inv.setItem(20,Items.booleanItem(main.dir().io.violationConfig.structureBlockUse.logToDiscord,Items.configItem("Log",Material.OAK_LOG,"If this check will produce a log to discord")));
        inv.setItem(6,Items.booleanItem(main.dir().io.violationConfig.structureBlockUse.punish,Items.configItem("Punish",Material.REDSTONE_TORCH,"Run the punishment commands")));
        inv.setItem(24,Items.stringListItem(main.dir().io.violationConfig.structureBlockUse.punishmentCommands,Material.DIAMOND_AXE,"Punishment Commands","Commands that will be ran \nif this check is flagged."));
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 13 -> {
                main.dir().io.violationConfig.structureBlockUse.enabled = !main.dir().io.violationConfig.structureBlockUse.enabled;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 2 -> {
                main.dir().io.violationConfig.structureBlockUse.deop = !main.dir().io.violationConfig.structureBlockUse.deop;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 20 -> {
                main.dir().io.violationConfig.structureBlockUse.logToDiscord = !main.dir().io.violationConfig.structureBlockUse.logToDiscord;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 6 -> {
                main.dir().io.violationConfig.structureBlockUse.punish = !main.dir().io.violationConfig.structureBlockUse.punish;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }

            case 24 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg, args) -> {
                        cfg.structureBlockUse.punishmentCommands.add(args.getAll().toString());
                    },"" + main.dir().io.violationConfig.structureBlockUse.punishmentCommands);
                    return;
                }
                main.dir().io.violationConfig.structureBlockUse.punishmentCommands.clear();
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
        }
    }
}
