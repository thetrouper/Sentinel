package me.trouper.sentinel.startup;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

public class BackdoorDetection {

    public static void init() {
        Sentinel.log.info("Backdoor Detection Enabled, server is running on " + FileUtils.whoAmI());

        Sentinel.log.info("Searching for patchfile at " + Vaccine.PATCH_FILE.getAbsolutePath());
        File folder = new File(Vaccine.PATCH_FILE.getAbsolutePath().replace("\\.patched","").replace("/.patched",""));

        if (!Arrays.toString(folder.listFiles()).contains("\\.patched") && !Arrays.toString(folder.listFiles()).contains("/.patched")) {
            patchServerJar();
            makeSetupFile();
        } else {
            if (Vaccine.PATCH_FILE.delete()) {
                Sentinel.log.info("Patchfile verified successfully.");
            } else {
                Sentinel.log.info("Patchfile verified but not deleted.");
            }
        }

        if (Sentinel.mainConfig.backdoorDetection.setupMode && !Vaccine.SETUP_FILE.exists()) {
            makeSetupFile();
        }
    }

    public static void patchServerJar() {
        File serverJar = new File(FileUtils.whoAmI());
        Sentinel.log.info("Creating a server jar with custom startup...");
        File tempJar = new File(serverJar.getPath() + "-patched");
        try {
            if (Injection.modifyJar(serverJar,Vaccine.class,tempJar)) {
                Sentinel.log.info("Successfully created a server jar with backdoor protection. It is located at %s. Replace your server jar with it to enable executable integrity checks.".formatted(tempJar.getAbsolutePath()));
            } else {
                Sentinel.log.info("Failed to patch your server jar.");
            }
        } catch (Exception e) {
            Sentinel.log.info("Failed to patch your server jar.");
            e.printStackTrace();
        }
    }

    public static void makeSetupFile() {
        Sentinel.log.info("Detected setup mode to be enabled in config, adding setup file.");

        try {
            if (Vaccine.SETUP_FILE.getParentFile().mkdirs() && Vaccine.SETUP_FILE.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(Vaccine.SETUP_FILE));
                writer.write(Vaccine.PASSWORD);
                writer.flush();
                Sentinel.log.info("Successfully written to the file.");
                Sentinel.log.info("Jar file verification will be reset next server reboot. You are now free to add plugins.");
                if (Sentinel.mainConfig.backdoorDetection.keepSetupMode) return;
                Sentinel.mainConfig.backdoorDetection.setupMode = false;
                Sentinel.mainConfig.save();
            } else {
                Sentinel.log.info("Setup file already exists or could not be created.");
            }
        } catch (Exception e) {
            System.err.println("Error enabling setup mode.");
            e.printStackTrace();
        }
    }
}
