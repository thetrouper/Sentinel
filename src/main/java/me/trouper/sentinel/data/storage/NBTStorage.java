package me.trouper.sentinel.data.storage;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.NBTHolder;
import me.trouper.sentinel.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NBTStorage implements JsonSerializable<NBTStorage> {

    /**
     * A lightweight, serializable class to hold item metadata in memory.
     * This is what gets stored in the main nbt.json file.
     */
    public static class Metadata {
        public final UUID owner;
        public final long timestamp;
        public final int byteSize;

        // Private no-arg constructor for JSON deserialization
        private Metadata() {
            this(null, 0, 0);
        }

        public Metadata(UUID owner, long timestamp, int byteSize) {
            this.owner = owner;
            this.timestamp = timestamp;
            this.byteSize = byteSize;
        }
    }

    // The map stores a filename key and the lightweight Metadata object.
    // The large item data is NOT stored in memory.
    public Map<String, Metadata> caughtItems = new HashMap<>();

    public void storeItem(ItemStack item, UUID owner) {
        // Use NBTHolder as a temporary tool to get the serialized data and metadata.
        NBTHolder holder = new NBTHolder(item, owner);
        String nbt = holder.getBase64Item();

        if (nbt == null) {
            System.err.println("Failed to serialize item for NBT storage.");
            return;
        }

        File storageDir = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "storage/nbt");
        String fileName = UUID.randomUUID().toString() + ".nbt";
        File file = new File(storageDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            writer.write(nbt);
        } catch (IOException e) {
            e.printStackTrace();
            return; // Do not save metadata if file write fails.
        }

        // Create the lightweight metadata object and store it in the map.
        Metadata metadata = new Metadata(holder.getOwner(), holder.getTimestamp(), holder.getByteSize());
        caughtItems.put(fileName, metadata);
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
            String b64 = new String(fis.readAllBytes(), StandardCharsets.UTF_8);
            // Use the static decoder from NBTHolder to avoid duplicate code.
            return NBTHolder.decodeItem(b64);
        } catch (FileNotFoundException e) {
            // Cleanup the metadata entry if the corresponding file is missing.
            Sentinel.getInstance().getDirector().io.nbtStorage.caughtItems.remove(fileName);
            Sentinel.getInstance().getDirector().io.nbtStorage.save();
            return new ItemBuilder().material(Material.STRUCTURE_VOID)
                    .displayName(Component.text("File not found.", NamedTextColor.RED).decoration(TextDecoration.ITALIC,false))
                    .loreComponent(Component.text("This item no longer exists and has been removed from the list.",NamedTextColor.GRAY))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return new ItemBuilder().material(Material.STRUCTURE_VOID)
                    .displayName(Component.text("Unknown IO Exception.", NamedTextColor.RED).decoration(TextDecoration.ITALIC,false))
                    .loreComponent(Component.text("Check console for details.",NamedTextColor.DARK_RED))
                    .build();
        }
    }

    @Override
    public File getFile() {
        File file = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "storage/nbt.json");
        // Ensure the directory for individual .nbt files exists.
        new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "storage/nbt").mkdirs();
        file.getParentFile().mkdirs();
        return file;
    }
}