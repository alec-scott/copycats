package com.copycatsplus.copycats;

import com.copycatsplus.copycats.port.registrate.builders.BlockBuilder;
import net.minecraft.world.level.block.Block;
import com.copycatsplus.copycats.port.registrate.util.nullness.NonNullUnaryOperator;

public class CCBuilderTransformers {

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> copycat() {
        return com.copycatsplus.copycats.fabric.CCBuilderTransformersImpl.copycat();
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> multiCopycat() {
        return com.copycatsplus.copycats.fabric.CCBuilderTransformersImpl.multiCopycat();
    }
}
