package me.trouper.sentinel.server.gui.config.nuke.checks;

import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.AntiNukeGUI;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CBExecuteGUI {
    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 CB Whitelist"))
            .size(27)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define(26, Items.BACK, e->{
                e.getWhoClicked().openInventory(new AntiNukeGUI().home.getInventory());
            })
            .build();

    private void blankPage(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i,Items.BLANK);
        }

        ItemStack top = Items.RED;
        if (Sentinel.violationConfig.commandBlockExecute.enabled) {
            top = Items.GREEN;
        }

        for (int i = 0; i < 9; i++) {
            inv.setItem(i,top);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(4,Items.booleanItem(Sentinel.violationConfig.commandBlockExecute.enabled,Items.configItem("Check Toggle", Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(11,Items.booleanItem(Sentinel.violationConfig.commandBlockExecute.destroyBlock,Items.configItem("Destroy",Material.NETHERITE_PICKAXE,"Destroy the offending command-block")));
        inv.setItem(13,Items.booleanItem(Sentinel.violationConfig.commandBlockExecute.attemptRestore,Items.configItem("Restore",Material.COMMAND_BLOCK,"Attempt to restore the block if a \nwhitelisted one exists at the location")));
        inv.setItem(15,Items.booleanItem(Sentinel.violationConfig.commandBlockExecute.logToDiscord,Items.configItem("Log",Material.OAK_LOG,"If this check will produce a log to discord")));
    }

    private void mainClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 4 -> {
                Sentinel.violationConfig.commandBlockExecute.enabled = !Sentinel.violationConfig.commandBlockExecute.enabled;
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }
            case 11 -> {
                Sentinel.violationConfig.commandBlockExecute.destroyBlock = !Sentinel.violationConfig.commandBlockExecute.destroyBlock;
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }
            case 13 -> {
                Sentinel.violationConfig.commandBlockExecute.attemptRestore = !Sentinel.violationConfig.commandBlockExecute.attemptRestore;
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }
            case 15 -> {
                Sentinel.violationConfig.commandBlockExecute.logToDiscord = !Sentinel.violationConfig.commandBlockExecute.logToDiscord;
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }
        }
    }
}

