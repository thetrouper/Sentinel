package me.trouper.sentinel.server.events.violations.command;

import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.events.violations.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.AntiNukeGUI;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.OldTXT;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SpecificCommand extends AbstractViolation {

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (PlayerUtils.isTrusted(p)) return;
        String label = e.getMessage().substring(1).split(" ")[0];
        String args = e.getMessage();

        if (label.contains(":") && main.dir().io.violationConfig.commandExecute.specific.enabled) {
            e.setCancelled(true);
            ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                    .setEvent(e)
                    .setPlayer(p)
                    .cancel(true)
                    .punish(main.dir().io.violationConfig.commandExecute.specific.punish)
                    .setPunishmentCommands(main.dir().io.violationConfig.commandExecute.specific.punishmentCommands)
                    .logToDiscord(main.dir().io.violationConfig.commandExecute.specific.logToDiscord);

            runActions(
                    Text.format(Text.Pallet.WARNING,main.dir().io.lang.violations.protections.rootName.rootNameFormatPlayer,p.getName(), main.dir().io.lang.violations.protections.rootName.run, main.dir().io.lang.violations.protections.rootName.specificCommand),
                    generateCommandInfo(args, p),
                    config
            );
        }
    }

    @Override
    public CustomGui getConfigGui() {
        return CustomGui.create()
                .title(OldTXT.color("&6&lSentinel &8»&0 Specific Command Check"))
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
        if (main.dir().io.violationConfig.commandExecute.specific.enabled) {
            top = Items.GREEN;
        }

        for (int i = 0; i < 9; i++) {
            inv.setItem(i,top);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(4,Items.booleanItem(main.dir().io.violationConfig.commandExecute.specific.enabled,Items.configItem("Check Toggle", Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(11,Items.booleanItem(main.dir().io.violationConfig.commandExecute.specific.punish,Items.configItem("Punish",Material.REDSTONE_TORCH,"If this check will run the punishment commands")));
        inv.setItem(13,Items.booleanItem(main.dir().io.violationConfig.commandExecute.specific.logToDiscord,Items.configItem("Log",Material.OAK_LOG,"If this check will produce a log to discord")));
        inv.setItem(15,Items.stringListItem(main.dir().io.violationConfig.commandExecute.specific.punishmentCommands,Material.DIAMOND_AXE,"Commands","Commands that will flag this check"));
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 4 -> {
                main.dir().io.violationConfig.commandExecute.specific.enabled = !main.dir().io.violationConfig.commandExecute.specific.enabled;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 13 -> {
                main.dir().io.violationConfig.commandExecute.specific.logToDiscord = !main.dir().io.violationConfig.commandExecute.specific.logToDiscord;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 11 -> {
                main.dir().io.violationConfig.commandExecute.specific.punish = !main.dir().io.violationConfig.commandExecute.specific.punish;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }

            case 15 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg, args) -> {
                        cfg.commandExecute.specific.punishmentCommands.add(args.getAll().toString());
                    },"" + main.dir().io.violationConfig.commandExecute.specific.punishmentCommands);
                    return;
                }
                main.dir().io.violationConfig.commandExecute.specific.punishmentCommands.clear();
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
        }
    }
}
