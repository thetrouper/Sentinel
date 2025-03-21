package me.trouper.sentinel.server.functions.hotbar;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.NBTConfig;

public abstract class AbstractCheck<T> {
    public NBTConfig config = Sentinel.getInstance().getDirector().io.nbtConfig;
    public abstract boolean passes(T input);
}
