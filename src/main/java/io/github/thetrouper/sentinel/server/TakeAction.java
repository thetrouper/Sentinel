package io.github.thetrouper.sentinel.server;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.server.util.FileUtils;
import io.github.thetrouper.sentinel.server.util.Notifications.NotifyConsole;
import io.github.thetrouper.sentinel.server.util.Notifications.NotifyDiscord;
import io.github.thetrouper.sentinel.server.util.Notifications.NotifyTrusted;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TakeAction {
    // command nbt place useblock useentity
    public static void command(PlayerCommandPreprocessEvent e) {
        boolean deoped = false;
        boolean banned = false;
        boolean denied = false;
        boolean logged = false;
        Player p = e.getPlayer();
        String message = e.getMessage();
        String command = e.getMessage().substring(1).split(" ")[0];
        if (Sentinel.isDangerousCommand(command)) {
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                denied = true;
            }
        }
        if (Config.blockSpecificCommands) {
            if (command.contains(":")) {
                if (!Sentinel.isTrusted(p)) {
                    e.setCancelled(true);
                    denied = true;
                }
            }
        }
        if (Config.deop) {
            e.getPlayer().setOp(false);
            deoped = true;
        }
        if (Config.ban) {
            ServerUtils.sendCommand("ban " + e.getPlayer().getName() + " ]=- Sentinel Anti-Grief -=[ You have been banned for attempting to use dangerous commands. Contact an administrator if you believe this to be a mistake.");
            banned = true;
        }
        if (Config.logDangerousCommands) {
            logged = true;
            NotifyDiscord.command(e.getPlayer(),message,denied,deoped,banned,true);
        }
        NotifyConsole.command(e.getPlayer(),message,denied,deoped,banned,logged);
        NotifyTrusted.command(e.getPlayer(),message,denied,deoped,banned,logged);
    }
    public static void NBT(InventoryCreativeEvent e) {
        Player p = (Player) e.getWhoClicked();
        final ItemStack item = e.getCursor();
        boolean removed = false;
        boolean deoped = false;
        boolean banned = false;
        boolean logged = false;
        boolean gms = false;
        if (Config.preventNBT) {
            e.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(Sentinel.getInstance(),() -> {
                e.getCursor().setType(Material.AIR);
            }, 1);
            p.setGameMode(GameMode.SURVIVAL);
            gms = true;
            removed = true;
        }
        if (Config.deop) {
            p.setOp(false);
            deoped = true;
        }
        if (Config.ban) {
            ServerUtils.sendCommand("ban " + p.getName() + " ]=- Sentinel Anti-Grief -=[ You have been banned for attempting to use dangerous items. Contact an administrator if you believe this to be a mistake.");
            banned = true;
        }
        if (Config.logNBT) {
            logged = true;
            NotifyDiscord.NBT(p,item,removed,deoped,gms,banned, true,FileUtils.createNBTLog(item.getType().toString().toLowerCase() + item.getItemMeta().getAsString()));
        }
        NotifyConsole.NBT(p,item,removed,deoped,gms,banned,logged);
        NotifyTrusted.NBT(p,item,removed,deoped,gms,banned,logged);
    }
    public static void placeBlock(BlockPlaceEvent e) {
        Block b = e.getBlock();
        Player p = e.getPlayer();
        boolean deleted = false;
        boolean deoped = false;
        boolean banned = false;
        boolean logged = false;
        if (Config.preventCmdBlocks) {
            e.setCancelled(true);
            p.getInventory().remove(Material.COMMAND_BLOCK);
            p.getInventory().remove(Material.REPEATING_COMMAND_BLOCK);
            p.getInventory().remove(Material.CHAIN_COMMAND_BLOCK);
            deleted = true;
        }
        if (Config.deop) {
            p.setOp(false);
            deoped = true;
        }
        if (Config.ban) {
            ServerUtils.sendCommand("ban " + p.getName() + " ]=- Sentinel Anti-Grief -=[ You have been banned for attempting to place dangerous blocks. Contact an administrator if you believe this to be a mistake.");
            banned = true;
        }
        if (Config.logCmdBlocks) {
            logged = true;
            NotifyDiscord.placeBlock(p,b,deleted,deoped,banned,logged);
        }
        NotifyConsole.placeBlock(p,b,deleted,deoped,banned,logged);
        NotifyTrusted.placeBlock(p,b,deleted,deoped,banned,logged);
    }
    public static void useBlock(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        boolean denied = false;
        boolean deoped = false;
        boolean banned = false;
        boolean logged = false;
        if (Config.preventCmdBlocks) {
            e.setCancelled(true);
            denied = true;
        }
        if (Config.deop) {
            p.setOp(false);
            deoped = true;
        }
        if (Config.ban) {
            ServerUtils.sendCommand("ban " + p.getName() + " ]=- Sentinel Anti-Grief -=[ You have been banned for attempting to use dangerous blocks. Contact an administrator if you believe this to be a mistake.");
            banned = true;
        }
        if (Config.logCmdBlocks) {
            logged = true;
            NotifyDiscord.usedBlock(p,b,denied,deoped,banned,logged);
        }
        NotifyConsole.usedBlock(p,b,denied,deoped,banned,logged);
        NotifyTrusted.usedBlock(p,b,denied,deoped,banned,logged);
    }
    public static void useEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() == null) return;
        Player p = e.getPlayer();
        Entity ent = e.getRightClicked();
        boolean denied = false;
        boolean deoped = false;
        boolean banned = false;
        boolean logged = false;
        if (Config.preventCmdBlocks) {
            e.setCancelled(true);
            denied = true;
        }
        if (Config.deop) {
            p.setOp(false);
            deoped = true;
        }
        if (Config.ban) {
            ServerUtils.sendCommand("ban " + p.getName() + " ]=- Sentinel Anti-Grief -=[ You have been banned for attempting to use dangerous entities. Contact an administrator if you believe this to be a mistake.");
            banned = true;
        }
        if (Config.logCmdBlocks) {
            logged = true;
            NotifyDiscord.usedEntity(p,ent,denied,deoped,banned,logged);
        }
        NotifyConsole.usedEntity(p,ent,denied,deoped,banned,logged);
        NotifyTrusted.usedEntity(p,ent,denied,deoped,banned,logged);
    }
}
