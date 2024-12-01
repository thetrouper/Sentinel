package me.trouper.sentinel.startup;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.CipherUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AntiPiracy {

    public static boolean sailTheSevenSeas() {
        Sentinel.log.info("Docking Vesel");
        File sentinel = Sentinel.us;
        Sentinel.log.info("Obtaining Gold");
        String hash = CipherUtils.getFileHash(sentinel);
        Sentinel.log.info("Checking for fake coin");
        if (hash == null) {
            Sentinel.log.info("Counterfeiter!");
            return true;
        }

        Set<String> allowedHashes = getHashList();
        if (allowedHashes == null || allowedHashes.isEmpty()) {
            Sentinel.log.info("No Booty?");
            return true;
        }

        if (allowedHashes.contains(hash)) {
            Sentinel.log.info("Checkpoint 1 Complete");
            return false;
        } else {
            Sentinel.log.info("Well,");
            return true;
        }

    }

    public static Set<String> getHashList() {
        Sentinel.log.info("Initializing East India Protocol");
        try {
            String urlString = "http://api.trouper.me:8080/sentinel-hashes";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            Sentinel.log.info("Bargaining with merchants");

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

            Sentinel.log.info("Counting Coins");
            Gson gson = new Gson();

            return gson.fromJson(content.toString(), new TypeToken<Set<String>>() {}.getType());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
