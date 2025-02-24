package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.helpers.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.server.functions.helpers.CBWhitelistManager;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class CommandBlockUse extends AbstractViolation {

    @EventHandler
    private void onCMDBlockUse(PlayerInteractEvent e) {
        //ServerUtils.verbose("CommandBlockUse: Detected Interaction");
        if (!Sentinel.violationConfig.commandBlockUse.enabled) return;
        //ServerUtils.verbose("CommandBlockUse: Enabled");
        Player p = e.getPlayer();
        if (!p.isOp()) return;
        //ServerUtils.verbose("CommandBlockUse: Player is op");
        if (e.getClickedBlock() == null) return;
        //ServerUtils.verbose("CommandBlockUse: Block isn't null");
        Block b = e.getClickedBlock();
        if (!(b.getType() == Material.COMMAND_BLOCK || b.getType() == Material.REPEATING_COMMAND_BLOCK || b.getType() == Material.CHAIN_COMMAND_BLOCK)) return;
        CommandBlock cb = (CommandBlock) b.getState();
        ServerUtils.verbose("CommandBlockUse: Block is a command block");
        if (PlayerUtils.isTrusted(p)) {
            if (!CBWhitelistManager.autoWhitelist.contains(p.getUniqueId())) return;
            if (CBWhitelistManager.canRun(cb.getBlock())) return;
            e.setCancelled(true);
            CBWhitelistManager.add(cb, p.getUniqueId());
            return;
        }
        ServerUtils.verbose("CommandBlockUse: Not trusted, performing action");

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setPlayer(p)
                .deop(Sentinel.violationConfig.commandBlockUse.deop)
                .cancel(true)
                .punish(Sentinel.violationConfig.commandBlockUse.punish)
                .setPunishmentCommands(Sentinel.violationConfig.commandBlockUse.punishmentCommands)
                .logToDiscord(Sentinel.violationConfig.commandBlockUse.logToDiscord);

        runActions(
                Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.use, Sentinel.lang.violations.protections.rootName.commandBlock),
                Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.use, Sentinel.lang.violations.protections.rootName.commandBlock),
                generateCommandBlockInfo(cb),
                config
        );
    }
}