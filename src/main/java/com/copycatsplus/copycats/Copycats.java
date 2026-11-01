package com.copycatsplus.copycats;

import com.copycatsplus.copycats.config.CCConfigs;
import com.copycatsplus.copycats.network.CCPackets;
import com.copycatsplus.copycats.port.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Copycats {
    public static final String MODID = "copycats";
    /**
     * For user-facing non-translatable display names
     */
    public static final String NAME = "Copycats";
    //Only used for the data fixers!!!
    public static final int DATA_FIXER_VERSION = 1;
    public static final Logger LOGGER = LoggerFactory.getLogger("Copycats+");

    // PORT-NOTE(26.1): the tooltip modifier factory moved to CopycatsClient (Create Fly's tooltip classes are client
    // only); datagen (gatherData) is stubbed, the committed generated resources are used.
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public static void init() {
        CCBlocks.register();
        CCBlockEntityTypes.register();
        CCCatVariants.register();
        CCItems.register();

        CCConfigs.register();

        CCPackets.register();
    }

    public static CreateRegistrate getRegistrate() {
        return REGISTRATE;
    }

    public static Identifier asResource(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
