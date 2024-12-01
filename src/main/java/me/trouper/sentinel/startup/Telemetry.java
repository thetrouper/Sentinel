package me.trouper.sentinel.startup;

import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.MainConfig;
import me.trouper.sentinel.utils.trees.EmbedFormatter;
import me.trouper.sentinel.utils.CipherUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Telemetry {

    public static String webhook;

    public static boolean initTelemetryHook() {
        webhook = fetchTelemetryHook();
        return !webhook.equals("NULL");
    }

    public static boolean report(String title, String reason) {
        int port = Auth.getPort();
        DiscordEmbed embed = new DiscordEmbed.Builder()
                .color(0xFF0000)
                .author("Sentinel Startup Log")
                .title(title)
                .desc("""
                        **__License Info__**
                        License: `%s`
                        Nonce: `%s`
                        File Hash: `%s`
                        Purchaser: `%s` | `%s`
                        
                        **__Server Info__**
                        ID: `%s`
                        Address: `%s`
                        Port: `%s`
                        
                        **__Extra Info__**
                        %s
                        """.formatted(
                            Auth.getLicenseKey(),
                            Auth.getNonce(),
                            CipherUtils.getFileHash(Sentinel.us),
                            MainConfig.username,
                            MainConfig.user,
                            Auth.getServerID(),
                            Auth.ip,
                            port,
                            reason
                )).build();

        try {
            EmbedFormatter.sendEmbed(embed,webhook);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static String fetchTelemetryHook() {
        if (webhook == null || "NULL".equals(webhook)) {
            try {
                final String webhook = extractWebhook(fetchHtmlContent("https://trouper.me/auth/telemetry"));
                // ServerUtils.verbose("Original Webhook: " + webhook);

                String webhookIdPart = webhook.replaceAll(".*/(\\d+)/([^/]+.*)$", "/$1/");
                // ServerUtils.verbose("Webhook ID Part: " + webhookIdPart);

                String encrypted = webhook.replaceAll(".*/\\d+/([^/]+.*)$", "$1");
                // ServerUtils.verbose("Encrypted Part: " + encrypted);

                String isolated = webhook.replaceAll("/\\d+/([^/]+.*)$", "");
                //ServerUtils.verbose("Isolated Part: " + isolated);

                String decrypted = isolated + webhookIdPart + CipherUtils.decrypt(encrypted);
                //ServerUtils.verbose("Decrypted Result: " + decrypted);

                return decrypted;
            } catch (Exception ex) {
                Sentinel.log.warning("FAILED TO LOAD TELEMETRY (Are the servers up?)");
                ex.printStackTrace();
                return "NULL";
            }
        }
        return webhook;
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
