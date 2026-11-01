package com.copycatsplus.copycats.foundation.copycat.model.kinetic.fabric;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.model.kinetic.WrappedRenderWorld;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.EntityBlockSbbBuilder;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.flywheel.api.model.Model;
import com.zurrtum.create.client.flywheel.lib.model.baked.BakedModelBuilder;
import com.zurrtum.create.client.flywheel.lib.model.baked.ModelRenderHelper;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * PORT-NOTE(26.1): the copycat model reads the block entity from the {@link WrappedRenderWorld}, so the buffers are
 * tesselated with it directly (NeoForge passed the materials through model data).
 */
public class KineticCopycatRendererImpl {

    public static SuperByteBuffer renderBuffer(BlockStateModel model, ICopycatBlockEntity be, PoseStack ms) {
        WrappedRenderWorld renderWorld = new WrappedRenderWorld(be);
        BlockPos pos = be.getBlockPos();
        BlockState state = be.getBlockState();
        EntityBlockSbbBuilder builder = new EntityBlockSbbBuilder();
        ModelRenderHelper.getHelper(builder).tesselateBlock(0, 0, 0, renderWorld, pos, state, model, state.getSeed(pos));
        return builder.build();
    }

    public static Model instancedModel(BlockStateModel model, ICopycatBlockEntity be) {
        return new BakedModelBuilder(model)
                .level(new WrappedRenderWorld(be))
                .pos(be.getBlockPos())
                .build();
    }
}
