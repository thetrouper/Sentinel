package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.CBWhitelistManager;
import me.trouper.sentinel.utils.FileUtils;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import me.trouper.sentinel.utils.trees.ConsoleFormatter;
import me.trouper.sentinel.utils.trees.EmbedFormatter;
import me.trouper.sentinel.utils.trees.HoverFormatter;
import me.trouper.sentinel.utils.trees.Node;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerCommandEvent;

public class CBExecuteEvent implements CustomListener {
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

        Node log = getLog(cb);
        Node actions = new Node(Sentinel.lang.violations.commandBlockExecute.actionsTitle);

        e.setCancelled(true);
        actions.addTextLine(Sentinel.lang.violations.commandBlockExecute.preventExecution);

        if (Sentinel.violationConfig.commandBlockExecute.destroyBlock) {
            cmdBlock.setType(Material.AIR);
            actions.addTextLine(Sentinel.lang.violations.commandBlockExecute.destroyedBlock);
        }

        if (Sentinel.violationConfig.commandBlockExecute.attemptRestore) {
            boolean restored = CBWhitelistManager.restore(cmdBlock.getLocation());
            actions.addKeyValue(Sentinel.lang.violations.commandBlockExecute.restore, restored ? Sentinel.lang.violations.commandBlockExecute.restoreSuccess : Sentinel.lang.violations.commandBlockExecute.restoreFailure);
        }

        if (Sentinel.violationConfig.commandBlockExecute.logToDiscord) actions.addTextLine(Sentinel.lang.violations.commandBlockExecute.loggedToDiscord);
        log.addChild(actions);

        if (Sentinel.violationConfig.commandBlockExecute.logToDiscord) {
            EmbedFormatter.sendEmbed(EmbedFormatter.format(log));
        }

        ServerUtils.forEachPlayer(trusted -> {
            if (PlayerUtils.isTrusted(trusted)) {
                trusted.sendMessage(Component.text(Text.prefix("The command block whitelist has been tripped!")).hoverEvent(Component.text(HoverFormatter.format(log)).asHoverEvent()));
            }
        });

        Sentinel.log.info(ConsoleFormatter.format(log));
    }

    private static Node getLog(CommandBlock cb) {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.commandBlockExecute.commandBlockWhitelistTripped);

        Node violationInfo = new Node(Sentinel.lang.violations.commandBlockExecute.commandBlockInfoTitle);
        violationInfo.addField(Sentinel.lang.violations.commandBlockExecute.blockLocation,"World: %s X: %s Y: %s Z: %s".formatted(cb.getWorld().getName(), cb.getX(), cb.getY(), cb.getZ()));
        String command = cb.getCommand();
        if (command.length() <= 128) {
            violationInfo.addField(Sentinel.lang.violations.commandBlockExecute.executedCommand, command);
        } else {
            violationInfo.addKeyValue(Sentinel.lang.violations.commandBlockExecute.executedCommand, FileUtils.createCommandLog(command));
        }
        root.addChild(violationInfo);

        return root;
    }
}
