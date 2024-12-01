package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.CBWhitelistManager;
import me.trouper.sentinel.utils.trees.Node;
import me.trouper.sentinel.utils.FileUtils;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.server.functions.ViolationController;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class CBPlaceEvent implements CustomListener {

    @EventHandler
    private void onCMDBlockPlace(BlockPlaceEvent e) {
        //ServerUtils.verbose("CommandBlockPlace: Detected block place");
        if (!Sentinel.violationConfig.commandBlockPlace.enabled) return;
        ServerUtils.verbose("CommandBlockPlace: Enabled");
        if (!e.getPlayer().isOp()) return;
        ServerUtils.verbose("CommandBlockPlace: Player is operator");
        Block b = e.getBlockPlaced();
        if (!(b.getType().equals(Material.COMMAND_BLOCK) ||
                b.getType().equals(Material.REPEATING_COMMAND_BLOCK) ||
                b.getType().equals(Material.CHAIN_COMMAND_BLOCK))) return;
        ServerUtils.verbose("CommandBlockPlace: Block is a command block");
        Player p = e.getPlayer();
        if (PlayerUtils.isTrusted(p)) {
            if (!CBWhitelistManager.autoWhitelist.contains(p.getUniqueId())) return;
            CBWhitelistManager.add((CommandBlock) b.getState(), p.getUniqueId());
            return;
        }
        ServerUtils.verbose("CommandBlockPlace: Not trusted, performing action");
        e.setCancelled(true);

        Node log = getLog(p, (CommandBlock) b.getState());

        ViolationController.handleViolation(
                Sentinel.lang.violations.commandBlockPlace.detectionChat.formatted(p.getName()),
                Sentinel.violationConfig.commandBlockPlace.punish,
                Sentinel.violationConfig.commandBlockPlace.deop,
                Sentinel.violationConfig.commandBlockPlace.logToDiscord,
                p,
                Sentinel.violationConfig.commandBlockPlace.punishmentCommands,
                log
        );
    }

    private static Node getLog(Player p, CommandBlock cb) {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.commandBlockPlace.detectionTree);

        Node playerInfo = new Node(Sentinel.lang.violations.commandBlockPlace.playerInfoTitle.formatted(p.getName()));
        playerInfo.addKeyValue(Sentinel.lang.violations.commandBlockPlace.uuid, p.getUniqueId().toString());
        playerInfo.addField(Sentinel.lang.violations.commandBlockPlace.location, Sentinel.lang.violations.commandBlockPlace.locationFormat.formatted(Math.round(p.getX()), Math.round(p.getY()), Math.round(p.getZ())));
        root.addChild(playerInfo);

        Node violationInfo = new Node(Sentinel.lang.violations.commandBlockPlace.commandBlockEditInfoTitle);
        violationInfo.addField(Sentinel.lang.violations.commandBlockPlace.blockLocation, Sentinel.lang.violations.commandBlockPlace.blockLocationFormat.formatted(cb.getWorld().getName(), cb.getX(), cb.getY(), cb.getZ()));
        String command = cb.getCommand();
        if (command.length() <= 128) {
            violationInfo.addField(Sentinel.lang.violations.commandBlockPlace.insertedCommand, command);
        } else {
            violationInfo.addKeyValue(Sentinel.lang.violations.commandBlockPlace.insertedCommandUploadedTo, FileUtils.createCommandLog(command));
        }
        root.addChild(violationInfo);

        return root;
    }
}
