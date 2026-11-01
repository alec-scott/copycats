package com.copycatsplus.copycats.config.fabric;

import com.copycatsplus.copycats.Copycats;
import com.copycatsplus.copycats.config.CClient;
import com.copycatsplus.copycats.config.CCConfigs;
import com.copycatsplus.copycats.config.CCommon;
import com.copycatsplus.copycats.config.CServer;
import com.copycatsplus.copycats.port.catnip.config.ConfigBase;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.neoforged.fml.config.ModConfig;

import java.util.Map;

public class CCConfigsImpl extends CCConfigs {

    public static void onLoad(ModConfig modConfig) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == modConfig.getSpec())
                config.onLoad();
    }

    public static void onReload(ModConfig modConfig) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == modConfig.getSpec())
                config.onReload();
    }

    public static void register() {
        client = register(CClient::new, ModConfig.Type.CLIENT);
        common = register(CCommon::new, ModConfig.Type.COMMON);
        server = register(CServer::new, ModConfig.Type.SERVER);

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
            ConfigRegistry.INSTANCE.register(Copycats.MODID, pair.getKey(), pair.getValue().specification);
        ModConfigEvents.loading(Copycats.MODID).register(CCConfigsImpl::onLoad);
        ModConfigEvents.reloading(Copycats.MODID).register(CCConfigsImpl::onReload);
    }
}
