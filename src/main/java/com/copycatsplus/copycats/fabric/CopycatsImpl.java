package com.copycatsplus.copycats.fabric;

import com.copycatsplus.copycats.Copycats;
import com.copycatsplus.copycats.config.fabric.FeatureEnabledCondition;
import com.copycatsplus.copycats.config.fabric.SyncConfigBaseImpl;
import com.copycatsplus.copycats.utility.LogicalSidedProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class CopycatsImpl implements ModInitializer {

    @Override
    public void onInitialize() {
        Copycats.init();
        CCCreativeTabsImpl.register();
        Copycats.getRegistrate().register();
        com.copycatsplus.copycats.port.create.ItemUseOverrides.init();

        FeatureEnabledCondition.register();
        SyncConfigBaseImpl.register();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> LogicalSidedProvider.setServer(() -> server));
    }
}
