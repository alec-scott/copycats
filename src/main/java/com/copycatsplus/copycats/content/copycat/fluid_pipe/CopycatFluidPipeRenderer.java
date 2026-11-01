package com.copycatsplus.copycats.content.copycat.fluid_pipe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.catnip.animation.LerpedFloat;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.content.fluids.pipes.TransparentStraightPipeRenderer.TransparentStraightPipeRenderState;
import com.zurrtum.create.client.foundation.fluid.FluidRenderer;
import com.zurrtum.create.client.foundation.fluid.FluidRenderer.FluidStreamRenderState;
import com.zurrtum.create.content.fluids.FluidTransportBehaviour;
import com.zurrtum.create.content.fluids.PipeConnection.Flow;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders the fluid flowing through a copycat fluid pipe when its material is see-through.
 * <p>
 * PORT-NOTE(26.1): extract/submit, following Create Fly's TransparentStraightPipeRenderer (the stream radius is the
 * same 3/16). NeoForge skipped materials with a solid render layer; here, materials that occlude.
 */
public class CopycatFluidPipeRenderer implements BlockEntityRenderer<CopycatFluidPipeBlockEntity, TransparentStraightPipeRenderState> {
    private static final float RADIUS = 3 / 16f;
    protected final FluidStateModelSet fluidStateModelSet;

    public CopycatFluidPipeRenderer(BlockEntityRendererProvider.Context context) {
        fluidStateModelSet = context.blockModelResolver().modelManager.getFluidStateModelSet();
    }

    @Override
    public TransparentStraightPipeRenderState createRenderState() {
        return new TransparentStraightPipeRenderState();
    }

    @Override
    public void extractRenderState(CopycatFluidPipeBlockEntity be, TransparentStraightPipeRenderState state, float tickProgress,
                                   Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        state.fluids = null;
        if (be.getMaterial().canOcclude())
            return;
        FluidTransportBehaviour pipe = be.getBehaviour(FluidTransportBehaviour.TYPE);
        if (pipe == null)
            return;
        List<FluidStreamRenderState> fluids = new ArrayList<>(6);
        BlockAndTintGetter level = (BlockAndTintGetter) be.getLevel();
        BlockPos blockPos = be.getBlockPos();
        int lightCoords = level != null ? LevelRenderer.getLightCoords(level, blockPos) : LightCoordsUtil.FULL_BRIGHT;
        for (Direction side : Iterate.directions) {
            Flow flow = pipe.getFlow(side);
            if (flow == null)
                continue;
            FluidStack fluidStack = flow.fluid;
            if (fluidStack.isEmpty())
                continue;
            LerpedFloat progress = flow.progress;
            if (progress == null)
                continue;
            float value = progress.getValue(tickProgress);
            boolean inbound = flow.inbound;
            if (value == 1) {
                if (inbound) {
                    if (pipe.getFlow(side.getOpposite()) == null)
                        value -= 1e-6f;
                } else {
                    FluidTransportBehaviour adjacent = BlockEntityBehaviour.get(level, blockPos.relative(side), FluidTransportBehaviour.TYPE);
                    if (adjacent == null) {
                        value -= 1e-6f;
                    } else {
                        Flow other = adjacent.getFlow(side.getOpposite());
                        if (other == null || !other.inbound && !other.complete)
                            value -= 1e-6f;
                    }
                }
            }
            fluids.add(FluidRenderer.extractFluidStreamRenderState(level, blockPos, fluidStateModelSet, fluidStack.getFluid(),
                    fluidStack.getComponentChanges(), side, value, inbound, RADIUS, lightCoords));
        }
        if (fluids.isEmpty())
            return;
        state.blockPos = blockPos;
        state.blockState = be.getBlockState();
        state.blockEntityType = be.getType();
        state.lightCoords = lightCoords;
        state.breakProgress = crumblingOverlay;
        state.fluids = fluids;
    }

    @Override
    public void submit(TransparentStraightPipeRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        if (state.fluids != null)
            for (FluidStreamRenderState fluid : state.fluids)
                fluid.submit(queue, matrices);
    }
}
