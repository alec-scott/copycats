package com.copycatsplus.copycats.compat;

import net.minecraft.world.level.BlockAndTintGetter;

public class AthenaCompat {
    public static BlockAndTintGetter unwrapAthenaGetter(BlockAndTintGetter getter) {
        return com.copycatsplus.copycats.compat.fabric.AthenaCompatImpl.unwrapAthenaGetter(getter);
    }
}
