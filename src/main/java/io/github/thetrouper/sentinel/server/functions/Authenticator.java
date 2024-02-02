package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.server.util.MathUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Authenticator {

    public static String getServerID() {
        try {
            return MathUtils.SHA512(getPublicIPAddress());
        } catch (Exception e) {
            return "NULL";
        }
    }

    public static String authorize(String licenseKey, String serverID) {
        String authStatus = "";

        try {
            URL url = new URL("https://trouper.me/auth/sentinel");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            List<String> lines = readLines(reader);

            for (String line : lines) {
                if (line.contains("data-key")) {
                    String key = extractValue(line, "data-key");
                    String allowedIDs = extractValue(line, "data-allowed");
                    String[] allowedArr = allowedIDs.split(":");

                    if (key.equals(licenseKey)) {
                        if (Arrays.asList(allowedArr).contains(serverID)) {
                            authStatus = "AUTHORIZED";
                            return authStatus;
                        } else {
                            if (Arrays.asList(allowedArr).contains("minehut")) {
                                authStatus = "MINEHUT";
                                return authStatus;
                            }
                            authStatus = "INVALID-ID";
                            return authStatus;
                        }
                    }
                }
            }

            if (authStatus.isEmpty()) {
                authStatus = "UNREGISTERED";
                return authStatus;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return authStatus;
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
