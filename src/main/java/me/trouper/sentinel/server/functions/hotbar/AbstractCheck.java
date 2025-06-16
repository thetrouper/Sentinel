package me.trouper.sentinel.server.functions.hotbar;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.NBTConfig;
import me.trouper.sentinel.server.Main;

public abstract class AbstractCheck<T> implements Main {
    public NBTConfig config = main.dir().io.nbtConfig;
    public abstract boolean passes(T input);
}
