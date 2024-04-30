package io.github.thetrouper.sentinel.data;

import org.bukkit.event.Cancellable;


import java.util.function.Consumer;

public record ActionResult(String name, Runnable action, boolean isRan) {

}
