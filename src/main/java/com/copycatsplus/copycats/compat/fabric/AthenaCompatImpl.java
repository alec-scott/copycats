package com.copycatsplus.copycats.compat.fabric;

import net.minecraft.client.renderer.block.BlockAndTintGetter;

// PORT-NOTE: Athena has no 26.1 Fabric build, so there is no wrapped getter to unwrap.
public class AthenaCompatImpl {
    public static BlockAndTintGetter unwrapAthenaGetter(BlockAndTintGetter getter) {
        return getter;
    }
}
