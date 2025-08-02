package me.trouper.sentinel.server.events.violations.entities;

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
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandMinecartUse extends AbstractViolation {

    @EventHandler
    private void onCMDBlockMinecartUse(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (!(e.getRightClicked() instanceof CommandMinecart cm)) return;
        ServerUtils.verbose("MinecartCommandUse: Entity is minecart command");

        CommandBlockHolder holder = main.dir().whitelistManager.getFromList(cm.getUniqueId());
        if (PlayerUtils.isTrusted(p)) {
            if (main.dir().whitelistManager.autoWhitelist.contains(p.getUniqueId())) holder.setWhitelisted(true);
            holder.update(p);
            e.setCancelled(true);
            return;
        }

        if (!main.dir().io.violationConfig.commandBlockUse.enabled) {
            holder.update(p);
            return;
        }

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setPlayer(p)
                .setEntity(cm)
                .cancel(true)
                .punish(main.dir().io.violationConfig.commandBlockMinecartUse.punish)
                .deop(main.dir().io.violationConfig.commandBlockMinecartUse.deop)
                .setPunishmentCommands(main.dir().io.violationConfig.commandBlockMinecartUse.punishmentCommands)
                .logToDiscord(main.dir().io.violationConfig.commandBlockMinecartUse.logToDiscord);

        runActions(
                Text.format(Text.Pallet.WARNING,main.dir().io.lang.violations.protections.rootName.rootNameFormatPlayer,p.getName(), main.dir().io.lang.violations.protections.rootName.use, main.dir().io.lang.violations.protections.rootName.commandMinecart),
                generateMinecartInfo(cm),
                config
        );
    }

    @Override
    public CustomGui getConfigGui() {
        return CustomGui.create()
                .title(OldTXT.color("&6&lSentinel &8»&0 Command Cart Use"))
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
        if (main.dir().io.violationConfig.commandBlockMinecartUse.enabled) {
            ring = Items.GREEN;
        }

        List<Integer> ringList = List.of(3,4,5,12,14,21,22,23);

        for (Integer i : ringList) {
            inv.setItem(i,ring);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(13,Items.booleanItem(main.dir().io.violationConfig.commandBlockMinecartUse.enabled,Items.configItem("Check Toggle", Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(2,Items.booleanItem(main.dir().io.violationConfig.commandBlockMinecartUse.deop,Items.configItem("De-Op",Material.END_CRYSTAL,"Remove the user's operator privileges")));
        inv.setItem(20,Items.booleanItem(main.dir().io.violationConfig.commandBlockMinecartUse.logToDiscord,Items.configItem("Log",Material.OAK_LOG,"If this check will produce a log to discord")));
        inv.setItem(6,Items.booleanItem(main.dir().io.violationConfig.commandBlockMinecartUse.punish,Items.configItem("Punish",Material.REDSTONE_TORCH,"Run the punishment commands")));
        inv.setItem(24,Items.stringListItem(main.dir().io.violationConfig.commandBlockMinecartUse.punishmentCommands,Material.DIAMOND_AXE,"Punishment Commands","Commands that will be ran \nif this check is flagged."));
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 13 -> {
                main.dir().io.violationConfig.commandBlockMinecartUse.enabled = !main.dir().io.violationConfig.commandBlockMinecartUse.enabled;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 2 -> {
                main.dir().io.violationConfig.commandBlockMinecartUse.deop = !main.dir().io.violationConfig.commandBlockMinecartUse.deop;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 20 -> {
                main.dir().io.violationConfig.commandBlockMinecartUse.logToDiscord = !main.dir().io.violationConfig.commandBlockMinecartUse.logToDiscord;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 6 -> {
                main.dir().io.violationConfig.commandBlockMinecartUse.punish = !main.dir().io.violationConfig.commandBlockMinecartUse.punish;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }

            case 24 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg, args) -> {
                        cfg.commandBlockMinecartUse.punishmentCommands.add(args.getAll().toString());
                    },"" + main.dir().io.violationConfig.commandBlockMinecartUse.punishmentCommands);
                    return;
                }
                main.dir().io.violationConfig.commandBlockMinecartUse.punishmentCommands.clear();
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
        }
    }
}