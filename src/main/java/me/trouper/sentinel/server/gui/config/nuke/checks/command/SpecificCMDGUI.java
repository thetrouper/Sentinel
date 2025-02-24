package me.trouper.sentinel.server.gui.config.nuke.checks.command;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.config.ConfigUpdater;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.ViolationConfig;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.nuke.CommandGUI;
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

import java.util.function.BiConsumer;

public class SpecificCMDGUI {
    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 Specific Command Check"))
            .size(27)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define(26, Items.BACK, e->{
                e.getWhoClicked().openInventory(new CommandGUI().home.getInventory());
            })
            .build();

    private void blankPage(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i,Items.BLANK);
        }

        ItemStack top = Items.RED;
        if (Sentinel.violationConfig.commandExecute.specific.enabled) {
            top = Items.GREEN;
        }

        for (int i = 0; i < 9; i++) {
            inv.setItem(i,top);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(4,Items.booleanItem(Sentinel.violationConfig.commandExecute.specific.enabled,Items.configItem("Check Toggle", Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(11,Items.booleanItem(Sentinel.violationConfig.commandExecute.specific.punish,Items.configItem("Punish",Material.REDSTONE_TORCH,"If this check will run the punishment commands")));
        inv.setItem(13,Items.booleanItem(Sentinel.violationConfig.commandExecute.specific.logToDiscord,Items.configItem("Log",Material.OAK_LOG,"If this check will produce a log to discord")));
        inv.setItem(15,Items.stringListItem(Sentinel.violationConfig.commandExecute.specific.punishmentCommands,Material.DIAMOND_AXE,"Commands","Commands that will flag this check"));
    }

    private void mainClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 4 -> {
                Sentinel.violationConfig.commandExecute.specific.enabled = !Sentinel.violationConfig.commandExecute.specific.enabled;
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }
            case 13 -> {
                Sentinel.violationConfig.commandExecute.specific.logToDiscord = !Sentinel.violationConfig.commandExecute.specific.logToDiscord;
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }
            case 11 -> {
                Sentinel.violationConfig.commandExecute.specific.punish = !Sentinel.violationConfig.commandExecute.specific.punish;
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }

            case 15 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg, args) -> {
                        cfg.commandExecute.specific.punishmentCommands.add(args.getAll().toString());
                    },"" + Sentinel.violationConfig.commandExecute.specific.punishmentCommands);
                    return;
                }
                Sentinel.violationConfig.commandExecute.specific.punishmentCommands.clear();
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
