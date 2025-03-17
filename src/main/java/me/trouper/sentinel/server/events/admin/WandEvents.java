package me.trouper.sentinel.server.events.admin;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.utils.misc.SoundPlayer;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.CommandBlockHolder;
import me.trouper.sentinel.data.types.Selection;
import me.trouper.sentinel.utils.Text;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.*;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class WandEvents implements CustomListener {
    public static final ItemStack SELECTION_WAND = ItemBuilder.create()
            .material(Material.BLAZE_ROD)
            .name(Text.color("&dCommand Block Wand"))
            .lore(Text.color("&7Use this wand to manage command blocks."))
            .lore(Text.color("&7It can scan up to 10 blocks away."))
            .lore(Text.color("&7Selections are visible up to 64 blocks away."))
            .lore(Text.color("&8&l➥&r &7Left Click&8:&f Set Position 1"))
            .lore(Text.color("&8&l➥&r &7Right Click&8:&f Set Position 2"))
            .lore(Text.color("&8&l➥&r &7Break CMD Block&8:&f Remove from whitelist"))
            .lore(Text.color("&8&l➥&r &7Click CMD Block&8:&f Add to whitelist"))
            .lore(Text.color("&8&l➥&r &7Sneak&8:&f Force Position Setting"))
            .lore(Text.color("&7Blocks close to you will get highlighted when holding."))
            .lore(Text.color(" &fHighlight Color Key&8:"))
            .lore(Text.color(" &8- &cRed &7: &fNot whitelisted."))
            .lore(Text.color(" &8- &aGreen &7: &fWhitelisted."))
            .lore(Text.color(" &8- &9Blue &7: &fYour selection."))
            .lore(Text.color(" &8- &dPurple &7: &fMissing Command Block"))
            .lore(Text.color(" &8- &fBlack &7: &fUnknown Command Block (Auto-Correcting)"))
            .customModelData(1984)
            .build();

    public static final Map<UUID, Selection> selections = new HashMap<>();

    @EventHandler
    public void onClickEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        ItemStack i = p.getInventory().getItemInMainHand();

        if (!i.isSimilar(SELECTION_WAND)) return;
        if (!PlayerUtils.isTrusted(p)) return;

        SoundPlayer add = new SoundPlayer(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
        if (!(e.getRightClicked() instanceof CommandMinecart cm)) return;

        e.setCancelled(true);

        Sentinel.getInstance().getDirector().whitelistManager.getFromList(cm.getUniqueId()).setWhitelisted(true);
        add.play(p);
    }

    @EventHandler
    public void onDamage(VehicleDamageEvent e) {
        if (!(e.getAttacker() instanceof Player p)) return;
        ItemStack i = p.getInventory().getItemInMainHand();

        if (!i.isSimilar(SELECTION_WAND)) return;
        if (!PlayerUtils.isTrusted(p)) return;

        SoundPlayer remove = new SoundPlayer(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 100, 1);

        if (!(e.getVehicle() instanceof CommandMinecart cm)) return;
        e.setCancelled(true);

        Sentinel.getInstance().getDirector().whitelistManager.getFromList(cm.getUniqueId()).setWhitelisted(false);
        remove.play(p);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack i = p.getInventory().getItemInMainHand();

        if (!i.isSimilar(SELECTION_WAND)) return;
        if (!PlayerUtils.isTrusted(p)) return;

        SoundPlayer add = new SoundPlayer(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
        SoundPlayer remove = new SoundPlayer(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 100, 1);
        SoundPlayer set1 = new SoundPlayer(p.getLocation(), Sound.UI_BUTTON_CLICK, 100, 1);
        SoundPlayer set2 = new SoundPlayer(p.getLocation(), Sound.UI_BUTTON_CLICK, 100, 0.8F);

        Selection selection = selections.computeIfAbsent(p.getUniqueId(), k -> new Selection());
        if (p.getTargetBlockExact(10) == null) return;
        Location loc = p.getTargetBlockExact(10).getLocation();

        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            e.setCancelled(true);
            if (p.isSneaking() && ServerUtils.isCommandBlock(loc.getBlock())) {
                set1.play(p);
                setPos1(p, selection, loc);
            } else if (ServerUtils.isCommandBlock(loc.getBlock())) {
                remove.play(p);
                Sentinel.getInstance().getDirector().whitelistManager.getFromList(loc).setWhitelisted(false);
            } else {
                set1.play(p);
                setPos1(p, selection, loc);
            }
        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (p.isSneaking() && ServerUtils.isCommandBlock(loc.getBlock())) {
                e.setCancelled(true);
                set2.play(p);
                setPos2(p, selection, loc);
            } else if (ServerUtils.isCommandBlock(loc.getBlock())) {
                add.play(p);
                Sentinel.getInstance().getDirector().whitelistManager.generateHolder(p.getUniqueId(),(CommandBlock) loc.getBlock().getState()).addAndWhitelist();
                e.setCancelled(true);
            } else {
                e.setCancelled(true);
                set2.play(p);
                setPos2(p, selection, loc);
            }
        }
    }

    private static void sortNear(Player p) {
        ItemStack i = p.getInventory().getItemInMainHand();

        if (!i.isSimilar(SELECTION_WAND) || !PlayerUtils.isTrusted(p)) {
            return;
        }

        // Highlight nearby command blocks
        Selection around = new Selection();
        around.setPos1(p.getLocation().add(-10, -10, -10));
        around.setPos2(p.getLocation().add(10, 10, 10));
        around.getBlocks().stream()
                .filter(block -> ServerUtils.isCommandBlock(block) && block.getLocation().distance(p.getLocation()) <= 10)
                .forEach(block -> {
                    if (!(block.getState() instanceof CommandBlock cb)) return;
                    CommandBlockHolder holder = Sentinel.getInstance().getDirector().whitelistManager.getFromList(block.getLocation());
                    Material color = Material.BLACK_CONCRETE_POWDER;
                    if (holder == null) {
                        holder = Sentinel.getInstance().getDirector().whitelistManager.generateHolder(p.getUniqueId(), cb);
                        holder.add();
                    } else {
                        color = holder.isWhitelisted() ? Material.LIME_CONCRETE_POWDER : Material.RED_CONCRETE_POWDER;
                    }
                    holder.highlight(p, color);
                });

        // Highlight nearby command minecarts
        p.getNearbyEntities(10, 10, 10).stream()
                .filter(entity -> entity instanceof CommandMinecart)
                .forEach(entity -> {
                    CommandMinecart cm = (CommandMinecart) entity;
                    CommandBlockHolder holder = Sentinel.getInstance().getDirector().whitelistManager.getFromList(cm.getUniqueId());
                    Material color = Material.BLACK_CONCRETE_POWDER;
                    if (holder == null) {
                        holder = Sentinel.getInstance().getDirector().whitelistManager.generateHolder(p.getUniqueId(), cm);
                        holder.add();
                    } else {
                        color = holder.isWhitelisted() ? Material.LIME_CONCRETE_POWDER : Material.RED_CONCRETE_POWDER;
                    }
                    holder.highlight(p, color);
                });

        // Highlight missing command blocks
        List<CommandBlockHolder> holdersCopy = new ArrayList<>(Sentinel.getInstance().getDirector().io.commandBlocks.holders);
        holdersCopy.forEach(holder -> {
            if (!holder.present() && holder.isWhitelisted()) holder.highlight(p,Material.MAGENTA_CONCRETE_POWDER);
        });
    }

    public static void handleDisplay() {
        PlayerUtils.forEachTrusted(WandEvents::sortNear);

        // Display selections
        selections.forEach((uuid, selection) -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null || !p.isOnline() || !p.getInventory().getItemInMainHand().isSimilar(SELECTION_WAND)) return;
            selection.display(p);
        });
    }

    private void setPos2(Player p, Selection selection, Location loc) {
        if (selection.getPos2() != null && selection.getPos2().distance(loc) < 0.1) return;
        selection.setPos2(loc);
        p.sendMessage(Text.prefix("Position 2 set to " + Text.formatLoc(loc)));
    }

    private void setPos1(Player p, Selection selection, Location loc) {
        if (selection.getPos1() != null && selection.getPos1().distance(loc) < 0.1) return;
        selection.setPos1(loc);
        p.sendMessage(Text.prefix("Position 1 set to " + Text.formatLoc(loc)));
    }
}