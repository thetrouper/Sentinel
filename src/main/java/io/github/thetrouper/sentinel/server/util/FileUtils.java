package io.github.thetrouper.sentinel.server.util;

import io.github.thetrouper.sentinel.Sentinel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.util.Random;

public class FileUtils {
    public static boolean folderExists(String folderName) {
        File folder = new File(Sentinel.getDF(), folderName);
        return folder.exists() && folder.isDirectory();
    }
    public static void createFolder(String folderName) {
        File folder = new File(Sentinel.getDF(), folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
    public static String createNBTLog(String contents)  {
        String fileName = "nbt_log-" + Randomizer.generateID();
        File file = new File(Sentinel.getDF() + "/LoggedNBT/" + fileName + ".txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.append(contents);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
