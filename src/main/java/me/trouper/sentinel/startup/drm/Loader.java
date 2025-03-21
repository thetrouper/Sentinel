package me.trouper.sentinel.startup.drm;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.itzispyder.pdk.utils.SchedulerUtils;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.commands.*;
import me.trouper.sentinel.server.events.admin.AntiBanEvents;
import me.trouper.sentinel.server.events.admin.BlockDisplayHideEvent;
import me.trouper.sentinel.server.events.admin.WandEvents;
import me.trouper.sentinel.server.events.violations.blocks.command.*;
import me.trouper.sentinel.server.events.violations.blocks.jigsaw.JigsawBlockBreak;
import me.trouper.sentinel.server.events.violations.blocks.jigsaw.JigsawBlockPlace;
import me.trouper.sentinel.server.events.violations.blocks.jigsaw.JigsawBlockUse;
import me.trouper.sentinel.server.events.violations.blocks.structure.StructureBlockBreak;
import me.trouper.sentinel.server.events.violations.blocks.structure.StructureBlockPlace;
import me.trouper.sentinel.server.events.violations.blocks.structure.StructureBlockUse;
import me.trouper.sentinel.server.events.violations.command.DangerousCommand;
import me.trouper.sentinel.server.events.violations.command.LoggedCommand;
import me.trouper.sentinel.server.events.violations.command.SpecificCommand;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartEdit;
import me.trouper.sentinel.server.events.violations.players.*;
import me.trouper.sentinel.server.events.violations.whitelist.CommandBlockExecute;
import me.trouper.sentinel.server.events.extras.ShadowRealmEvents;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartBreak;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartPlace;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartUse;
import me.trouper.sentinel.server.functions.chatfilter.profanity.ProfanityFilter;
import me.trouper.sentinel.server.functions.chatfilter.spam.SpamFilter;
import me.trouper.sentinel.server.functions.hotbar.items.RateLimitCheck;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Bukkit;

public final class Loader {

    private boolean lite = false;

    public static final String LITE_MODE = Text.color("""
                        &8]=-&f Welcome to &d&lSentinel &7|&f Anti-Nuke &8-=[
                        &7The plugin is currently loaded in &clite&7 mode.
                        
                        &7Your License Key is &a%s&7.
                        &7Your server ID is &6%s&7.
                        
                        &fIf you have just &apurchased&f the plugin:
                         &8- &7Join the &b&ndiscord&r&7 and open a ticket.
                         &8- &7https://discord.gg/Xh6BAzNtxY
                         &8- &7You will then receive a license key.
                        &fIf you have &cnot&f purchased the plugin:
                         &8- &7Then purchase it :D
                         &8- &7It wont do anything in this state!
                         &8- &7(Its only 5$)
                        &fIf you are reading this from a decompiler:
                         &8- &7Please stop trying to crack the plugin and purchase it!
                         &8- &7Your time spent trying to bypass my DRM could be spent at a minimum wage job.
                         &8- &7There you will make 7$ an hour! (As oppose to 5$ for multiple hours of cracking)
                        &fWoah! You read quite far!
                         &8- &7Want the plugin for cheaper, &nor even for free&r&7?
                         &8- &7DM &b@obvwolf&7 on discord and lets make a deal!
                        """.formatted(Sentinel.getInstance().license,Sentinel.getInstance().identifier));

    public boolean load(String license, String identifier, boolean coldStart) {
        Sentinel.getInstance().getLogger().info("\n]====---- Requesting Authentication ----====[ \n- License Key: %s\n- Server ID: %s\n".formatted(license,identifier));
        try {
            Sentinel.getInstance().getLogger().info("Auth Requested...");
            switch (Sentinel.getInstance().getDirector().auth.authorize(license,identifier)) {
                case "AUTHORIZED" -> {
                    Sentinel.getInstance().getLogger().info("\n]======----- Auth Success! -----======[");
                    startup(coldStart);
                    return true;
                }
                case "MINEHUT" -> {
                    boolean minehutStatus = Sentinel.getInstance().getDirector().telemetry.report("Dynamic IP server has authorized.","Success.");
                    if (minehutStatus) {
                        Sentinel.getInstance().getLogger().info("Dynamic IP auth Success!");
                        startup(coldStart);
                        return true;
                    } else {
                        Sentinel.getInstance().getLogger().info("Dynamic IP Failure. Make sure telemetry is enabled in main-config.json. If it still doesn't work, contact a developer.");
                        if (coldStart) liteStart("How is this even possible?");
                    }
                }
                case "INVALID-ID" -> {
                    Sentinel.getInstance().getLogger().info("Authentication Failure, You have not whitelisted this server ID yet.");
                    if (coldStart) liteStart("They have not whitelisted their server ID yet. (License exists, no ID)");
                }
                case "UNREGISTERED" -> {
                    Sentinel.getInstance().getLogger().warning("Authentication Failure, YOU SHALL NOT PASS! License: %s Server ID: %s".formatted(license,identifier));
                    if (coldStart) liteStart("They do not have a license key");
                }
                case "ERROR" -> {
                    Sentinel.getInstance().getLogger().warning("Hmmmmmm thats not right... License: %s Server ID: %s\nPlease report the above stacktrace.".formatted(license,identifier));
                    if (coldStart) liteStart("An expected error occurred which prevented them from launching");
                }
                default -> {
                    Sentinel.getInstance().getLogger().warning("Achievement unlocked: How did we get here? License: %s Server ID: %s\nPlease report the above stacktrace.".formatted(license,identifier));
                    if (coldStart) liteStart("An unexpected error occured which prevented them from launching");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Sentinel.getInstance().getLogger().info("WTFFFF ARE YOU DOING MAN??????");
            if (coldStart) liteStart("An exception was thrown, then caught.");
        }
        return false;
      
    }

    public void liteStart(String reason) {
        setLite(true);
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


        SchedulerUtils.repeat(20*60,()->{
            if (Sentinel.getInstance().getDirector().loader.isLite()) {
                Sentinel.getInstance().getLogger().info(Text.removeColors(Loader.LITE_MODE));
            }
        });
    }

    public void startup(boolean coldStart) {
        Sentinel.getInstance().getLogger().info("\n]======----- Loading Sentinel! -----======[");
        setLite(false);

        // Plugin startup logic
        Sentinel.getInstance().getLogger().info("Starting Up! (%s)...".formatted(Sentinel.getInstance().getDescription().getVersion()));

        // Commands
        if (coldStart) new SentinelCommand().register();
        new MessageCommand().register();
        new ReplyCommand().register();
        new ReopCommand().register();
        new CallbackCommand().register();
        new ExtraCommand().register();

        // Packets
        PacketEvents.getAPI().getEventManager().registerListener(new PluginCloakingPacket(), PacketListenerPriority.NORMAL);
        PacketEvents.getAPI().getEventManager().registerListener(new ShadowRealmEvents(), PacketListenerPriority.HIGHEST);
        PacketEvents.getAPI().getEventManager().registerListener(new CommandBlockEdit(), PacketListenerPriority.NORMAL);
        PacketEvents.getAPI().getEventManager().registerListener(new CommandMinecartEdit(), PacketListenerPriority.NORMAL);

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

        // Scheduled timers
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), SpamFilter::decayHeat,0, 20);
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), ProfanityFilter::decayScore,0,1200);
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), WandEvents::handleDisplay,0,1);
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), new RateLimitCheck()::decayData,0,1200);
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), new RateLimitCheck()::decayItems,0,200);
        
        if (Sentinel.getInstance().getDirector().io.mainConfig.backdoorDetection.enabled) Sentinel.getInstance().getDirector().backdoorDetection.init();

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

    public boolean isLite() {
        return lite;
    }

    public void setLite(boolean lite) {
        this.lite = lite;
    }
}
