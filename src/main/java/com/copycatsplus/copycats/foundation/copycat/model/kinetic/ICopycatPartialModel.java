package com.copycatsplus.copycats.foundation.copycat.model.kinetic;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * A partial model which is assembled from models of copied materials using a {@link CopycatModelCore}.
 * These models are intended to be reused across different copycats, thus their implementation must not be specific to a single block.
 * <p>
 * Multi-state copycats should use a separate partial model for each part for efficient caching. For example, a Copycat
 * Cogwheel should use separate partial models for the shaftless cogwheel and the shaft, so that the shaft model can be reused by
 * Copycat Shafts.
 * <p>
 * Use {@link dev.engine_room.flywheel.lib.model.baked.PartialModel} instead if dynamic assembly is not required.
 */
public interface ICopycatPartialModel {

    /**
     * Get the baked model for this partial model, usually a copycat model which contains a {@link CopycatModelCore}.
     */
    BlockStateModel getModel();

    /**
     * Get all the block state properties required to render this partial model. The model must not use any other properties,
     * since the render result is cached using these properties as a key.
     */
    Property<?>[] getProperties();

    static BlockStateModel modelOf(CopycatModelCore core) {
        // PORT-NOTE(26.1): built from the baked air model (no unbaked root to wrap at runtime)
        return new com.copycatsplus.copycats.foundation.copycat.model.fabric.CopycatModelFabric(
                CopycatModelCore.getModelOf(Blocks.AIR.defaultBlockState()), core, true);
    }
}
