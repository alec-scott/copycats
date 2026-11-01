package com.copycatsplus.copycats.config.fabric;

import com.copycatsplus.copycats.config.CCConfigs;
import com.copycatsplus.copycats.config.SyncConfigBase;
import com.copycatsplus.copycats.port.catnip.config.ConfigBase;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public abstract class SyncConfigBaseImpl extends SyncConfigBase {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            for (ConfigBase config : CCConfigs.CONFIGS.values())
                if (config instanceof SyncConfigBase syncConfig)
                    syncConfig.syncToPlayer(handler.getPlayer());
        });
    }
}
