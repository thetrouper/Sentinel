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

    public static ItemStack toItem(String data) {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            ItemStack item = (ItemStack) objectInputStream.readObject();
            objectInputStream.close();
            return item;
        } catch (IOException | ClassNotFoundException e) {
            Sentinel.getInstance().getLogger().warning("Could not deserialize ItemStack: " + e.getMessage());
            return null;
        }
    }

    public static String toB64(ItemStack item) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(item);
            objectOutputStream.close();
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            Sentinel.getInstance().getLogger().warning("Could not serialize ItemStack: " + e.getMessage());
            return null;
        }
    }
}
