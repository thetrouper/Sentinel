package me.trouper.sentinel.data.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NBTStorage implements JsonSerializable<NBTStorage> {

    // Mapping from file name to owner UUID (as a String)
    public Map<String, String> caughtItems = new HashMap<>();

    private final File mappingFile;
    private final File storageDir;

    public NBTStorage() {
        // Create the storage directory: /storage/nbt/ inside the plugin data folder
        File dataFolder = Sentinel.getInstance().getDirector().io.getDataFolder();
        storageDir = new File(dataFolder, "storage/nbt");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        // The mapping file that stores the file-name to owner UUID mapping
        mappingFile = new File(dataFolder, "storage/nbt.json");
        mappingFile.getParentFile().mkdirs();
    }

    /**
     * Stores an ItemStack's serialized NBT to a unique file
     * and maps the generated file name to the owner UUID.
     *
     * @param item  the ItemStack to store
     * @param owner the owner's UUID
     */
    public void storeItem(ItemStack item, UUID owner) {
        // Generate a unique file name with a .nbt extension
        String fileName = UUID.randomUUID().toString() + ".nbt";
        File file = new File(storageDir, fileName);
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {
            
            String nbt = serializeItem(item);
            writer.write(nbt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Add mapping: file name -> owner UUID (as string)
        caughtItems.put(fileName, owner.toString());
        save();
    }

    /**
     * Placeholder for item serialization.
     * Replace this with an actual NBT serialization logic.
     *
     * @param item the ItemStack to serialize
     * @return a String representing the NBT data of the item
     */
    private String serializeItem(ItemStack item) {
        
        return item.toString();
    }
    
    // Make a deserialize method too.
    

    @Override
    public File getFile() {
        return mappingFile;
    }
    
}
