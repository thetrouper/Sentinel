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
import java.util.List;

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
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }
    public static List<String> serverIDS(List<String> strings) {
        return strings.stream().filter(string -> string.contains("<p>")).toList();
    }
    public static boolean hasPaid() throws IOException {
        try {
            URL url = new URL("https://thetrouper.github.io/CUSTOMERS.html");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            List<String> ids = serverIDS(readLines(bufferedReader));
            ids = ArrayUtils.toNewList(ids, string -> string.replaceAll("</p>", "").replaceAll("<p>", "").trim());
            if (!ids.contains(getServerID())) {
                throw new RuntimeException();
            }
            return false;
        } catch (Exception e) {
            throw new IllegalStateException("YOU SHALL NOT PASS! " + getServerID());
        }
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
