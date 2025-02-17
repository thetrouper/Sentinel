package me.trouper.sentinel.startup;

import io.github.itzispyder.pdk.utils.SchedulerUtils;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.commands.*;
import me.trouper.sentinel.server.events.*;
import me.trouper.sentinel.server.functions.chatfilter.profanity.ProfanityFilter;
import me.trouper.sentinel.server.functions.chatfilter.spam.SpamFilter;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Bukkit;

public class Load {

    public static boolean lite = false;

    public static boolean load(String license, String identifier, boolean coldStart) {
        Sentinel.log.info("\n]====---- Requesting Authentication ----====[ \n- License Key: %s\n- Server ID: %s\n".formatted(license,identifier));
        try {
            Sentinel.log.info("Auth Requested...");
            switch (Auth.authorize(license,identifier)) {
                case "AUTHORIZED" -> {
                    Sentinel.log.info("\n]======----- Auth Success! -----======[");
                    startup(coldStart);
                    return true;
                }
                case "MINEHUT" -> {
                    boolean minehutStatus = Telemetry.report("Dynamic IP server has authorized.","Success.");
                    if (minehutStatus) {
                        Sentinel.log.info("Dynamic IP auth Success!");
                        startup(coldStart);
                        return true;
                    } else {
                        Sentinel. log.info("Dynamic IP Failure. Webhook Error possible? Please contact obvWolf to fix this.");
                        if (coldStart) liteStart("How is this even possible?");
                    }
                }
                case "INVALID-ID" -> {
                    Sentinel.log.info("Authentication Failure, You have not whitelisted this server ID yet.");
                    if (coldStart) liteStart("They have not whitelisted their server ID yet. (License exists, no ID)");
                }
                case "UNREGISTERED" -> {
                    Sentinel.log.warning("Authentication Failure, YOU SHALL NOT PASS! License: %s Server ID: %s".formatted(license,identifier));
                    if (coldStart) liteStart("They do not have a license key");
                }
                case "ERROR" -> {
                    Sentinel.log.warning("Hmmmmmm thats not right... License: %s Server ID: %s\nPlease report the above stacktrace.".formatted(license,identifier));
                    if (coldStart) liteStart("An expected error occurred which prevented them from launching");
                }
                default -> {
                    Sentinel.log.warning("Achievement unlocked: How did we get here? License: %s Server ID: %s\nPlease report the above stacktrace.".formatted(license,identifier));
                    if (coldStart) liteStart("An unexpected error occured which prevented them from launching");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Sentinel.log.info("WTFFFF ARE YOU DOING MAN??????");
            if (coldStart) liteStart("An exception was thrown, then caught.");
        }
        return false;
    }

    public static void liteStart(String reason) {
        lite = true;
        Telemetry.report("Server has launched in lite mode",reason);

        new SentinelCommand().register();

        Sentinel.log.info("""
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
            if (lite) {
                Sentinel.log.info(Text.removeColors(SentinelCommand.liteMode));
            }
        });
    }

    public static void startup(boolean coldStart) {
        Sentinel.log.info("\n]======----- Loading Sentinel! -----======[");
        lite = false;

        // Plugin startup logic
        Sentinel.log.info("Starting Up! (%s)...".formatted(Sentinel.getInstance().getDescription().getVersion()));

        // Commands
        if (coldStart) new SentinelCommand().register();
        new MessageCommand().register();
        new ReplyCommand().register();
        new ReopCommand().register();
        new CallbackCommand().register();

        // Events
        new CBEditEvent().register();
        new CBExecuteEvent().register();
        new CBMCPlaceEvent().register();
        new CBMCUseEvent().register();
        new CBPlaceEvent().register();
        new CBUseEvent().register();
        new ChatEvent().register();
        new CommandExecuteEvent().register();
        new CreativeHotbarEvent().register();
        new TrapCommand().register();
        new PluginCloakingEvent().register();

        // Scheduled timers
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), SpamFilter::decayHeat,0, 20);
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), ProfanityFilter::decayScore,0,1200);

        if (Sentinel.mainConfig.backdoorDetection.enabled) BackdoorDetection.init();

        Sentinel.log.info("""
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
}
