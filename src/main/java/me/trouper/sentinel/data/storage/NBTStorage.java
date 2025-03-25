package me.trouper.sentinel.data.storage;

import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NBTStorage implements JsonSerializable<NBTStorage> {

    public Map<String, String> caughtItems = new HashMap<>();
    
    public void storeItem(ItemStack item, UUID owner) {
        File storageDir = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "storage/nbt");
        String fileName = UUID.randomUUID().toString() + ".nbt";
        File file = new File(storageDir, fileName);
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

            String nbt = serializeItem(item);
            writer.write(nbt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        caughtItems.put(fileName, owner.toString());
        save();
    }
    
    public boolean deleteItem(String fileName) {
        File storageDir = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "storage/nbt");
        File file = new File(storageDir, fileName);
        caughtItems.remove(fileName);
        save();
        return file.delete();
    }

    public static ItemStack getItem(String fileName) {
        File storageDir = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "storage/nbt");
        File file = new File(storageDir, fileName);
        try (FileInputStream fis = new FileInputStream(file)) {
            StringBuilder b64 = new StringBuilder();
            int content;
            while ((content = fis.read()) != -1) {
                b64.append((char) content);
            }

            return deserializeItem(b64.toString());
        } catch (FileNotFoundException e) {
            Sentinel.getInstance().getDirector().io.nbtStorage.caughtItems.remove(fileName);
            Sentinel.getInstance().getDirector().io.nbtStorage.save();
            return new ItemBuilder().material(Material.STRUCTURE_VOID)
                    .name(Text.color("&cFile not found."))
                    .lore(Text.color("&7This item no longer exists and has been removed from the list."))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return new ItemBuilder().material(Material.STRUCTURE_VOID)
                    .name(Text.color("&cUnknown IO exception."))
                    .lore(Text.color("&4Check Console."))
                    .build();
        }
    }
    
    public static String serializeItem(ItemStack item) {
        if (item == null) {
            return null;
        }
        try {
            Map<String, Object> serializedItem = item.serialize();

            YamlConfiguration config = new YamlConfiguration();
            config.set("item", serializedItem);
            String yamlString = config.saveToString();

            return Base64.getEncoder().encodeToString(yamlString.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack deserializeItem(String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        try {
            byte[] decodedData = Base64.getDecoder().decode(data);
            String yamlString = new String(decodedData, StandardCharsets.UTF_8);
            
            YamlConfiguration config = new YamlConfiguration();
            config.loadFromString(yamlString);

            ConfigurationSection itemSection = config.getConfigurationSection("item");
            if (itemSection == null) {
                return null;
            }

            Map<String, Object> serializedItem = itemSection.getValues(true);

            return ItemStack.deserialize(serializedItem);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public File getFile() {
        File file = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "storage/nbt.json");
        new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "storage/nbt").mkdirs();
        file.getParentFile().mkdirs();
        return file;
    }
}