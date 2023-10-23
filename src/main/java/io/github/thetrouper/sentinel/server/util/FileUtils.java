package io.github.thetrouper.sentinel.server.util;

import com.google.gson.reflect.TypeToken;
import io.github.thetrouper.sentinel.Sentinel;

import java.io.*;
import java.time.*;
import java.util.List;
import java.util.Random;
import com.google.gson.Gson;
import org.bukkit.Location;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Location;
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
        File file = new File(Sentinel.getInstance().getDataFolder() + "/LoggedNBT/" + fileName + ".txt");
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
