package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.auth.Auth;
import io.github.thetrouper.sentinel.cmds.*;
import io.github.thetrouper.sentinel.events.*;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.Bukkit;

public class Load {

    public void load(String license, String serverID) {
        String authstatus = "ERROR";
        String authStatus = "ERROR";
        try {
            authStatus = Authenticator.authorize(license, serverID);
            authstatus = Auth.authorize(license, serverID);
            Sentinel.IP = Authenticator.getPublicIPAddress();
            Sentinel.log.info("Auth Requested...");
        } catch (Exception e) {
            e.printStackTrace();
            Sentinel.log.info("WTFFFF ARE YOU DOING MAN??????");
            liteStart();
        }
        switch (authStatus) {
            case "AUTHORIZED" -> {
                Sentinel.log.info("\n]======----- Auth Success! -----======[");
                startup();
            }
            case "MINEHUT" -> {
                Sentinel.usesDynamicIP = true;
                Telemetry.initTelemetryHook();
                boolean minehutStatus = Telemetry.sendStartupLog();
                if (minehutStatus) {
                    authstatus = authstatus.replaceAll("ur a skid lmao", "get out of here kiddo");
                    ServerUtils.sendDebugMessage(authstatus);
                    Sentinel.log.info("Dynamic IP auth Success! ");
                    this.startup();
                } else {
                    Sentinel. log.info("Dynamic IP Failure. Webhook Error possible? Please contact obvWolf to fix this.");
                    liteStart();
                }
            }
            case "INVALID-ID" -> {
                Sentinel.log.info("Authentication Failure, You have not whitelisted this server ID yet.");
                liteStart();
            }
            case "UNREGISTERED" -> {
                Sentinel.log.warning("Authentication Failure, YOU SHALL NOT PASS! License: %s Server ID: %s".formatted(license,serverID));
                liteStart();
            }
            case "ERROR" -> {
                Sentinel.log.warning("Hmmmmmm thats not right... License: %s Server ID: %s\nPlease report the above stacktrace.".formatted(license,serverID));
                liteStart();
            }
            default -> {
                Sentinel.log.warning("Achievement unlocked: How did we get here? License: %s Server ID: %s\nPlease report the above stacktrace.".formatted(license,serverID));
                liteStart();
            }
        }
    }

    public static boolean lite = false;

    public void liteStart() {
        lite = true;

        Telemetry.initTelemetryHook();
        if (!Telemetry.sendLiteLog()) {
            Sentinel.manager.disablePlugin(Sentinel.getInstance());
            return;
        }

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
    }

    public void startup() {
        Sentinel.log.info("\n]======----- Loading Sentinel! -----======[");

        // Plugin startup logic
        Sentinel.log.info("Starting Up! (%s)...".formatted(Sentinel.getInstance().getDescription().getVersion()));

        // Commands
        new SentinelCommand().register();
        new MessageCommand().register();
        new ReplyCommand().register();
        new ReopCommand().register();
        new SocialSpyCommand().register();
        new ChatClickCallback().register();

        // Events
        new ChatEvent().register();
        new CommandEvent().register();
        new CMDBlockExecute().register();
        new CMDBlockPlace().register();
        new CMDBlockUse().register();
        new CMDMinecartPlace().register();
        new CMDMinecartUse().register();
        new NBTEvents().register();
        new MiscEvents().register();
        if (Sentinel.doNoPlugins) {
            new TrapCommand().register();
            new PluginHiderEvents().register();
            TabCompleteEvent.registerEvent(Sentinel.getInstance());
        }

        // Scheduled timers
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), AntiSpam::decayHeat,0, 20);
        Bukkit.getScheduler().runTaskTimer(Sentinel.getInstance(), ProfanityFilter::decayScore,0,1200);
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
