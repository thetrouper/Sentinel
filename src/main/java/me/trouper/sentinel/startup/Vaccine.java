package me.trouper.sentinel.startup;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Vaccine implements Runnable {

    public static final String PASSWORD = "%%__TIMESTAMP__%%";
    private static final File FOLDER = new File(".sentinel/");
    private static final File CHECKSUM_FILE = new File(FOLDER, ".checksums.lock");
    public static final File SETUP_FILE = new File(FOLDER, ".checksums.setup");
    public static final File PATCH_FILE = new File(FOLDER, ".patched");

    private static Map<File, String> fileChecksums = new HashMap<>();

    @Override
    public void run() {
        System.out.println("This server is protected by Sentinel Anti-Nuke.");
        if (!FOLDER.exists() && !FOLDER.mkdirs()) {
            System.out.println("Failed to make directories.");
            System.exit(-1);
            return;
        }

        try {
            if (PATCH_FILE.exists() || PATCH_FILE.createNewFile()) {
                System.out.println("Patchfile verified successfully.");
            } else {
                System.out.println("Unable to verify patch file. Sentinel may re-inject me! If you see this message twice in one startup, delete your server jar and try again.");
            }

            File dir = new File(System.getProperty("user.dir"));
            // Check if the setup file exists and validate the password
            if (loadChecksums()) {
                System.out.println("Successfully loaded checksums.");
            } else {
                System.out.println("This error should only occur on first startup of the custom server jar.");
            }
            if ((SETUP_FILE.exists())) {
                if (validatePassword(SETUP_FILE)) {
                    System.out.println("Entering setup mode.");
                    clearChecksums();
                    System.out.println("Registering executable checksums.");
                    registerFiles(dir);
                    if (!SETUP_FILE.delete()) {
                        System.out.println("Setup finished. Mode exited.");
                    } else {
                        System.out.println("Setup finished. Could not exit setup mode, please delete \".sentinel/.checksums.setup\" manually.");
                    }
                } else {
                    System.out.println("Invalid password in setup file.");
                }
            } else {
                System.out.println("Setup file not found.");
            }

            // Verify the integrity of .jar files
            System.out.println("Verifying executable integrity.");
            verifyJarFiles(dir);

            // Save checksums to encrypted file
            saveChecksums();
            System.out.println("Saved checksums.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Validates the password in the .checksums.setup file
    private static boolean validatePassword(File setupFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(setupFile))) {
            String storedPassword = reader.readLine();
            return storedPassword != null && storedPassword.equals(PASSWORD);
        }
    }

    // Clears the checksum map
    private static void clearChecksums() {
        fileChecksums.clear();
    }

    // Registers all .jar files in the directory into the checksum map
    private static void registerFiles(File dir) throws Exception {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively call the method on subdirectories
                    registerFiles(file.getAbsoluteFile());
                } else if (file.getName().toLowerCase().endsWith(".jar")) {
                    String md5 = calculateMD5(file);
                    System.out.printf("%s -> %s%n", file.getAbsolutePath(), md5);
                    fileChecksums.put(file, md5);
                }
            }
        }
    }

    // Verifies the integrity of .jar files by checking their MD5 checksum
    private static void verifyJarFiles(File dir) throws Exception {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively call the method on subdirectories
                    verifyJarFiles(file);
                } else if (file.getName().toLowerCase().endsWith(".jar")) {
                    String storedMD5 = fileChecksums.get(file.getAbsoluteFile());
                    String currentMD5 = calculateMD5(file);
                    System.out.printf("Checking %s, Sum: %s%n", file.getAbsoluteFile(), currentMD5);
                    if (storedMD5 == null || !storedMD5.equals(currentMD5)) {
                        // If the MD5 doesn't match or the file is not registered, quarantine it
                        System.out.printf("%s has an invalid checksum. It has been quarantined.%n", file.getName());
                        quarantineFile(file);
                    }
                }
            }
        }
    }


    // Quarantines the file by renaming it with .quarantined suffix
    private static void quarantineFile(File file) {
        File quarantinedFile = new File(file.getParent(), file.getName() + ".quarantined");
        file.renameTo(quarantinedFile);
    }

    // Calculates the MD5 hash of a file
    private static String calculateMD5(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
        }
        byte[] hashBytes = md.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    // Saves the current file checksums to an encrypted file
    private static void saveChecksums() throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new CipherOutputStream(
                new FileOutputStream(CHECKSUM_FILE),
                getCipher(Cipher.ENCRYPT_MODE)))) {
            oos.writeObject(fileChecksums);
        }
    }

    // Loads checksums from the encrypted .checksums.lock file
    private static boolean loadChecksums() {
        try (ObjectInputStream ois = new ObjectInputStream(new CipherInputStream(
                new FileInputStream(CHECKSUM_FILE),
                getCipher(Cipher.DECRYPT_MODE)))) {
            fileChecksums = (Map<File, String>) ois.readObject();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Returns a Cipher instance for the specified mode (encryption or decryption)
    private static Cipher getCipher(int mode) throws Exception {
        // Generate a hashed key from the password using SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedPassword = digest.digest(PASSWORD.getBytes());

        // Create AES key from the hashed password
        SecretKeySpec key = new SecretKeySpec(Arrays.copyOf(hashedPassword, 16), "AES"); // Using the first 16 bytes of the SHA-256 hash
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, key);
        return cipher;
    }
}
