package me.trouper.sentinel.data.types;

import org.bukkit.inventory.ItemStack;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class NBTHolder {

    private final String base64Item;
    private final UUID owner;
    private final long timestamp;
    private final int byteSize;

    public NBTHolder(ItemStack item, UUID owner) {
        this.base64Item = encodeItem(item);
        this.owner = owner;
        this.timestamp = System.currentTimeMillis();
        this.byteSize = base64Item != null ? base64Item.getBytes(StandardCharsets.UTF_8).length : 0;
    }

    public UUID getOwner() {
        return owner;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getByteSize() {
        return byteSize;
    }

    public ItemStack getItem() {
        return decodeItem(base64Item);
    }

    public String getBase64Item() {
        return base64Item;
    }

    /**
     * Encodes an ItemStack into a Base64 string using the modern, robust byte array serialization.
     * @param item The item to encode.
     * @return A Base64 string representing the item.
     */
    public static String encodeItem(ItemStack item) {
        if (item == null) return null;
        try {
            byte[] itemBytes = item.serializeAsBytes();
            return Base64.getEncoder().encodeToString(itemBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decodes a Base64 string back into an ItemStack using byte array deserialization.
     * @param base64 The Base64 string to decode.
     * @return The deserialized ItemStack.
     */
    public static ItemStack decodeItem(String base64) {
        if (base64 == null || base64.isEmpty()) return null;
        try {
            byte[] itemBytes = Base64.getDecoder().decode(base64);
            return ItemStack.deserializeBytes(itemBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}