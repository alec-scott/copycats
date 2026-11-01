package com.copycatsplus.copycats.foundation.copycat.model.kinetic;

import com.copycatsplus.copycats.CopycatsClient;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.flywheel.api.model.Model;
import com.zurrtum.create.client.catnip.render.*;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;

/**
 * Helper class to render kinetic copycat models.
 */
public class KineticCopycatRenderer {
    public static final SuperByteBufferCache.Compartment<KineticCopycatRenderData> KINETIC_COPYCAT = new SuperByteBufferCache.Compartment<>();
    private static final RendererReloadCache<KineticCopycatRenderData, Model> MODEL_CACHE = new RendererReloadCache<>();

    public static SuperByteBuffer getRenderedBuffer(ICopycatPartialModel partialModel, ICopycatBlockEntity be) {
        return CopycatsClient.BUFFER_CACHE.get(KINETIC_COPYCAT,
                KineticCopycatRenderData.of(partialModel, be),
                () -> renderBuffer(partialModel.getModel(), be, new PoseStack())
        );
    }

    public static SuperByteBuffer getRenderedBuffer(ICopycatPartialModel partialModel, IMultiStateCopycatBlockEntity be, String property) {
        return CopycatsClient.BUFFER_CACHE.get(KINETIC_COPYCAT,
                KineticCopycatRenderData.of(partialModel, be, property),
                () -> renderBuffer(partialModel.getModel(), be, new PoseStack())
        );
    }

    public static Model getInstancedModel(ICopycatPartialModel partialModel, ICopycatBlockEntity be) {
        return MODEL_CACHE.get(
                KineticCopycatRenderData.of(partialModel, be),
                data -> instancedModel(partialModel.getModel(), be)
        );
    }

    public static Model getInstancedModel(ICopycatPartialModel partialModel, IMultiStateCopycatBlockEntity be, String property) {
        return MODEL_CACHE.get(
                KineticCopycatRenderData.of(partialModel, be, property),
                data -> instancedModel(partialModel.getModel(), be)
        );
    }

    public static SuperByteBuffer renderBuffer(BlockStateModel model, ICopycatBlockEntity be, PoseStack ms) {
        return com.copycatsplus.copycats.foundation.copycat.model.kinetic.fabric.KineticCopycatRendererImpl.renderBuffer(model, be, ms);
    }

    public static Model instancedModel(BlockStateModel model, ICopycatBlockEntity be) {
        return com.copycatsplus.copycats.foundation.copycat.model.kinetic.fabric.KineticCopycatRendererImpl.instancedModel(model, be);
    }
}
