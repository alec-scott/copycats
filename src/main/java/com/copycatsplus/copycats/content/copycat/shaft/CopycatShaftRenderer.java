package com.copycatsplus.copycats.content.copycat.shaft;

import com.copycatsplus.copycats.CCCopycatPartialModels;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.model.kinetic.IKineticCopycatBlockRenderer;
import com.zurrtum.create.client.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
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

// PORT-NOTE(26.1): extract/submit. The copycat shaft buffer is assembled in the block's own orientation, so it is
// only rotated about its axis (submit is inherited).
public class CopycatShaftRenderer extends BracketedKineticBlockEntityRenderer implements IKineticCopycatBlockRenderer {
    public CopycatShaftRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(BracketedKineticBlockEntity be, BracketedKineticRenderState state, float tickProgress,
                                   Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(level);
        Direction.Axis axis = getRotationAxisOf(state.blockState);
        state.shaft = null;
        state.shaftAngle = null;
        state.angle = getRotateAngle(getProgress(be, level), rotationOffset(state.blockState, axis, state.blockPos), axis.getPositive());
        state.model = getRotatedModel(CCCopycatPartialModels.SHAFT, (ICopycatBlockEntity) be)
                .cardinalLighting(cardinalLighting).light(state.lightCoords).color(getTintColor(be)).extractRenderState();
    }
}
