package me.trouper.sentinel.server.functions.itemchecks;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.ServerUtils;

import java.util.Arrays;

public abstract class AbstractCheck<T> {
    public abstract boolean passes(T input);
}
