package me.trouper.sentinel.server.events.violations.entities;

import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.Sentinel;
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
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class CommandMinecartPlace extends AbstractViolation {

    private final ConcurrentHashMap<Location,UUID> queuedInteractions = new ConcurrentHashMap<>();
    
    private UUID getPlayer(Location loc) {
        ServerUtils.verbose("Getting responsible player for a location");
        AtomicReference<UUID> player = new AtomicReference<>();
        
        queuedInteractions.forEach((location,uuid)->{
            if (player.get() == null) {
                ServerUtils.verbose("Loop is running");
                if (loc.distance(location) < 1) {
                    ServerUtils.verbose("Found a matching minecart");
                    player.set(uuid);
                    queuedInteractions.remove(location);
                }
            }
        });
        return player.get();
    }
    
    @EventHandler
    private void onVehicleCreate(VehicleCreateEvent e) {
        //ServerUtils.verbose("Vehicle Creation Event");
        if (!(e.getVehicle() instanceof CommandMinecart commandMinecart)) return;
        if (queuedInteractions.isEmpty()) {
            ServerUtils.verbose("Queue is empty, preventing");
            e.setCancelled(true);
            return;
        }
        UUID uuid = getPlayer(e.getVehicle().getLocation());
        if (uuid == null) {
            ServerUtils.verbose("UUID is null, preventing");
            e.setCancelled(true);
            return;
        }
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) {
            ServerUtils.verbose("Player is null, preventing");
            e.setCancelled(true);
            return;     
        }
        
        if (PlayerUtils.isTrusted(p)) {
            ServerUtils.verbose("Player is trusted, allowing.");
            Sentinel.getInstance().getDirector().whitelistManager.generateHolder(p.getUniqueId(),commandMinecart)
                    .addToExisting();
            return;
        }
        

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setPlayer(p)
                .cancel(true)
                .punish(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.punish)
                .deop(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.deop)
                .setPunishmentCommands(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.punishmentCommands)
                .logToDiscord(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.logToDiscord);

        // Remove the command block minecart from the player's inventory
        p.getInventory().remove(Material.COMMAND_BLOCK_MINECART);

        runActions(
                Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.place, Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.commandMinecart),
                Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.place, Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.commandMinecart),
                generateMinecartInfo(commandMinecart),
                config
        );
    }
    
    @EventHandler
    private void onIneteract(PlayerInteractEvent e) {
        //ServerUtils.verbose("Player Interaction Event");
        if (!Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.enabled) return;
        //ServerUtils.verbose("MinecartCommandPlace: Check is enabled");
        Player p = e.getPlayer();
        if (e.getItem() == null) return;
        //ServerUtils.verbose("MinecartCommandPlace: Item isn't null");
        if (e.getClickedBlock() == null) return;
        //ServerUtils.verbose("MinecartCommandPlace: Clicked block isn't null");
        if (!e.getItem().getType().equals(Material.COMMAND_BLOCK_MINECART)) return;
        ServerUtils.verbose("Item is a minecart command");
        if (!(e.getClickedBlock().getType() == Material.RAIL || e.getClickedBlock().getType() == Material.POWERED_RAIL || e.getClickedBlock().getType() == Material.ACTIVATOR_RAIL || e.getClickedBlock().getType() == Material.DETECTOR_RAIL)) return;
        ServerUtils.verbose("Clicked block is a rail, adding to list");

        queuedInteractions.put(e.getClickedBlock().getLocation(),p.getUniqueId());
    }

    @Override
    public CustomGui getConfigGui() {
        return CustomGui.create()
                .title(Text.color("&6&lSentinel &8»&0 Command Cart Place"))
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
        if (Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.enabled) {
            ring = Items.GREEN;
        }

        List<Integer> ringList = List.of(3,4,5,12,14,21,22,23);

        for (Integer i : ringList) {
            inv.setItem(i,ring);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(13,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.enabled,Items.configItem("Check Toggle", Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(2,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.deop,Items.configItem("De-Op",Material.END_CRYSTAL,"Remove the user's operator privileges")));
        inv.setItem(20,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.logToDiscord,Items.configItem("Log",Material.OAK_LOG,"If this check will produce a log to discord")));
        inv.setItem(6,Items.booleanItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.punish,Items.configItem("Punish",Material.REDSTONE_TORCH,"Run the punishment commands")));
        inv.setItem(24,Items.stringListItem(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.punishmentCommands,Material.DIAMOND_AXE,"Punishment Commands","Commands that will be ran \nif this check is flagged."));
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 13 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.enabled = !Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.enabled;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 2 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.deop = !Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.deop;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 20 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.logToDiscord = !Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.logToDiscord;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
            case 6 -> {
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.punish = !Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.punish;
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }

            case 24 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg, args) -> {
                        cfg.commandBlockMinecartPlace.punishmentCommands.add(args.getAll().toString());
                    },"" + Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.punishmentCommands);
                    return;
                }
                Sentinel.getInstance().getDirector().io.violationConfig.commandBlockMinecartPlace.punishmentCommands.clear();
                getMainPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.violationConfig.save();
            }
        }
    }
}