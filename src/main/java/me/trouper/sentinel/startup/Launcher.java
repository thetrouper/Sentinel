package me.trouper.sentinel.startup;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.commands.*;
import me.trouper.sentinel.server.commands.extras.*;
import me.trouper.sentinel.server.events.admin.AntiBanEvents;
import me.trouper.sentinel.server.events.admin.BlockDisplayHideEvent;
import me.trouper.sentinel.server.events.admin.WandEvents;
import me.trouper.sentinel.server.events.extras.ShadowRealmEvents;
import me.trouper.sentinel.server.events.violations.blocks.command.CommandBlockBreak;
import me.trouper.sentinel.server.events.violations.blocks.command.CommandBlockEdit;
import me.trouper.sentinel.server.events.violations.blocks.command.CommandBlockPlace;
import me.trouper.sentinel.server.events.violations.blocks.command.CommandBlockUse;
import me.trouper.sentinel.server.events.violations.blocks.jigsaw.JigsawBlockBreak;
import me.trouper.sentinel.server.events.violations.blocks.jigsaw.JigsawBlockPlace;
import me.trouper.sentinel.server.events.violations.blocks.jigsaw.JigsawBlockUse;
import me.trouper.sentinel.server.events.violations.blocks.structure.StructureBlockBreak;
import me.trouper.sentinel.server.events.violations.blocks.structure.StructureBlockPlace;
import me.trouper.sentinel.server.events.violations.blocks.structure.StructureBlockUse;
import me.trouper.sentinel.server.events.violations.command.DangerousCommand;
import me.trouper.sentinel.server.events.violations.command.LoggedCommand;
import me.trouper.sentinel.server.events.violations.command.SpecificCommand;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartBreak;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartEdit;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartPlace;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartUse;
import me.trouper.sentinel.server.events.violations.players.ChatEvent;
import me.trouper.sentinel.server.events.violations.players.CreativeHotbar;
import me.trouper.sentinel.server.events.violations.players.PluginCloakingEvents;
import me.trouper.sentinel.server.events.violations.players.PluginCloakingPacket;
import me.trouper.sentinel.server.events.violations.whitelist.CommandBlockExecute;
import me.trouper.sentinel.server.functions.chatfilter.profanity.ProfanityFilter;
import me.trouper.sentinel.server.functions.chatfilter.spam.SpamFilter;
import me.trouper.sentinel.server.functions.hotbar.items.RateLimitCheck;
import me.trouper.sentinel.startup.drm.Loader;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Bukkit;

import java.util.List;

public class Launcher {
    public void startup(boolean coldStart) {
        Sentinel.getInstance().getLogger().info("\n]======----- Loading Sentinel! -----======[");


        Sentinel.getInstance().getLogger().info("Starting Up! (%s)...".formatted(Sentinel.getInstance().version));

        registerCommands(coldStart);
        registerEvents();
        startTimers();

        Sentinel.getInstance().getLogger().info("""
                Finished!
                 ____                   __                        ___     \s
                /\\  _`\\                /\\ \\__  __                /\\_ \\    \s
                \\ \\,\\L\\_\\     __    ___\\ \\ ,_\\/\\_\\    ___      __\\//\\ \\   \s
                 \\/_\\__ \\   /'__`\\/' _ `\\ \\ \\/\\/\\ \\ /' _ `\\  /'__`\\\\ \\ \\  \s
                   /\\ \\L\\ \\/\\  __//\\ \\/\\ \\ \\ \\_\\ \\ \\/\\ \\/\\ \\/\\  __/ \\_\\ \\_\s
                   \\ `\\____\\ \\____\\ \\_\\ \\_\\ \\__\\\\ \\_\\ \\_\\ \\_\\ \\____\\/\\____\\
                    \\/_____/\\/____/\\/_/\\/_/\\/__/ \\/_/\\/_/\\/_/\\/____/\\/____/
                     ]====---- Advanced Anti-Grief & Chat Filter ----====[""");
    }

    public void liteStart(String reason) {
        Sentinel.getInstance().getDirector().telemetry.report("Server has launched in lite mode",reason);

        new SentinelCommand().register();

        Sentinel.getInstance().getLogger().info("""
                Finished!
                 ____                   __                        ___     \s
                /\\  _`\\                /\\ \\__  __                /\\_ \\    \s
                \\ \\,\\L\\_\\     __    ___\\ \\ ,_\\/\\_\\    ___      __\\//\\ \\   \s
                 \\/_\\__ \\   /'__`\\/' _ `\\ \\ \\/\\/\\ \\ /' _ `\\  /'__`\\\\ \\ \\  \s
                   /\\ \\L\\ \\/\\  __//\\ \\/\\ \\ \\ \\_\\ \\ \\/\\ \\/\\ \\/\\  __/ \\_\\ \\_\s
                   \\ `\\____\\ \\____\\ \\_\\ \\_\\ \\__\\\\ \\_\\ \\_\\ \\_\\ \\____\\/\\____\\
                    \\/_____/\\/____/\\/_/\\/_/\\/__/ \\/_/\\/_/\\/_/\\/____/\\/____/
                     ]==-- Enabled Lite mode. Go verify your purchase. --==[
                """);


        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(),(task)->{
            if (!Sentinel.getInstance().getDirector().loader.isLite()) {
                task.cancel();
                return;
            }
            Sentinel.getInstance().getLogger().info(Text.removeColors(Loader.LITE_MODE));
        },20,20*60);
    }

    private void registerCommands(boolean coldStart) {
        // Commands
        if (coldStart) new SentinelCommand().register();
        new MessageCommand().register();
        new ReplyCommand().register();
        new ReopCommand().register();
        new CallbackCommand().register();
        new ExtraCommand(
                List.of(
                        new CorruptChunks(),
                        new DeletePlayer(),
                        new DemoScreenCrash(),
                        new EntitySpamCrash(),
                        new KickTroll(),
                        new MessageSpamCrash(),
                        new ShadowRealm(),
                        new SleepyPlayer(),
                        new ViewDistanceCrash(),
                        new BookExtra(),
                        new BlockShuffler()
                )
        ).register();
    }

    private void registerPackets() {
        // Packets
        PacketEvents.getAPI().getEventManager().registerListener(new PluginCloakingPacket(), PacketListenerPriority.NORMAL);
        PacketEvents.getAPI().getEventManager().registerListener(new ShadowRealmEvents(), PacketListenerPriority.HIGHEST);
        PacketEvents.getAPI().getEventManager().registerListener(new CommandBlockEdit(), PacketListenerPriority.NORMAL);
        PacketEvents.getAPI().getEventManager().registerListener(new CommandMinecartEdit(), PacketListenerPriority.NORMAL);
    }

    private void registerEvents() {
        // Events
        new AntiBanEvents().register();
        new CommandBlockExecute().register();
        new CommandMinecartPlace().register();
        new CommandMinecartUse().register();
        new CommandMinecartBreak().register();
        new CommandBlockPlace().register();
        new CommandBlockUse().register();
        new CommandBlockBreak().register();
        new ChatEvent().register();
        new DangerousCommand().register();
        new LoggedCommand().register();
        new SpecificCommand().register();
        new CreativeHotbar().register();
        new TrapCommand().register();
        new PluginCloakingEvents().register();
        new WandEvents().register();
        new JigsawBlockBreak().register();
        new JigsawBlockPlace().register();
        new JigsawBlockUse().register();
        new StructureBlockBreak().register();
        new StructureBlockUse().register();
        new StructureBlockPlace().register();
        new ShadowRealmEvents().register();
        new BlockDisplayHideEvent().register();
        new OnWorldLoad().register();
    }

    private void startTimers() {
        // Scheduled timers
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), SpamFilter::decayHeat,0, 20);
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), ProfanityFilter::decayScore,0,1200);
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), WandEvents::handleDisplay,0,1);
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), new RateLimitCheck()::decayData,0,1200);
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), new RateLimitCheck()::decayItems,0,200);
    }
    
    
}
