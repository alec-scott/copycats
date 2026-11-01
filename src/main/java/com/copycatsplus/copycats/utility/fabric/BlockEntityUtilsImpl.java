package com.copycatsplus.copycats.utility.fabric;

import com.zurrtum.create.catnip.levelWrappers.SchematicLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityUtilsImpl {
    public static void requestModelDataUpdate(BlockEntity blockEntity) {
        // no-op: Fabric models read the block entity at meshing time, and redraw() sends a block update
    }

    /**
     * PORT-NOTE(26.1): re-check the block's light; the light engine mixins read the material's emission
     * (see CopycatLight).
     */
    public static void updateLight(BlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
        if (level == null || level instanceof SchematicLevel)
            return;
        level.getLightEngine().checkBlock(blockEntity.getBlockPos());
    }
}
