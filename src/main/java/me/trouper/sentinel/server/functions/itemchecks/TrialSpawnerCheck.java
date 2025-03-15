package me.trouper.sentinel.server.functions.itemchecks;

import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.TrialSpawner;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.entity.Entity;
import org.bukkit.spawner.TrialSpawnerConfiguration;

public class TrialSpawnerCheck extends AbstractCheck<TrialSpawner> {

    @Override
    public boolean passes(TrialSpawner spawner) {
        ServerUtils.verbose("Running trial spawner check.");
        if (spawner.getNormalConfiguration() != null) {
            TrialSpawnerConfiguration config = spawner.getNormalConfiguration();
            if (config.getSpawnedEntity() != null && !new EntitySnapshotCheck().passes(config.getSpawnedEntity())) {
                ServerUtils.verbose("Trial Spawner failed check: Normal entity snapshot not allowed.");
                return false;
            }
        }
        if (spawner.getOminousConfiguration() != null) {
            TrialSpawnerConfiguration config = spawner.getOminousConfiguration();
            if (config.getSpawnedEntity() != null && !new EntitySnapshotCheck().passes(config.getSpawnedEntity())) {
                ServerUtils.verbose("Trial Spawner failed check: Ominous entity snapshot not allowed.");
                return false;
            }
        }
        return true;
    }
}

