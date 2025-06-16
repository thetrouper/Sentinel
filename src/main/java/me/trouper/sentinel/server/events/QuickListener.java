package me.trouper.sentinel.server.events;

import me.trouper.sentinel.server.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public interface QuickListener extends Listener, Main {
    default QuickListener register() {
        Bukkit.getPluginManager().registerEvents(this, this.getPlugin());
        return this;
    }
}
