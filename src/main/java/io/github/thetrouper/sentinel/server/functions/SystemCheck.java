package io.github.thetrouper.sentinel.server.functions;

import io.github.itzispyder.pdk.utils.SchedulerUtils;
import io.github.itzispyder.pdk.utils.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.cmds.SocialSpyCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class SystemCheck {
    public static void fullCheck(Player p) {
        chatCheck(p);

    }
    public static void chatCheck(Player p) {
        SocialSpyCommand.spyMap.put(p.getUniqueId(),true);
        AsyncPlayerChatEvent swear = new AsyncPlayerChatEvent(true,p,"Sentinel AntiSwear check > Fvck", Set.of(p));
        AsyncPlayerChatEvent spam = new AsyncPlayerChatEvent(true,p,"Sentinel AntiSpam Check", Set.of(p));
        ProfanityFilter.handleProfanityFilter(swear);
        SchedulerUtils.loop(10,5, (loop)->{
            AntiSpam.lastMessageMap.put(p,"Sentinel AntiSpam Check");
            AntiSpam.handleAntiSpam(spam);
        });


        Message.messagePlayer(p,p,"Sentinel Automatic System Check > Private Message");

    }
}
