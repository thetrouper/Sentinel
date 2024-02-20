package io.github.thetrouper.sentinel.cmds;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.cmdblocks.WhitelistedBlock;
import io.github.thetrouper.sentinel.events.ChatEvent;
import io.github.thetrouper.sentinel.server.functions.*;
import io.github.thetrouper.sentinel.server.util.CipherUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CommandRegistry(value = "sentinel",permission = @Permission("sentinel.debug"),printStackTrace = true)
public class SentinelCommand implements CustomCommand {
    public static boolean debugMode;
    @Override
    public void dispatchCommand(CommandSender commandSender, Args args) {
        Player p = (Player) commandSender;
        Sentinel instance = Sentinel.getInstance();
        switch (args.get(0).toString()) {
            case "commandblock", "cb" -> handleCommandBlock(p,args);
            case "reload" -> {
                if (!Sentinel.isTrusted(p)) return;
                p.sendMessage(Text.prefix("Reloading Sentinel!"));
                Sentinel.log.info("[Sentinel] Re-Initializing Sentinel!");
                instance.loadConfig();
            }
            case "full-system-check" -> {
                p.sendMessage(Text.prefix("Initiating a full system check!"));
                SystemCheck.fullCheck(p);
            }
            case "debug" -> handleDebugCommand(p,args);
        }
    }

    private void handleCommandBlock(Player p, Args args) {
        Block target = p.getTargetBlock(Set.of(Material.AIR),10);
        switch (args.get(1).toString()) {
            case "add" -> {
                if (target.getType().equals(Material.COMMAND_BLOCK) || target.getType().equals(Material.REPEATING_COMMAND_BLOCK) || target.getType().equals(Material.CHAIN_COMMAND_BLOCK)) {
                    CommandBlock cb = (CommandBlock) target.getState();
                    CMDBlockWhitelist.add(cb,p.getUniqueId());
                    p.sendMessage(Text.prefix("Successfully whitelisted a &b" + Text.blockName(cb.getType().toString()) + "&7 with the command &a" + cb.getCommand() + "&7."));
                    return;
                }
                p.sendMessage(Text.prefix("Could not whitelist the &b" + Text.blockName(target.getType().toString()) + "&7 it is not a command block!"));
            }
            case "remove" -> {
                WhitelistedBlock wb = CMDBlockWhitelist.get(target.getLocation());
                if (wb != null) {
                    CMDBlockWhitelist.remove(target.getLocation());
                    p.sendMessage(Text.prefix("Successfully removed 1 &b" + Text.blockName(WhitelistedBlock.fromSerialized(wb.loc()).getBlock().getType().toString()) + "&7 with the command &a" + wb.command() + "&7."));
                    return;
                }
                p.sendMessage(Text.prefix("Could not un-whitelist the &b" + Text.blockName(target.getType().toString()) + "&7 it wasn't whitelisted in the first place!"));
            }
        }
    }

    private void handleDebugCommand(Player p, Args args) {
        switch (args.get(1).toString()) {
            case "lang" -> {
                p.sendMessage(Sentinel.language.get("exmaple-message"));
            }
            case "toggle" -> {
                debugMode = !debugMode;
                p.sendMessage(Text.prefix((debugMode ? "Enabled" : "Disabled") + " debug mode."));
            }
                    /*case "encrypt" -> {
                        final String enc = CipherUtils.encrypt(args.getAll(2).toString());
                        final String check = CipherUtils.decrypt(enc);
                        final String main = Text.prefix("Successfully encrypted \"&e" + check + "&7\" using AES.\n &7> &b" + enc);
                        Sentinel.log.info(args.getAll(2).toString() + "\n" + enc + "\n" + check);
                        TextComponent message = new TextComponent();
                        message.setText(main);
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text("&bClick to copy!")));
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, enc));
                        p.spigot().sendMessage(message);
                    }*/
            case "chat" -> {
                AsyncPlayerChatEvent message = new AsyncPlayerChatEvent(true,p,args.getAll(1).toString(), Set.of(p));
                AdvancedBlockers.handleAdvanced(message);
                AntiSpam.handleAntiSpam(message);
                ProfanityFilter.handleProfanityFilter(message);
            }
        }
    }

    @Override
    public void dispatchCompletions(CompletionBuilder b) {
        b.then(b.arg("reload","full-system-check"));
        b.then(b.arg("debug").then(
                b.arg("lang","toggle","chat")));
        b.then(b.arg("commandblock"));
    }
}
