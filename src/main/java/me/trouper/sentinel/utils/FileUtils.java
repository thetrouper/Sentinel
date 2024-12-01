package me.trouper.sentinel.utils;

import io.github.itzispyder.pdk.utils.FileValidationUtils;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.Randomizer;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    public static boolean folderExists(String folderName) {
        File folder = new File(Sentinel.dataFolder(), folderName);
        return folder.exists() && folder.isDirectory();
    }

    public static void createFolder(String folderName) {
        File folder = new File(Sentinel.dataFolder(), folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static String createNBTLog(String contents)  {
        ServerUtils.verbose("FileUtils: Creating NBT log");
        String fileName = "nbt_log-" + Randomizer.generateID();

        File dataFolder = Sentinel.dataFolder();

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

    public static String createNBTLog(ItemStack i)  {
        ServerUtils.verbose("FileUtils: Creating NBT log");

        String item = i.getType().name().toLowerCase() + i.getItemMeta().getAsString();

        String fileName = "nbt_log-" + Randomizer.generateID();

        File dataFolder = Sentinel.dataFolder();

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
            writer.append(item);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }


    public static String createCommandLog(String command)  {

        String fileName = "command_log-" + Randomizer.generateID();
        File file = new File(Sentinel.dataFolder() + "/LoggedCommands/" + fileName + ".txt");
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
