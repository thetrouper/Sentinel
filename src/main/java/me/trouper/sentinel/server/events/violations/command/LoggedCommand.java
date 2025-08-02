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

import java.util.List;

public class LoggedCommand extends AbstractViolation {
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (PlayerUtils.isTrusted(p)) return;
        String label = e.getMessage().substring(1).split(" ")[0];
        String args = e.getMessage();

        if (main.dir().io.violationConfig.commandExecute.logged.commands.contains(label) && main.dir().io.violationConfig.commandExecute.logged.enabled) {
            ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                    .setPlayer(p)
                    .logToDiscord(main.dir().io.violationConfig.commandExecute.logged.logToDiscord);

            runActions(
                    Text.format(Text.Pallet.WARNING,main.dir().io.lang.violations.protections.rootName.rootNameFormatPlayer,p.getName(), main.dir().io.lang.violations.protections.rootName.run, main.dir().io.lang.violations.protections.rootName.loggedCommand),
                    generateCommandInfo(args, p),
                    config
            );
        }
    }
    
    
    @Override
    public CustomGui getConfigGui() {
        return CustomGui.create()
                .title(OldTXT.color("&6&lSentinel &8»&0 Logged Command Check"))
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
        if (main.dir().io.violationConfig.commandExecute.logged.enabled) {
            ring = Items.GREEN;
        }

        List<Integer> ringList = List.of(3,4,5,12,14,21,22,23);

        for (Integer i : ringList) {
            inv.setItem(i,ring);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(13,Items.booleanItem(main.dir().io.violationConfig.commandExecute.logged.enabled,Items.configItem("Check Toggle", Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(11,Items.booleanItem(main.dir().io.violationConfig.commandExecute.logged.logToDiscord,Items.configItem("Log to Discord",Material.OAK_LOG,"If this check will log to discord")));
        inv.setItem(15,Items.stringListItem(main.dir().io.violationConfig.commandExecute.logged.commands,Material.CRIMSON_HANGING_SIGN,"Commands","Commands that will flag this check"));
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 13 -> {
                main.dir().io.violationConfig.commandExecute.logged.enabled = !main.dir().io.violationConfig.commandExecute.logged.enabled;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 11 -> {
                main.dir().io.violationConfig.commandExecute.logged.logToDiscord = !main.dir().io.violationConfig.commandExecute.logged.logToDiscord;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 15 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> {
                        cfg.commandExecute.logged.commands.add(args.getAll().toString());
                    },"" + main.dir().io.violationConfig.commandExecute.logged.commands);
                    return;
                }
                main.dir().io.violationConfig.commandExecute.logged.commands.clear();
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
        }
    }
}
