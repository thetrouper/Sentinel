package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.CBWhitelistManager;
import me.trouper.sentinel.server.functions.ViolationController;
import me.trouper.sentinel.utils.FileUtils;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.trees.Node;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class CBUseEvent implements CustomListener {

    @EventHandler
    private void onCMDBlockUse(PlayerInteractEvent e) {
        //ServerUtils.verbose("CommandBlockUse: Detected Interaction");
        if (!Sentinel.violationConfig.commandBlockUse.enabled) return;
        //ServerUtils.verbose("CommandBlockUse: Enabled");
        if (!e.getPlayer().isOp()) return;
        //ServerUtils.verbose("CommandBlockUse: Player is op");
        if (e.getClickedBlock() == null) return;
        //ServerUtils.verbose("CommandBlockUse: Block isn't null");
        Block b = e.getClickedBlock();
        if (!(b.getType() == Material.COMMAND_BLOCK || b.getType() == Material.REPEATING_COMMAND_BLOCK || b.getType() == Material.CHAIN_COMMAND_BLOCK)) return;
        CommandBlock cb = (CommandBlock) b.getState();
        ServerUtils.verbose("CommandBlockUse: Block is a command block");
        Player p = e.getPlayer();
        if (PlayerUtils.isTrusted(p)) {
            if (!CBWhitelistManager.autoWhitelist.contains(p.getUniqueId())) return;
            if (CBWhitelistManager.canRun(cb.getBlock())) return;
            e.setCancelled(true);
            CBWhitelistManager.add(cb, p.getUniqueId());
            return;
        }
        ServerUtils.verbose("CommandBlockUse: Not trusted, performing action");
        e.setCancelled(true);

        Node log = getLog(p, (CommandBlock) b.getState());

        ViolationController.handleViolation(
                Sentinel.lang.violations.commandBlockUse.detectionChat.formatted(p.getName()),
                Sentinel.violationConfig.commandBlockUse.punish,
                Sentinel.violationConfig.commandBlockUse.deop,
                Sentinel.violationConfig.commandBlockUse.logToDiscord,
                p,
                Sentinel.violationConfig.commandBlockUse.punishmentCommands,
                log
        );
    }

    private static Node getLog(Player p, CommandBlock cb) {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.commandBlockUse.detectionTree);

        Node playerInfo = new Node(Sentinel.lang.violations.commandBlockUse.playerInfoTitle.formatted(p.getName()));
        playerInfo.addKeyValue(Sentinel.lang.violations.commandBlockUse.uuid, p.getUniqueId().toString());
        playerInfo.addField(Sentinel.lang.violations.commandBlockUse.location, Sentinel.lang.violations.commandBlockUse.locationFormat.formatted(Math.round(p.getX()), Math.round(p.getY()), Math.round(p.getZ())));
        root.addChild(playerInfo);

        Node violationInfo = new Node(Sentinel.lang.violations.commandBlockUse.commandBlockUseInfoTitle);
        violationInfo.addField(Sentinel.lang.violations.commandBlockUse.blockLocation, Sentinel.lang.violations.commandBlockUse.blockLocationFormat.formatted(cb.getWorld().getName(), cb.getX(), cb.getY(), cb.getZ()));
        String command = cb.getCommand();
        if (command.length() <= 128) {
            violationInfo.addField(Sentinel.lang.violations.commandBlockUse.commandInside, command);
        } else {
            violationInfo.addKeyValue(Sentinel.lang.violations.commandBlockUse.commandUploadedTo, FileUtils.createCommandLog(command));
        }
        root.addChild(violationInfo);

        return root;
    }


}
