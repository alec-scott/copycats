package com.copycatsplus.copycats.utility.fabric;

import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityUtilsImpl {
    public static void requestModelDataUpdate(BlockEntity blockEntity) {
        // no-op: Fabric models read the block entity at meshing time, and redraw() sends a block update
    }

    public static void updateLight(BlockEntity blockEntity) {
        // PORT-NOTE: NeoForge's AuxiliaryLightManager gave copycats their material's light level. Fabric has no
        // per-position light emission, so (as with Create Fly's own copycats) glowing materials don't emit light.
    }
}
