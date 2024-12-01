package me.trouper.sentinel.startup;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.AdvancedConfig;
import me.trouper.sentinel.utils.MathUtils;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Auth {

    public static String serverID = "NULL";
    public static String licenseKey = "NULL";
    public static String nonce = "NULL";
    public static String ip = "NULL";
    public static int port = 0;

    public static boolean canLoad() {
        switch (authorize()) {
            case "AUTHORIZED" -> {
                return true;
            }
            case "MINEHUT" -> {
                boolean minehutStatus = Telemetry.initTelemetryHook() && Telemetry.report("Dynamic IP server has initialized.","Successful \"Auth\".");
                if (minehutStatus) {
                    Sentinel.log.info("Dynamic IP auth Success!");
                    return true;
                } else {
                    Sentinel. log.info("Dynamic IP Failure. Webhook Error possible? Please contact obvWolf to fix this.");
                    return false;
                }
            }
        }
        return false;
    }

    public static String authorize() {
        Map<String, List<String>> licenses = getLicenseList();
        if (licenses == null) return "ERROR";

        if (licenses.containsKey(getLicenseKey())) {
            List<String> allowedIDs = licenses.get(getLicenseKey());
            if (allowedIDs.contains(serverID)) {
                return "AUTHORIZED";
            } else if (allowedIDs.contains("minehut")) {
                return "MINEHUT";
            } else {
                return "INVALID-ID";
            }
        }

        return "UNREGISTERED";
    }

    public static String getServerID() {
        if (serverID == null || "NULL".equalsIgnoreCase(serverID)) {
            try {
                serverID = MathUtils.SHA256(getPublicIPAddress() + getPort());
                return serverID;
            } catch (Exception e) {
                return MathUtils.SHA256(getNonce() + getPort());
            }
        }
        return serverID;
    }

    public static Map<String, List<String>> getLicenseList() {
        try {
            String urlString = "http://api.trouper.me:8080/sentinel";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Failed to get response from server, response code: " + responseCode);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            Gson gson = new Gson();

            return gson.fromJson(content.toString(), new TypeToken<HashMap<String, List<String>>>() {}.getType());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getPublicIPAddress() throws IOException {
        String apiUrl = "http://checkip.amazonaws.com";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                ip = response.toString().trim();

                return ip;
            } else {
                throw new IOException("Failed to get public IP address. HTTP error code: " + responseCode);
            }
        } finally {
            connection.disconnect();
        }
    }

    public static int getPort() {
        port = Bukkit.getPort();
        return port;
    }

    public static String getNonce() {
        if (nonce == null || nonce.equals("NULL")) nonce = MathUtils.MD5(AdvancedConfig.nonce);
        return nonce;
    }

    public static String getLicenseKey() {
        licenseKey = Sentinel.mainConfig.plugin.license;
        return licenseKey;
    }
}
