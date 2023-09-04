package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Authenticator {

    public Authenticator() throws UnknownHostException {
    }
    private static final String ENCRYPTION_KEY = "lllIIlllIlSentinelAuthIllIllllII";
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String ENCRYPTION_MODE_PADDING = "AES/ECB/PKCS5Padding";
    static InetAddress IP;

    static {
        try {
            IP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static String authorize(String licenseKey, String serverID) {
        String authStatus = "";

        try {
            URL url = new URL("https://sentinelauth.000webhostapp.com/index.html");
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



    public static String getServerID() {
        return encrypt(IP.getHostAddress());
    }


    public static String encrypt(String text) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), ENCRYPTION_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_MODE_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(text.getBytes());
            String encryptedText = bytesToHex(encryptedBytes);;
            return encryptedText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERR";
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
}
