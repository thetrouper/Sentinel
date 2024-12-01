package me.trouper.sentinel.server.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandRegistry(value = "sentineltab")
public class TrapCommand implements CustomCommand {


    @Override
    public void dispatchCommand(CommandSender commandSender, Command command, String s, Args args) {
        commandSender.sendMessage(Component.text(Text.prefix("https://www.youtube.com/watch?v=4F4qzPbcFiA")).clickEvent(ClickEvent.openUrl("https://www.youtube.com/watch?v=4F4qzPbcFiA")));
    }

    @Override
    public void dispatchCompletions(CommandSender commandSender, Command command, String s, CompletionBuilder b) {
        ServerUtils.verbose("Listing the fake plugins: %s".formatted(Sentinel.advConfig.fakePlugins));
        b.then(b.arg(Sentinel.advConfig.fakePlugins));
    }
}
