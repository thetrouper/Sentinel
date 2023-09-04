package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.server.util.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Telemetry {

    public Telemetry() throws UnknownHostException {
    }
    static InetAddress IP;

    static {
        try {
            IP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static String telemetryHook;

    public static String loadTelemetryHook(String serverID, String licenseKey) {
        String hook = "";
        try {
            URL url = new URL("https://sentinelauth.000webhostapp.com/telemetrykey.html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            List<String> lines = readLines(reader);

            for (String line : lines) {
                if (line.contains("data-id")) {
                    hook = extractValue(line, "data-hook");
                    telemetryHook = hook;
                    Map<String,String> response = sendStartupLog(serverID,licenseKey);

                    if (response.containsKey("SUCCESS")) {
                        Sentinel.log.info("Successfully grabbed telemetry hook");
                        return "SUCCESS";
                    } else {
                        Sentinel.log.info("An Error occurred while attempting to connect to the telemetry hook: " + response.get("ERROR"));
                        return "FAIL";
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    public static Map<String, String> testWebhook(String hook) {
        Map<String, String> response = new HashMap<>();
        response.put("SUCCESS", "NULL");
        DiscordWebhook webhook = new DiscordWebhook(hook);
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Test Success!", "", "")
                .setDescription("Connected to webhook")
                .setColor(Color.GREEN);
        webhook.addEmbed(embed);
        try {
            webhook.execute();
        } catch (IOException e) {
            response.clear();
            response.put("ERROR", e.toString());
            return response;
        }
        return response;
    }

    public static List<String> readLines(BufferedReader reader) {
        try {
            List<String> lines = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();
            return lines;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static String extractValue(String line, String attribute) {
        int start = line.indexOf(attribute + "=\"") + attribute.length() + 2;
        int end = line.indexOf("\"", start);
        return line.substring(start, end);
    }

    public static Map<String, String> sendStartupLog(String serverID, String licenseKey) {
        Map<String, String> response = new HashMap<>();
        response.put("SUCCESS", "NULL");
        DiscordWebhook webhook = new DiscordWebhook(telemetryHook);
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Server Startup Log", "", "")
                .setTitle("Dynamic IP server connected")
                .setDescription("License key: `"+ licenseKey + "`\\n" +
                        "Server ID: `" + serverID + "`")
                .setColor(Color.GREEN);
        webhook.addEmbed(embed);
        try {
            webhook.execute();
        } catch (IOException e) {
            response.clear();
            response.put("ERROR", e.toString());
            return response;
        }
        return response;

    }
    public static Map<String, String> sendShutdownLog(String serverID, String licenseKey) {
        Map<String, String> response = new HashMap<>();
        response.put("SUCCESS", "NULL");
        DiscordWebhook webhook = new DiscordWebhook(telemetryHook);
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Server Shutdown Log", "", "")
                .setTitle("Dynamic IP server disconnected")
                .setDescription("License key: `"+ licenseKey + "`\\n" +
                        "Server ID: `" + serverID + "`")
                .setColor(Color.RED);
        webhook.addEmbed(embed);
        try {
            webhook.execute();
        } catch (IOException e) {
            response.clear();
            response.put("ERROR", e.toString());
            return response;
        }
        return response;

    }
}
