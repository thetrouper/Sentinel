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
import org.bukkit.command.BlockCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandBlockExecute extends AbstractViolation {

    @EventHandler
    private void commandBlockExecute(ServerCommandEvent e) {
        //ServerUtils.verbose("Handling command block event: " + e.getCommand());
        if (!Sentinel.violationConfig.commandBlockExecute.enabled) return;
        //ServerUtils.verbose("Whitelist not disabled ");
        if (!(e.getSender() instanceof BlockCommandSender s)) return;
        //ServerUtils.verbose("Sender is command block");
        Block cmdBlock = s.getBlock();
        if (CBWhitelistManager.canRun(cmdBlock)) return;
        ServerUtils.verbose("Command block can't run.");

        CommandBlock cb = (CommandBlock) cmdBlock.getState();

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setBlock(cmdBlock)
                .cancel(true)
                .destroyBlock(Sentinel.violationConfig.commandBlockExecute.destroyBlock)
                .restoreBlock(Sentinel.violationConfig.commandBlockExecute.attemptRestore)
                .logToDiscord(Sentinel.violationConfig.commandBlockExecute.logToDiscord);

        runActions(
                Sentinel.lang.violations.protections.rootName.rootNameFormat.formatted(Sentinel.lang.violations.protections.rootName.commandBlockWhitelist),
                Sentinel.lang.violations.protections.rootName.rootNameFormat.formatted( Sentinel.lang.violations.protections.rootName.commandBlockWhitelist),
                generateCommandBlockInfo(cb),
                config
        );
    }
}