package io.github.thetrouper.sentinel.server.util;

import io.github.thetrouper.sentinel.Sentinel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class FileUtils {
    public static boolean folderExists(String folderName) {
        File folder = new File(Sentinel.getInstance().getDataFolder(), folderName);
        return folder.exists() && folder.isDirectory();
    }
    public static void createFolder(String folderName) {
        File folder = new File(Sentinel.getInstance().getDataFolder(), folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
    public static String createNBTLog(String contents)  {
        ServerUtils.sendDebugMessage("FileUtils: Creating NBT log");
        String fileName = "nbt_log-" + Randomizer.generateID();

        File dataFolder = Sentinel.getInstance().getDataFolder();

        File loggedNBTFolder = new File(dataFolder,"LoggedNBT");
        if (!loggedNBTFolder.exists()) {
            loggedNBTFolder.mkdirs();
        }

        File file = new File(loggedNBTFolder, fileName + ".txt");
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

    public static String createCommandLog(String command)  {
        String fileName = "command_log-" + Randomizer.generateID();
        File file = new File(Sentinel.getInstance().getDataFolder() + "/LoggedCommands/" + fileName + ".txt");
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
