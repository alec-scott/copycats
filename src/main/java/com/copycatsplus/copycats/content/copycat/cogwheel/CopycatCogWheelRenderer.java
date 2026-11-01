package com.copycatsplus.copycats.content.copycat.cogwheel;

import com.copycatsplus.copycats.CCCopycatPartialModels;
import com.copycatsplus.copycats.foundation.copycat.model.kinetic.IKineticCopycatBlockRenderer;
import com.copycatsplus.copycats.foundation.copycat.model.kinetic.KineticCopycatRenderer;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.zurrtum.create.client.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.core.Direction;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.*;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual.rotationOffset;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual.shouldOffset;

// PORT-NOTE(26.1): extract/submit, following Create Fly's BracketedKineticBlockEntityRenderer (submit is inherited).
// The copycat buffers are assembled in the block's own orientation, so they are only rotated about the axis; each
// quad keeps its material's chunk layer, so the per-material render type lookup is gone.
public class CopycatCogWheelRenderer extends BracketedKineticBlockEntityRenderer implements IKineticCopycatBlockRenderer {
    public CopycatCogWheelRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(BracketedKineticBlockEntity be, BracketedKineticRenderState state, float tickProgress,
                                   Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(level);
        IMultiStateCopycatBlockEntity copycatBE = (IMultiStateCopycatBlockEntity) be;
        Direction.Axis axis = getRotationAxisOf(state.blockState);
        Direction direction = axis.getPositive();
        int color = getTintColor(be);
        float progress = getProgress(be, level);

        state.shaft = getRotatedModel(CCCopycatPartialModels.SHAFT, copycatBE, "shaft")
                .cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();

        float offset;
        if (ICogWheel.isLargeCog(state.blockState)) {
            // Large cogs sometimes have to offset their teeth by 11.25 degrees in order to mesh properly
            float shaftOffset;
            if (shouldOffset(axis, state.blockPos)) {
                offset = shaftOffset = 22.5f;
            } else {
                offset = 11.25f;
                shaftOffset = 0;
            }
            state.shaftAngle = getRotateAngle(progress, shaftOffset, direction);
            state.model = KineticCopycatRenderer.getRenderedBuffer(CCCopycatPartialModels.LARGE_COGWHEEL, copycatBE, "cogwheel")
                    .cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();
        } else {
            offset = rotationOffset(state.blockState, axis, state.blockPos);
            state.shaftAngle = getRotateAngle(progress, offset, direction);
            state.model = KineticCopycatRenderer.getRenderedBuffer(CCCopycatPartialModels.COGWHEEL, copycatBE, "cogwheel")
                    .cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();
        }
        state.angle = getRotateAngle(progress, offset, direction);
    }
}
