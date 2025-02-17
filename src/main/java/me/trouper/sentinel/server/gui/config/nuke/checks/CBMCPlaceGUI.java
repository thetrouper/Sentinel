package me.trouper.sentinel.server.gui.config.nuke.checks;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.config.ConfigUpdater;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.ViolationConfig;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.AntiNukeGUI;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;

public class CBMCPlaceGUI {

    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 CB MC Place Check"))
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

        ItemStack ring = Items.RED;
        if (Sentinel.violationConfig.commandBlockMinecartPlace.enabled) {
            ring = Items.GREEN;
        }

        List<Integer> ringList = List.of(3,4,5,12,14,21,22,23);

        for (Integer i : ringList) {
            inv.setItem(i,ring);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(13,Items.booleanItem(Sentinel.violationConfig.commandBlockMinecartPlace.enabled,Items.configItem("Check Toggle",Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(2,Items.booleanItem(Sentinel.violationConfig.commandBlockMinecartPlace.deop,Items.configItem("De-Op",Material.END_CRYSTAL,"Remove the user's operator privileges")));
        inv.setItem(20,Items.booleanItem(Sentinel.violationConfig.commandBlockMinecartPlace.logToDiscord,Items.configItem("Log",Material.OAK_LOG,"If this check will produce a log to discord")));
        inv.setItem(6,Items.booleanItem(Sentinel.violationConfig.commandBlockMinecartPlace.punish,Items.configItem("Punish",Material.REDSTONE_TORCH,"Run the punishment commands")));
        inv.setItem(24,Items.stringListItem(Sentinel.violationConfig.commandBlockMinecartPlace.punishmentCommands,Material.DIAMOND_AXE,"Punishment Commands","Commands that will be ran \nif this check is flagged."));
    }

    private void mainClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 13 -> {
                Sentinel.violationConfig.commandBlockMinecartPlace.enabled = !Sentinel.violationConfig.commandBlockMinecartPlace.enabled;
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }
            case 2 -> {
                Sentinel.violationConfig.commandBlockMinecartPlace.deop = !Sentinel.violationConfig.commandBlockMinecartPlace.deop;
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }
            case 20 -> {
                Sentinel.violationConfig.commandBlockMinecartPlace.logToDiscord = !Sentinel.violationConfig.commandBlockMinecartPlace.logToDiscord;
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }
            case 6 -> {
                Sentinel.violationConfig.commandBlockMinecartPlace.punish = !Sentinel.violationConfig.commandBlockMinecartPlace.punish;
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }

            case 24 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg, args) -> {
                        cfg.commandBlockMinecartPlace.punishmentCommands.add(args.getAll().toString());
                    },"" + Sentinel.violationConfig.commandBlockMinecartPlace.punishmentCommands);
                    return;
                }
                Sentinel.violationConfig.commandBlockMinecartPlace.punishmentCommands.clear();
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }

        }
    }


    public static ConfigUpdater<AsyncChatEvent, ViolationConfig> updater = new ConfigUpdater<>(Sentinel.violationConfig);
    private void queuePlayer(Player player, BiConsumer<ViolationConfig, Args> action, String currentValue) {
        MainGUI.awaitingCallback.add(player.getUniqueId());
        player.closeInventory();
        updater.queuePlayer(player, 20*60, (e)->{
            e.setCancelled(true);
            return LegacyComponentSerializer.legacySection().serialize(e.message());
        }, (cfg, newValue) -> {
            action.accept(cfg,new Args(newValue.split("\\s+")));
            cfg.save();
            player.sendMessage(Text.prefix("Value updated successfully"));
            player.openInventory(home.getInventory());
        });
        player.sendMessage(Component.text(Text.prefix("Enter the new value in chat. The value is currently set to &b%s&7. (Click to insert)".formatted(currentValue))).clickEvent(ClickEvent.suggestCommand(currentValue)));
    }
}
