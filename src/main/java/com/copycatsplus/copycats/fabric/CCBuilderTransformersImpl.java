package com.copycatsplus.copycats.fabric;

import com.copycatsplus.copycats.foundation.copycat.client.CopycatTints;
import com.copycatsplus.copycats.port.create.foundation.data.SharedProperties;
import com.copycatsplus.copycats.port.create.foundation.data.TagGen;
import com.copycatsplus.copycats.port.registrate.builders.BlockBuilder;
import com.copycatsplus.copycats.port.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

@SuppressWarnings("removal")
public class CCBuilderTransformersImpl {

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> copycat() {
        return b -> b.initialProperties(SharedProperties::softMetal)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.noOcclusion().mapColor(MapColor.METAL).forceSolidOn())
                .color(() -> CopycatTints::simple)
                .transform(TagGen.axeOrPickaxe());
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> multiCopycat() {
        return b -> b.initialProperties(SharedProperties::softMetal)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.noOcclusion().mapColor(MapColor.METAL).forceSolidOn())
                .color(() -> CopycatTints::multiState)
                .transform(TagGen.axeOrPickaxe());
    }
}
