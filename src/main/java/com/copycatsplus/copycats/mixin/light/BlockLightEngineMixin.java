package com.copycatsplus.copycats.mixin.light;

import com.copycatsplus.copycats.foundation.copycat.CopycatLight;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Copycats emit their material's light in the vanilla block light engine (see CopycatLight).
 */
@Mixin(BlockLightEngine.class)
public class BlockLightEngineMixin {
    @WrapOperation(method = "getEmission", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"))
    private int copycats$emission(BlockState state, Operation<Integer> original, @Local(argsOnly = true) long pos) {
        int emission = original.call(state);
        if (!CopycatLight.isCopycat(state))
            return emission;
        return CopycatLight.emission(emission, state, ((LightEngineAccessor) this).copycats$getChunkSource(),
                BlockPos.getX(pos), BlockPos.getY(pos), BlockPos.getZ(pos));
    }

    @WrapOperation(method = "lambda$propagateLightSources$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"))
    private int copycats$sourceEmission(BlockState state, Operation<Integer> original, @Local(argsOnly = true) BlockPos pos) {
        int emission = original.call(state);
        if (!CopycatLight.isCopycat(state))
            return emission;
        return CopycatLight.emission(emission, state, ((LightEngineAccessor) this).copycats$getChunkSource(), pos.getX(), pos.getY(), pos.getZ());
    }
}
