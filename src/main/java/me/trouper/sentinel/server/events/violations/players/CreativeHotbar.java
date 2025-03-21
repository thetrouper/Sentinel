package me.trouper.sentinel.server.events.violations.players;

import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.Pair;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.storage.NBTStorage;
import me.trouper.sentinel.server.events.violations.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.server.functions.itemchecks.ItemCheck;
import me.trouper.sentinel.server.functions.itemchecks.RateLimitCheck;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.AntiNukeGUI;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CreativeHotbar extends AbstractViolation {

    @EventHandler
    private void onNBTPull(InventoryCreativeEvent e) {
        //ServerUtils.verbose("NBT: Detected creative mode action");
        if (!Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.enabled) return;
        //ServerUtils.verbose("NBT: Enabled");
        if (!(e.getWhoClicked() instanceof Player p)) return;
        //ServerUtils.verbose("NBT: Clicker is a player");
        if (e.getCursor() == null) return; // Well it threw an exception during testing, so it isn't always false!
        //ServerUtils.verbose("NBT: Cursor isn't null");
        ItemStack i = e.getCursor();
        if (PlayerUtils.isTrusted(p)) return;
        //ServerUtils.verbose("NBT: Not trusted");
        if (e.getCursor().getItemMeta() == null) return;
        //ServerUtils.verbose("NBT: Cursor has meta");
        if (!(i.hasItemMeta() && i.getItemMeta() != null)) return;
        if (!new RateLimitCheck().passes(new Pair<>(p,i))) {
            List<String> punishmentCommands = new ArrayList<>();
            for (String punishmentCommand : Sentinel.getInstance().getDirector().io.nbtConfig.rateLimit.punishmentCommands) {
                punishmentCommands.add(punishmentCommand.formatted());
            }

            ServerUtils.verbose("Player flags rate limit, performing action");
            ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                    .setEvent(e)
                    .setPlayer(p)
                    .cancel(true)
                    .punish(true)
                    .deop(Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.deop)
                    .setPunishmentCommands();
            
            runActions(
                    Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.grab, Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.nbtItem),
                    Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.grab, Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.nbtItem),
                    generatePlayerInfo(p),
                    config
            );
            
            return;
        }
        if (new ItemCheck().passes(i)) return;
        ServerUtils.verbose("NBT: Item doesn't pass, performing action");

        Sentinel.getInstance().getDirector().io.nbtStorage.caughtItems.put(NBTStorage.toB64(i),p.getUniqueId().toString());
        Sentinel.getInstance().getDirector().io.nbtStorage.save();

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setPlayer(p)
                .cancel(true)
                .punish(Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.punish)
                .deop(Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.deop)
                .setPunishmentCommands(Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.punishmentCommands)
                .logToDiscord(Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.logToDiscord);

        runActions(
                Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.grab, Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.nbtItem),
                Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.grab, Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.nbtItem),
                generateItemInfo(i),
                config
        );
    }

    @Override
    public CustomGui getConfigGui() {
        return CustomGui.create()
                .title(Text.color("&6&lSentinel &8»&0 Creative Hotbar Check"))
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
            inv.setItem(i, Items.BLANK);
        }

        ItemStack ring = Items.RED;
        if (Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.enabled) {
            ring = Items.GREEN;
        }

        List<Integer> ringList = List.of(3,4,5,12,14,21,22,23);

        for (Integer i : ringList) {
            inv.setItem(i,ring);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(13,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.enabled,Items.configItem("Check Toggle", Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(2,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.deop,Items.configItem("De-Op",Material.END_CRYSTAL,"Remove the user's operator privileges")));
        inv.setItem(20,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.logToDiscord,Items.configItem("Log",Material.OAK_LOG,"If this check will produce a log to discord")));
        inv.setItem(6,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.punish,Items.configItem("Punish",Material.REDSTONE_TORCH,"Run the punishment commands")));
        inv.setItem(24,Items.stringListItem(Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.punishmentCommands,Material.DIAMOND_AXE,"Punishment Commands","Commands that will be ran \nif this check is flagged."));
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;

        switch (e.getSlot()) {
            case 13 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.enabled = !Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.enabled;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 2 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.deop = !Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.deop;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 20 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.logToDiscord = !Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.logToDiscord;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 6 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.punish = !Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.punish;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }

            case 24 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg, args) -> {
                        cfg.creativeHotbarAction.punishmentCommands.add(args.getAll().toString());
                    },"" + Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.punishmentCommands);
                    return;
                }
                Sentinel.getInstance().getDirector().io.violationConfig.creativeHotbarAction.punishmentCommands.clear();
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }

        }
    }
}