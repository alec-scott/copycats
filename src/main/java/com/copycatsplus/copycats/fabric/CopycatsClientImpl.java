package com.copycatsplus.copycats.fabric;

import com.copycatsplus.copycats.Copycats;
import com.copycatsplus.copycats.CopycatsClient;
import com.copycatsplus.copycats.foundation.copycat.model.kinetic.RendererReloadCache;
import com.copycatsplus.copycats.foundation.tooltip.CopycatDescription;
import com.copycatsplus.copycats.network.CCPackets;
import com.copycatsplus.copycats.port.create.foundation.data.CreateRegistrate;
import com.copycatsplus.copycats.port.registrate.util.entry.RegistryEntry;
import com.zurrtum.create.client.catnip.lang.FontHelper;
import com.zurrtum.create.client.foundation.item.ItemDescription;
import com.zurrtum.create.client.foundation.item.KineticStats;
import com.zurrtum.create.client.foundation.item.TooltipModifier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class CopycatsClientImpl implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CopycatsClient.init();
        CreateRegistrate.registerBlockModels();
        CCPackets.registerClient();
        registerTooltips();
        InvalidateRenderStateCallback.EVENT.register(RendererReloadCache::onReloadLevelRenderer);
    }

    // PORT-NOTE(26.1): was REGISTRATE.setTooltipModifierFactory in common code; Create Fly's tooltip classes are client only
    private static void registerTooltips() {
        for (RegistryEntry<Item, Item> entry : Copycats.getRegistrate().<Item, Item>getAll(Registries.ITEM)) {
            Item item = entry.get();
            if (TooltipModifier.REGISTRY.get(item) != null) continue;
            TooltipModifier.REGISTRY.register(item, CopycatDescription.create(item)
                    .andThen(new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE))
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
        }
    }
}
