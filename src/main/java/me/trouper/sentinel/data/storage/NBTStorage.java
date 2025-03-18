package me.trouper.sentinel.data.storage;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.*;

public class NBTStorage implements JsonSerializable<NBTStorage> {
    @Override
    public File getFile() {
        File file = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "/storage/nbt.json");
        file.getParentFile().mkdirs();
        return file;
    }
    
    public Map<String, String> caughtItems = new HashMap<>();

    public static ItemStack toItem(String serializedString) {
        if (serializedString.equals("null")) return null;
        byte[] decodedBytes = Base64.getDecoder().decode(serializedString);
        String mapString = new String(decodedBytes);
        // Remove the curly braces and split by commas to get key-value pairs
        String[] keyValuePairs = mapString.substring(1, mapString.length() - 1).split(", ");
        Map<String, Object> deserializedMap = new HashMap<>();
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=");
            deserializedMap.put(keyValue[0], keyValue[1]);
        }
        ItemStack item = ItemStack.deserialize(deserializedMap);
        return item;
    }
    
    public static String toB64(ItemStack itemStack) {
        Map<String, Object> serializedMap = itemStack.serialize();
        return Base64.getEncoder().encodeToString(serializedMap.toString().getBytes());
    }
}
