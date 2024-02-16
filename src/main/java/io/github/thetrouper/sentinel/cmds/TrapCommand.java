package io.github.thetrouper.sentinel.cmds;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.itzispyder.pdk.utils.ArrayUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandRegistry(value = "sentineltab")
public class TrapCommand implements CustomCommand {
    private ArrayList<String> fakePlugins = new ArrayList<>();
    @Override
    public void dispatchCommand(CommandSender commandSender, Args args) {

    }

    @Override
    public void dispatchCompletions(CompletionBuilder b) {
        fakePlugins.clear();
        fakePlugins.add("This server wishes to keep their plugins confidential. Anyways, Enjoy your meteor client! If the owner is incompetent, then .server plugins MassScan should still work ;)");
        fakePlugins.add("NoCheatPlus");
        fakePlugins.add("Negativity");
        fakePlugins.add("Warden");
        fakePlugins.add("Horizon");
        fakePlugins.add("Illegalstack");
        fakePlugins.add("CoreProtect");
        fakePlugins.add("ExploitsX");
        fakePlugins.add("Vulcan (Outdated version frfr)");
        fakePlugins.add("ABC");
        fakePlugins.add("Spartan");
        fakePlugins.add("Kauri");
        fakePlugins.add("AnticheatReloaded");
        fakePlugins.add("WitherAC");
        fakePlugins.add("GodsEye");
        fakePlugins.add("Matrix");
        fakePlugins.add("Wraith");
        fakePlugins.add("AntiXrayHeuristics");
        fakePlugins.add("GrimAC");
        b.then(b.arg(fakePlugins));
    }
}
