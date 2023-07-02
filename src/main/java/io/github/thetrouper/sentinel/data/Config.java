/**
 * This file is for tutorial purposes made by ImproperIssues. Distribute if you want :)
 */

package io.github.thetrouper.sentinel.data;

import io.github.thetrouper.sentinel.Sentinel;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Config loader
 */
public abstract class Config {

    private static final FileConfiguration config = Sentinel.getInstance().getConfig();

    /**
     * Config plugin section
     */
    public class Plugin {
        public static String getPrefix() {
            return config.getString("config.plugin.prefix");
        }
    }
}
