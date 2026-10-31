package com.copycatsplus.copycats.content.copycat.fluid_pipe;


import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class CopycatFluidPipeRenderer extends SafeBlockEntityRenderer<CopycatFluidPipeBlockEntity> {

    public CopycatFluidPipeRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(CopycatFluidPipeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        renderSafe(this, be, partialTicks, ms, buffer, light, overlay);
    }

    public static void renderSafe(CopycatFluidPipeRenderer renderer, CopycatFluidPipeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                                  int light, int overlay) {

    com.copycatsplus.copycats.content.copycat.fluid_pipe.fabric.CopycatFluidPipeRendererImpl.renderSafe(renderer, be, partialTicks, ms, buffer, light, overlay);}
}

