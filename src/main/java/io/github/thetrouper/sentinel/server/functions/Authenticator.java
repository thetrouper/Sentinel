package io.github.thetrouper.sentinel.server.functions;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.github.thetrouper.sentinel.server.util.MathUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Authenticator {

    public static String authorize(String licenseKey, String serverID) {
        Map<String,List<String>> licenses = getLicenseList();

        if (licenses.containsKey(licenseKey)) {
            List<String> allowedIDs = licenses.get(licenseKey);
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
        try {
            return MathUtils.SHA512(getPublicIPAddress());
        } catch (Exception e) {
            return "NULL";
        }
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

                return response.toString().trim();
            } else {
                throw new IOException("Failed to get public IP address. HTTP error code: " + responseCode);
            }
        } finally {
            connection.disconnect();
        }
    }
}
