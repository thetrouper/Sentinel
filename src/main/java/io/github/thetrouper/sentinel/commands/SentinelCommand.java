/**
 * This file is for tutorial purposes made by ImproperIssues. Distribute if you want :)
 */

package io.github.thetrouper.sentinel.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.FileUtils;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Example command
 */
public class SentinelCommand extends CustomCommand {
    public static boolean debugmode;

    public SentinelCommand() {
        super("sentinel");
        this.setPrintStacktrace(true);
    }

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        switch (args[0]) {
            case "debugmode" -> {
                debugmode = !debugmode;
                p.sendMessage(TextUtils.prefix(TextUtils.boolString(debugmode,"§aEnabled","§cDisabled") + "§7 debug mode."));
            }
        }
    }

    @Override
    public void registerCompletions(CompletionBuilder builder) {
        builder.addCompletion(1,"debugmode");
        builder.addCompletion(1,"whitelistcommandblock");
    }
}
