package com.copycatsplus.copycats.fabric.mixin.foundation.copycat.multistate;

import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.MultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.content.copycat.cogwheel.CopycatCogWheelBlockEntity;
import com.copycatsplus.copycats.utility.fabric.ModelDataUtils;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collections;

import static com.copycatsplus.copycats.foundation.copycat.model.fabric.CopycatModelNeoForge.MATERIALS_PROPERTY;

/**
 * Implement platform-specific methods for multi-state copycat block entities.
 * <p>
 * All multi-state copycats should register their block entities here instead of writing their own platform-specific implementations.
 */
@Mixin({
        MultiStateCopycatBlockEntity.class,
        CopycatCogWheelBlockEntity.class
})
public abstract class MultiStateCopycatBlockEntityMixin extends SmartBlockEntity implements IMultiStateCopycatBlockEntity {

    public MultiStateCopycatBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    @Unique
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelDataUtils.mergeData(
                super.getModelData(),
                ModelData.builder()
                        .with(MATERIALS_PROPERTY, Collections.synchronizedMap(getMaterialItemStorage().getMaterialMap()))
                        .build()
        ).build();
    }
}
