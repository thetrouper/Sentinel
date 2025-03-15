package me.trouper.sentinel.startup.drm;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.AdvancedConfig;
import me.trouper.sentinel.utils.HashUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Auth {

    public String authorize(String license, String identifier) {
        if (true) {
            return "AUTHORIZED";
        }
        Map<String, List<String>> licenses = getLicenseList();
        if (licenses == null) return "ERROR";

        if (licenses.containsKey(license)) {
            List<String> allowedIDs = licenses.getOrDefault(license,new ArrayList<>());
            if (allowedIDs.contains(identifier)) {
                return "AUTHORIZED";
            } else if (allowedIDs.contains("minehut")) {
                return "MINEHUT";
            } else {
                return "INVALID-ID";
            }
        }

        return "UNREGISTERED";
    }

    /**
     * This should be the last auth variable called.
     * @return the unique identifier of the server
     */
    public String getServerID() {
        return HashUtils.SHA256(Sentinel.getInstance().nonce + Sentinel.getInstance().ip + Sentinel.getInstance().port);
    }

    public Map<String, List<String>> getLicenseList() {
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

    public String getNonce() {
        return HashUtils.MD5(Sentinel.getInstance().getDirector().io.advConfig.nonce);
    }

    public String getLicenseKey() {
        return Sentinel.getInstance().getDirector().io.mainConfig.plugin.license;
    }
}
