package com.copycatsplus.copycats.utility;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public class DirectionUtils {
    /**
     * The direction with exactly the given unit offset, or null (1.21.1's {@code Direction.fromDelta}, removed in 26.1).
     */
    @Nullable
    public static Direction fromDelta(int x, int y, int z) {
        for (Direction direction : Direction.values()) {
            if (direction.getStepX() == x && direction.getStepY() == y && direction.getStepZ() == z)
                return direction;
        }
        return null;
    }
}
