package com.copycatsplus.copycats.mixin.light;

import com.copycatsplus.copycats.foundation.copycat.CopycatLight;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Treat copycats as possible light sources when a chunk's light sources are collected (their emission is then read
 * per position, see BlockLightEngineMixin).
 */
@Mixin(ChunkAccess.class)
public class ChunkAccessMixin {
    @WrapOperation(method = "lambda$findBlockLightSources$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"))
    private static int copycats$copycatsMayEmit(BlockState state, Operation<Integer> original) {
        int emission = original.call(state);
        return emission == 0 && CopycatLight.isCopycat(state) ? 1 : emission;
    }
}
