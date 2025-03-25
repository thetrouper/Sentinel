package me.trouper.sentinel.utils;

import io.github.itzispyder.pdk.utils.FileValidationUtils;
import me.trouper.sentinel.Sentinel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class FileUtils {

    public static String whoAmI() {
        String classPath = System.getProperty("java.class.path");
        String[] parts = classPath.split(File.pathSeparator);
        for (String part : parts) {
            if (part.endsWith(".jar")) {
                return new File(part).getName();
            }
        }
        return "Unknown.jar";
    }

    public static boolean fileExists(File file) {
        try {
            if (!file.getParentFile().exists()) {
                return false;
            } else {
                return file.exists();
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean folderExists(String folderName) {
        File folder = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), folderName);
        return folder.exists() && folder.isDirectory();
    }

    public static void createFolder(String folderName) {
        File folder = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }


    public static String createCommandLog(String command)  {

        String fileName = "command_log-" + RandomUtils.generateID();
        File file = new File(Sentinel.getInstance().getDirector().io.getDataFolder() + "/LoggedCommands/" + fileName + ".txt");
        FileValidationUtils.validate(file);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.append(command);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }
}
