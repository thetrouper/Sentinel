package me.trouper.sentinel.server.commands.extras;

import me.trouper.sentinel.server.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractExtra implements Main {
    private final String name;
    private final String description;

    public AbstractExtra(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract void execute(CommandSender sender, Player target);
    public abstract void stop(CommandSender sender, Player target);
}
