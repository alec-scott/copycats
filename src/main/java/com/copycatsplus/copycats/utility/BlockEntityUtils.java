package com.copycatsplus.copycats.utility;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.mixin.foundation.copycat.BlockEntityAccessor;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BlockEntityUtils {
    /**
     * Invalidate render data caches for the block entity and re-render it.
     */
    public static void redraw(BlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
        if (level != null) {
            if (level.isClientSide()) {
                requestModelDataUpdate(blockEntity);
            } else {
                blockEntity.setChanged();
            }
            BlockState state = blockEntity.getBlockState();
            level.sendBlockUpdated(blockEntity.getBlockPos(), state, state, 16);
            updateLight(blockEntity);
        }
    }

    public static void saveMetadata(BlockEntity blockEntity, ValueOutput view) {
        ((BlockEntityAccessor) blockEntity).callSaveMetadata(view);
    }

    public static void requestModelDataUpdate(BlockEntity blockEntity) {
    com.copycatsplus.copycats.utility.fabric.BlockEntityUtilsImpl.requestModelDataUpdate(blockEntity);}

    public static void updateLight(BlockEntity blockEntity) {
    com.copycatsplus.copycats.utility.fabric.BlockEntityUtilsImpl.updateLight(blockEntity);}

    public static int getLightEmission(BlockEntity blockEntity) {
        if (blockEntity instanceof IMultiStateCopycatBlockEntity multiStateBE) {
            int light = 0;
            for (BlockState material : multiStateBE.getMaterialItemStorage().getAllMaterials()) {
                light = Math.max(light, material.getLightEmission());
            }
            return light;
        } else if (blockEntity instanceof ICopycatBlockEntity copycatBE) {
            return copycatBE.getMaterial().getLightEmission();
        }
        return 0;
    }
}
