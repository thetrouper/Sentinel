package io.github.thetrouper.sentinel.server.functions;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import io.github.itzispyder.pdk.utils.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.server.util.CipherUtils;
import io.github.thetrouper.sentinel.server.util.ServerUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Telemetry {
    public static String webhook;
    public static void initTelemetryHook() {
        webhook = fetchTelemetryHook();
    }

    public static boolean sendStartupLog() {
        try {
            DiscordWebhook.create()
                    .username("Sentinel Anti-Nuke | Telemetry")
                    .avatar("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png")
                    .addEmbed(DiscordEmbed.create()
                            .author(new DiscordEmbed.Author("Server Startup Log","https://builtbybit.com/resources/sentinel-anti-nuke.30130/",null))
                            .title("A server has started up successfully")
                            .desc("Server " + Sentinel.serverID + "\n" +
                                    Emojis.rightSort + " License: ||" + Sentinel.license + "||\n" +
                                    Emojis.rightSort + " IP: ||" + Sentinel.IP + "||")
                            .color(0x44FF44)
                            .build()
                    ).send(webhook);
            return true;
        } catch (Exception ex) {
            Sentinel.log.info("Failed to initialize dynamic auth!");
            return false;
        }
    }

    public static void sendShutdownLog() {
        try {
            DiscordWebhook.create()
                    .username("Sentinel Anti-Nuke | Telemetry")
                    .avatar("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png")
                    .addEmbed(DiscordEmbed.create()
                            .author(new DiscordEmbed.Author("Server Shutdown Log","https://builtbybit.com/resources/sentinel-anti-nuke.30130/",null))
                            .title("A server has shut down")
                            .desc("Server " + Sentinel.serverID + "\n" +
                                    Emojis.rightSort + " License: ||" + Sentinel.license + "||\n" +
                                    Emojis.rightSort + " IP: ||" + Sentinel.IP + "||")
                            .color(0xFF0000)
                            .build()
                    ).send(webhook);
        } catch (Exception ex) {
            Sentinel.log.info("Failed to send dynamic shutdown!");
        }
    }

    public static String fetchTelemetryHook() {
        try {
            final String webhook = extractWebhook(fetchHtmlContent("https://trouper.me/auth/telemetry"));
            ServerUtils.sendDebugMessage("Original Webhook: " + webhook);

            String webhookIdPart = webhook.replaceAll(".*/(\\d+)/([^/]+.*)$", "/$1/");
            ServerUtils.sendDebugMessage("Webhook ID Part: " + webhookIdPart);

            String encrypted = webhook.replaceAll(".*/\\d+/([^/]+.*)$", "$1");
            ServerUtils.sendDebugMessage("Encrypted Part: " + encrypted);

            String isolated = webhook.replaceAll("/\\d+/([^/]+.*)$", "");
            ServerUtils.sendDebugMessage("Isolated Part: " + isolated);

            String decrypted = isolated + webhookIdPart + CipherUtils.decrypt(encrypted);
            ServerUtils.sendDebugMessage("Decrypted Result: " + decrypted);

            return decrypted;
        } catch (Exception ex) {
            Sentinel.log.warning("FAILED TO LOAD TELEMETRY (Are the servers up?)");
            ex.printStackTrace();
            return "NULL";
        }
    }

    private static String fetchHtmlContent(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        return response.toString();
    }

    private static String extractWebhook(String htmlContent) {
        String pattern = "data-hook=\"(.*?)\"";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(htmlContent);

        if (m.find()) {
            return m.group(1);
        } else {
            return "Webhook ID not found";
        }
    }


}
