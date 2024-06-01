package io.github.thetrouper.sentinel.cmds;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.thetrouper.sentinel.Sentinel;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandRegistry(value = "sentineltab")
public class TrapCommand implements CustomCommand {
    private final List<String> fakePlugins = Sentinel.advConfig.fakePlugins;
    @Override
    public void dispatchCommand(CommandSender commandSender, Args args) {

    }

    @Override
    public void dispatchCompletions(CompletionBuilder b) {
        b.then(b.arg(fakePlugins));
    }
}
