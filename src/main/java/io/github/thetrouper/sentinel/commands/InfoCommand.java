/**
 * This file is for tutorial purposes made by ImproperIssues. Distribute if you want :)
 */

package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.discord.WebhookSender;
import io.github.thetrouper.sentinel.exceptions.CmdExHandler;
import io.github.thetrouper.sentinel.server.functions.AntiSpam;
import io.github.thetrouper.sentinel.server.util.FileUtils;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Example command
 */
public class InfoCommand implements TabExecutor {
    public static boolean debugmode;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (args.length == 0) {
                sender.sendMessage(TextUtils.prefix("&cYou must specify an item to give."));
                return true;
            }
            switch (args[0]) {
                case "debugmode" -> {
                debugmode = !debugmode;
                sender.sendMessage(TextUtils.prefix("Debug mode set to §b" + debugmode));
                }
                case "webhooktest" -> {
                    sender.sendMessage(TextUtils.prefix("Testing the webhook..."));
                    WebhookSender.sendEmbedWarning(sender.getName(), "/sentinel webhooktest",true,true,false);
                }
                case "checkheat" -> {
                    sender.sendMessage(TextUtils.prefix("Your heat is §e" + AntiSpam.heatMap.get(sender).toString()));
                }
                case "dispatchtest" -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say " + " ]=- Sentinel Anti-Grief -=[ You have been banned for attempting a dangerous command. Contact an administrator if you believe this to be a mistake.");
                    ServerUtils.sendCommand("say test complete!");
                }
                case "pastebintest" -> {
                    WebhookSender.sendTestEmbed();
                }
                case "filetest" -> {
                    sender.sendMessage(TextUtils.prefix("testing file stuff"));
                    if (!FileUtils.folderExists("/LoggedNBT")) {
                        FileUtils.createFolder("/LoggedNBT");
                    }
                    FileUtils.createNBTLog("{[IDFK MINECRAFT NBT LMAO]}; [][]\\ima just fill this with text to test it. ()()() ||||| &^%$#@!@#$%^&*(){}|:\"\"<><>");
                }
            }
            return true;
        } catch (Exception ex) {
            CmdExHandler handler = new CmdExHandler(ex,command);
            sender.sendMessage(handler.getErrorMessage());
            ex.printStackTrace();
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new TabComplBuilder(sender,command,alias,args)
                .add(1,new String[]{
                        "debugmode",
                        "webhooktest",
                        "checkheat",
                        "dispatchtest",
                        "pastebintest",
                        "filetest"
                }).build();
    }
}
