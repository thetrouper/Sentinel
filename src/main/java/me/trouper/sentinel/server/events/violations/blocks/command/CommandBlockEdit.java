package me.trouper.sentinel.server.events.violations.blocks.command;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateCommandBlock;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.misc.CommandBlockHolder;
import me.trouper.sentinel.server.events.violations.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.AntiNukeGUI;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandBlockEdit extends AbstractViolation implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.UPDATE_COMMAND_BLOCK) return;
        ServerUtils.verbose("Packet is a command block update packet");

        WrapperPlayClientUpdateCommandBlock wrapper = new WrapperPlayClientUpdateCommandBlock(event);
        User user = event.getUser();
        Player p = Bukkit.getPlayer(user.getUUID());
        if (p == null) return;

        Vector3i pos = wrapper.getPosition();
        Location loc = new Location(p.getWorld(), pos.x, pos.y, pos.z);

        CommandBlockHolder holder = Sentinel.getInstance().getDirector().whitelistManager.getFromList(loc);
        if (PlayerUtils.isTrusted(p)) {
            if (Sentinel.getInstance().getDirector().whitelistManager.autoWhitelist.contains(p.getUniqueId())) holder.setWhitelisted(true);
            holder.update(p,wrapper);
            return;
        }

        if (!Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.enabled) {
            holder.update(p,wrapper);
            return;
        }

        ServerUtils.verbose("Enabled, performing action");

        event.setCancelled(true);

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setPlayer(p)
                .deop(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.deop)
                .punish(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.punish)
                .setPunishmentCommands(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.punishmentCommands)
                .logToDiscord(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.logToDiscord);


        runActions(
                Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.edit, Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.commandBlock),
                Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.edit, Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.commandBlock),
                generateCommandBlockInfo((CommandBlock) holder.loc().translate().getBlock().getState()),
                config
        );
    }


    @Override
    public CustomGui getConfigGui() {
        return CustomGui.create()
                .title(Text.color("&6&lSentinel &8»&0 Command Block Edit"))
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
        if (Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.enabled) {
            ring = Items.GREEN;
        }

        List<Integer> ringList = List.of(3,4,5,12,14,21,22,23);

        for (Integer i : ringList) {
            inv.setItem(i,ring);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(13,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.enabled,Items.configItem("Check Toggle", Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(2,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.deop,Items.configItem("De-Op",Material.END_CRYSTAL,"Remove the user's operator privileges")));
        inv.setItem(20,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.logToDiscord,Items.configItem("Log",Material.OAK_LOG,"If this check will produce a log to discord")));
        inv.setItem(6,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.punish,Items.configItem("Punish",Material.REDSTONE_TORCH,"Run the punishment commands")));
        inv.setItem(24,Items.stringListItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.punishmentCommands,Material.DIAMOND_AXE,"Punishment Commands","Commands that will be ran \nif this check is flagged."));
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 13 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.enabled = !Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.enabled;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 2 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.deop = !Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.deop;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 20 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.logToDiscord = !Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.logToDiscord;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 6 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.punish = !Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.punish;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }

            case 24 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg, args) -> {
                        cfg.commandBlockEdit.punishmentCommands.add(args.getAll().toString());
                    },"" + Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.punishmentCommands);
                    return;
                }
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockEdit.punishmentCommands.clear();
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }

        }
    }
}
