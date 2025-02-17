package me.trouper.sentinel.startup;

import org.apache.commons.lang3.StringUtils;

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
    /*
    Below is a discussion of several flaws in the design and implementation of this “Vaccine” class. In short, while the idea is to “lock down” the server’s jar files, there are several ways an attacker (or even an inadvertent change) could cause the protection to fail or be bypassed. Here are some of the main issues:

---

### 1. Use of MD5 for Integrity Checking

- **Weak Hash Algorithm:**
  The code uses MD5 to calculate file checksums. MD5 is now considered cryptographically broken and subject to collision attacks. An attacker with the skills to create a jar file that collides with a legitimate MD5 hash might be able to bypass the integrity check.

- **Collision Attacks:**
  Since MD5 is not collision resistant, it may be possible to generate two different jar files that yield the same MD5 hash. This makes the “fingerprint” of a jar file untrustworthy.

---

### 2. Hard-Coded Password and Key Management

- **Static Password:**
  The password `"SentinelAntiNuke"` is hard-coded. An attacker who reverse engineers your plugin or reads the source (or decompiles the bytecode) will know the “secret” used for both the setup file check and for encrypting/decrypting the checksum file.

- **Setup Mode Abuse:**
  The existence of a “setup” file (with the same hard-coded password inside) causes the plugin to clear the existing checksums and re-register all jar files. An attacker who can write to the server directory could create such a file (or modify it) and force the plugin to re-register even malicious files.

---

### 3. Insecure Encryption Practices

- **AES in ECB Mode:**
  In your `getCipher` method you call `Cipher.getInstance("AES")`, which in many Java versions defaults to `"AES/ECB/PKCS5Padding"`. ECB (Electronic Codebook) mode is not semantically secure – it does not use an IV, and identical blocks of plaintext result in identical ciphertext. While you are not encrypting extremely sensitive data here, it is still not a best practice.

- **Key Derivation:**
  The key is derived by taking the SHA‑256 hash of the password and using the first 16 bytes. This is not as robust as using a proper key derivation function (like PBKDF2) with a salt and many iterations. Although the password is fixed, this makes it trivial for an attacker to derive the same key.

- **Checksum File Tampering:**
  The checksum file (`.checksums.lock`) is encrypted with a key that is easily re-derived from the hard-coded password. An attacker who can access the filesystem can decrypt, modify, and re-encrypt this file to “legitimize” malicious jar files.

---

### 4. Mapping by File Name Only

- **Insufficient Uniqueness:**
  The plugin uses the jar’s filename (using `file.getName()`) as the key in the checksum map. This means that if there are two jar files with the same name in different directories, they will collide in the map. An attacker might take advantage of this by placing a malicious jar in a different directory under a known-good name.

- **Case Sensitivity Issues:**
  The code checks for files ending in `".jar"` (a case‑sensitive match). A file named `"plugin.JAR"` (with different casing) would be skipped by the integrity check.

---

### 5. Deserialization of Encrypted Data

- **Use of Object Serialization:**
  The checksum file is stored as a serialized Java object. Even though it is encrypted, once an attacker can compute the key (which they easily can due to the hard-coded password), they might be able to craft malicious serialized data. In Java, deserialization of untrusted data can lead to remote code execution if there is a gadget chain available.

---

### 6. File Operation Issues

- **Renaming for Quarantine:**
  When a jar file fails the checksum test, the plugin attempts to “quarantine” it by renaming it with a `.quarantined` suffix. However, the code does not check whether the rename operation succeeded. If it fails, the jar might still be loaded.

- **Recursive Directory Traversal:**
  The recursion into subdirectories does not protect against cycles (e.g. via symbolic links). Although this is more of a robustness/usability concern than a direct security issue, it could be exploited in some situations.

---

### 7. General Trust Model Problems

- **Reliance on the Filesystem:**
  The entire scheme is based on file checksums stored on disk. If an attacker already has the ability to modify files on the server, they can simply change the checksum file (or the jar files and then re-run the setup process) to bypass your integrity checks.

- **No External Trust Anchor:**
  There is no digital signature or external verification. Everything depends on locally stored values that, once known, are trivial for an attacker to forge.

---

### **Summary**

While the intent of “injecting” code to check MD5 sums of jar files might seem to raise the bar against backdoors, the design is fundamentally flawed:

- **MD5 is too weak** to trust for integrity checking.
- **A hard-coded password** means that the “secret” is public to anyone who looks at the code.
- **The encryption method (AES in ECB mode, with a trivial key derivation)** is not sufficient to protect the checksum file from tampering.
- **Mapping by just filename** (with case‑sensitivity issues) opens up additional attack vectors.
- **The re‑registration process** (triggered by a setup file) can be abused by an attacker with filesystem access.

Any attacker who has access to the server’s filesystem (or who can upload files) can defeat these measures by simply regenerating the checksums after introducing malicious code.

---

### **Recommendations**

- **Use a stronger hash:** Replace MD5 with SHA‑256 (or better) for file integrity.
- **Improve key management:** Avoid hard-coding passwords; consider using a secure configuration that isn’t embedded in code.
- **Use a secure cipher mode:** Switch from ECB to a mode such as CBC or GCM and use a proper IV.
- **Include full paths:** When mapping files, use their canonical paths (or a similar unique identifier) rather than just the filename.
- **Avoid unsafe deserialization:** If you must persist data, use a safer data format (such as JSON or XML) and validate it.
- **Add a digital signature:** Instead of (or in addition to) simple checksums, sign your jar files with a trusted certificate.

By addressing these issues, you would significantly improve the robustness of your server’s integrity checking mechanism.
     */

    public static final String PASSWORD = "%%__TIMESTAMP__%%"; // This can be replaced with a dynamic password input method
    private static final File FOLDER = new File(".sentinel/");
    private static final File CHECKSUM_FILE = new File(FOLDER, ".checksums.lock");
    public static final File SETUP_FILE = new File(FOLDER, ".checksums.setup"); // Setup file to reset checksums
    public static final File PATCH_FILE = new File(FOLDER, ".patched"); // PatchFile name

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
