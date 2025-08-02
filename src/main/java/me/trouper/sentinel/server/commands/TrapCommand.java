package me.trouper.sentinel.server.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.OldTXT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandRegistry(value = "sentineltab")
public class TrapCommand implements QuickCommand {


    @Override
    public void dispatchCommand(CommandSender commandSender, Command command, String s, Args args) {
        message(commandSender,Component.text("https://www.youtube.com/watch?v=4F4qzPbcFiA").clickEvent(ClickEvent.openUrl("https://www.youtube.com/watch?v=4F4qzPbcFiA")));
    }

    @Override
    public void dispatchCompletions(CommandSender commandSender, Command command, String s, CompletionBuilder b) {
        b.then(b.arg(main.dir().io.advConfig.fakePlugins));
    }
}
