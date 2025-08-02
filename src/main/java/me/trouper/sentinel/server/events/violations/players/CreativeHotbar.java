package me.trouper.sentinel.server.events.violations.players;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.Pair;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.events.violations.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.server.functions.hotbar.items.ItemCheck;
import me.trouper.sentinel.server.functions.hotbar.items.RateLimitCheck;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.AntiNukeGUI;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.OldTXT;
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
        if (!main.dir().io.violationConfig.creativeHotbarAction.enabled) return;
        //ServerUtils.verbose("NBT: Enabled");
        if (!(e.getWhoClicked() instanceof Player p)) return;
        //ServerUtils.verbose("NBT: Clicker is a player");
        if (e.getCursor() == null) return; // Well it threw an exception during testing, so it isn't always false!
        //ServerUtils.verbose("NBT: Cursor isn't null");
        ItemStack i = e.getCursor();
        if (PlayerUtils.isTrusted(p)) return;
        //ServerUtils.verbose("NBT: Not trusted");
        scan(e,p,i);
    }
    
    public void scan(InventoryCreativeEvent e, Player p, ItemStack i) {
        if (e.getCursor().getItemMeta() == null) return;
        //ServerUtils.verbose("NBT: Cursor has meta");
        if (!(i.hasItemMeta() && i.getItemMeta() != null)) return;
        if (!new RateLimitCheck().passes(new Pair<>(p,i))) {
            List<String> punishmentCommands = new ArrayList<>();
            for (String punishmentCommand : main.dir().io.nbtConfig.rateLimit.punishmentCommands) {
                try {
                    punishmentCommand = punishmentCommand.formatted(RateLimitCheck.dataUsed.get(p.getUniqueId()),main.dir().io.nbtConfig.rateLimit.rateLimitBytes);
                } catch (Exception ignored) {}
                punishmentCommands.add(punishmentCommand);
            }

            ServerUtils.verbose("Player flags rate limit, performing action");
            ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                    .setEvent(e)
                    .setPlayer(p)
                    .cancel(true)
                    .punish(true)
                    .deop(main.dir().io.violationConfig.creativeHotbarAction.deop)
                    .setPunishmentCommands(punishmentCommands);

            runActions(
                    Text.format(Text.Pallet.WARNING,main.dir().io.lang.violations.protections.rootName.rootNameFormatPlayer,p.getName(), main.dir().io.lang.violations.protections.rootName.grab, main.dir().io.lang.violations.protections.rootName.nbtItem),
                    generatePlayerInfo(p),
                    config
            );

            return;
        }
        if (new ItemCheck().passes(i)) return;
        ServerUtils.verbose("NBT: Item doesn't pass, performing action");

        main.dir().io.nbtStorage.storeItem(i, p.getUniqueId());

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setPlayer(p)
                .cancel(true)
                .punish(main.dir().io.violationConfig.creativeHotbarAction.punish)
                .deop(main.dir().io.violationConfig.creativeHotbarAction.deop)
                .setPunishmentCommands(main.dir().io.violationConfig.creativeHotbarAction.punishmentCommands)
                .logToDiscord(main.dir().io.violationConfig.creativeHotbarAction.logToDiscord);

        runActions(
                Text.format(Text.Pallet.WARNING,main.dir().io.lang.violations.protections.rootName.rootNameFormatPlayer,p.getName(), main.dir().io.lang.violations.protections.rootName.grab, main.dir().io.lang.violations.protections.rootName.nbtItem),
                generateItemInfo(i),
                config
        );
    }

    @Override
    public CustomGui getConfigGui() {
        return CustomGui.create()
                .title(OldTXT.color("&6&lSentinel &8»&0 Creative Hotbar Check"))
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
        if (main.dir().io.violationConfig.creativeHotbarAction.enabled) {
            ring = Items.GREEN;
        }

        List<Integer> ringList = List.of(3,4,5,12,14,21,22,23);

        for (Integer i : ringList) {
            inv.setItem(i,ring);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(13,Items.booleanItem(main.dir().io.violationConfig.creativeHotbarAction.enabled,Items.configItem("Check Toggle", Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(2,Items.booleanItem(main.dir().io.violationConfig.creativeHotbarAction.deop,Items.configItem("De-Op",Material.END_CRYSTAL,"Remove the user's operator privileges")));
        inv.setItem(20,Items.booleanItem(main.dir().io.violationConfig.creativeHotbarAction.logToDiscord,Items.configItem("Log",Material.OAK_LOG,"If this check will produce a log to discord")));
        inv.setItem(6,Items.booleanItem(main.dir().io.violationConfig.creativeHotbarAction.punish,Items.configItem("Punish",Material.REDSTONE_TORCH,"Run the punishment commands")));
        inv.setItem(24,Items.stringListItem(main.dir().io.violationConfig.creativeHotbarAction.punishmentCommands,Material.DIAMOND_AXE,"Punishment Commands","Commands that will be ran \nif this check is flagged."));
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;

        switch (e.getSlot()) {
            case 13 -> {
                main.dir().io.violationConfig.creativeHotbarAction.enabled = !main.dir().io.violationConfig.creativeHotbarAction.enabled;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 2 -> {
                main.dir().io.violationConfig.creativeHotbarAction.deop = !main.dir().io.violationConfig.creativeHotbarAction.deop;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 20 -> {
                main.dir().io.violationConfig.creativeHotbarAction.logToDiscord = !main.dir().io.violationConfig.creativeHotbarAction.logToDiscord;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }
            case 6 -> {
                main.dir().io.violationConfig.creativeHotbarAction.punish = !main.dir().io.violationConfig.creativeHotbarAction.punish;
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }

            case 24 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg, args) -> {
                        cfg.creativeHotbarAction.punishmentCommands.add(args.getAll().toString());
                    },"" + main.dir().io.violationConfig.creativeHotbarAction.punishmentCommands);
                    return;
                }
                main.dir().io.violationConfig.creativeHotbarAction.punishmentCommands.clear();
                getMainPage(e.getInventory());
                main.dir().io.violationConfig.save();
            }

        }
    }
}