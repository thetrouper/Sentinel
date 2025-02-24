package me.trouper.sentinel.startup;

import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import io.github.itzispyder.pdk.utils.misc.Timer;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.MainConfig;
import me.trouper.sentinel.utils.CipherUtils;
import me.trouper.sentinel.utils.trees.EmbedFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Telemetry {

    private static String webhook;
    private static final String API_URL = "http://api.trouper.me:8080/api/webhook";
    private static final String API_TOKEN = "this-really-isnt-needed";

    public static boolean report(String title, String reason) {
        try {
            if (webhook == null || webhook.isBlank()) webhook = fetchTelemetryHook();

            DiscordEmbed embed = new DiscordEmbed.Builder()
                    .color(0xFF0000)
                    .author("Sentinel Startup Log")
                    .title(title)
                    .desc("""
                            **__License Info__**
                            License: `%s`
                            Nonce: `%s`
                            Purchaser: `%s` | `%s`
                            
                            **__Server Info__**
                            ID: `%s`
                            Address: `%s`
                            Port: `%s`
                            
                            **__Extra Info__**
                            %s
                            """.formatted(
                                Sentinel.getInstance().license,
                                Sentinel.getInstance().nonce,
                                MainConfig.username,
                                MainConfig.user,
                                Sentinel.getInstance().identifier,
                                Sentinel.getInstance().ip,
                                Sentinel.getInstance().port,
                                reason
                    )).build();


            EmbedFormatter.sendEmbed(embed,webhook);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static String fetchTelemetryHook() throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", API_TOKEN)
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new TimeoutException("Failed to get webhook (Status Code): " + response.statusCode());
        }
    }
}
