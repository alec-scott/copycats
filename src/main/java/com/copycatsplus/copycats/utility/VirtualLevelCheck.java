package com.copycatsplus.copycats.utility;

import com.zurrtum.create.client.foundation.virtualWorld.VirtualRenderWorld;
import net.minecraft.world.level.Level;

/**
 * Kept separate from common code so the client-only VirtualRenderWorld is only loaded when a non-server level asks.
 */
public class VirtualLevelCheck {
    public static boolean isVirtual(Level level) {
        return level instanceof VirtualRenderWorld;
    }
}
