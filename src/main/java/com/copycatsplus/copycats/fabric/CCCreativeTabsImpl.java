package com.copycatsplus.copycats.fabric;

import com.copycatsplus.copycats.CCBlocks;
import com.copycatsplus.copycats.CCCreativeTabs;
import com.copycatsplus.copycats.Copycats;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

// PORT-NOTE(26.1): Fabric tab registration. Tab order (was withTabsBefore Create's palettes tab) is Fabric's default.
// The display generators skip disabled features, which also keeps them out of the search tab, so NeoForge's
// search-tab cleanup isn't needed.
public class CCCreativeTabsImpl extends CCCreativeTabs {

    public static final ResourceKey<CreativeModeTab> MAIN_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Copycats.asResource("main"));
    public static final ResourceKey<CreativeModeTab> FUNCTIONAL_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Copycats.asResource("functional"));

    private static CreativeModeTab mainTab;
    private static CreativeModeTab functionalTab;

    public static void setCreativeTab() {
    }

    public static void register() {
        mainTab = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MAIN_TAB_KEY, FabricCreativeModeTab.builder()
                .title(Component.translatable("itemGroup.copycats.main"))
                .icon(CCBlocks.COPYCAT_SLAB::asStack)
                .displayItems(new AdvancedDisplayGenerator(DECORATIVE, () -> mainTab))
                .build());
        functionalTab = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, FUNCTIONAL_TAB_KEY, FabricCreativeModeTab.builder()
                .title(Component.translatable("itemGroup.copycats.functional"))
                .icon(CCBlocks.COPYCAT_COGWHEEL::asStack)
                .displayItems(new AdvancedDisplayGenerator(FUNCTIONAL, () -> functionalTab))
                .build());
    }

    public static CreativeModeTab getBaseTab() {
        return mainTab;
    }

    public static ResourceKey<CreativeModeTab> getBaseTabKey() {
        return MAIN_TAB_KEY;
    }

    public static CreativeModeTab getFunctionalTab() {
        return functionalTab;
    }

    public static ResourceKey<CreativeModeTab> getFunctionalTabKey() {
        return FUNCTIONAL_TAB_KEY;
    }
}
