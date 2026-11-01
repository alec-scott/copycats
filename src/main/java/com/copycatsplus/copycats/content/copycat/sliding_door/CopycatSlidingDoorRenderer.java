package com.copycatsplus.copycats.content.copycat.sliding_door;

import com.copycatsplus.copycats.CCCopycatPartialModels;
import com.copycatsplus.copycats.foundation.copycat.model.kinetic.IKineticCopycatBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.decoration.slidingDoor.SlidingDoorBlock;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * PORT-NOTE(26.1): extract/submit. Create Fly always renders sliding doors from the block entity (there is no
 * VISIBLE block state), and each quad keeps its material's layer, so there is no translucent moving-block buffer.
 */
public class CopycatSlidingDoorRenderer implements BlockEntityRenderer<CopycatSlidingDoorBlockEntity, CopycatSlidingDoorRenderer.State>, IKineticCopycatBlockRenderer {

    public CopycatSlidingDoorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CopycatSlidingDoorBlockEntity be, State state, float partialTicks, Vec3 cameraPos,
                                   @Nullable CrumblingOverlay crumblingOverlay) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(level);
        state.parts.clear();
        BlockState blockState = be.getBlockState();

        Direction facing = blockState.getValue(DoorBlock.FACING);
        Direction movementDirection = facing.getClockWise();

        if (blockState.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT)
            movementDirection = movementDirection.getOpposite();

        float value = be.animation().getValue(partialTicks);
        float value2 = Mth.clamp(value * 10, 0, 1);

        Vec3 offset = Vec3.atLowerCornerOf(movementDirection.getUnitVec3i())
                .scale(value * value * 13 / 16f)
                .add(Vec3.atLowerCornerOf(facing.getUnitVec3i())
                        .scale(value2 * 1 / 32f));

        if (((SlidingDoorBlock) blockState.getBlock()).isFoldingDoor()) {
            boolean flip = blockState.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
            for (boolean left : Iterate.trueAndFalse) {
                SuperByteBuffer partial = left ^ flip ?
                        IKineticCopycatBlockRenderer.super.getRotatedModel(CCCopycatPartialModels.FOLDING_DOOR_LEFT, be) :
                        IKineticCopycatBlockRenderer.super.getRotatedModel(CCCopycatPartialModels.FOLDING_DOOR_RIGHT, be);
                float f = flip ? -1 : 1;

                partial.translate(0, -1 / 512f, 0)
                        .translate(Vec3.atLowerCornerOf(facing.getUnitVec3i())
                                .scale(value2 * 1 / 32f));
                partial.rotateCentered(
                        Mth.DEG_TO_RAD * AngleHelper.horizontalAngle(facing.getClockWise()), Direction.UP);

                if (flip)
                    partial.translate(0, 0, 1);
                partial.rotateYDegrees(91 * f * value * value);

                if (!left)
                    partial.translate(0, 0, f / 2f)
                            .rotateYDegrees(-181 * f * value * value);

                if (flip)
                    partial.translate(0, 0, -1 / 2f);

                state.parts.add(partial.cardinalLighting(cardinalLighting).light(state.lightCoords).extractRenderState());
            }
        } else {
            state.parts.add(IKineticCopycatBlockRenderer.super.getRotatedModel(CCCopycatPartialModels.SLIDING_DOOR, be)
                    .translate(offset)
                    .cardinalLighting(cardinalLighting)
                    .light(state.lightCoords)
                    .extractRenderState());
        }
    }

    @Override
    public void submit(State state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        for (SuperByteBufferRenderState part : state.parts)
            part.submit(matrices, queue);
    }

    public static class State extends BlockEntityRenderState {
        public final List<SuperByteBufferRenderState> parts = new ArrayList<>(2);
    }
}
