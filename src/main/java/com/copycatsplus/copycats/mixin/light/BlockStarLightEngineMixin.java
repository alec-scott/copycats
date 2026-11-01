package com.copycatsplus.copycats.mixin.light;

import com.copycatsplus.copycats.compat.Mods;
import com.copycatsplus.copycats.foundation.annotation.ModMixin;
import com.copycatsplus.copycats.foundation.copycat.CopycatLight;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LightChunkGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Copycats emit their material's light in Starlight's block light engine (as used by ScalableLux), see CopycatLight.
 */
@ModMixin(requiredMods = Mods.STARLIGHT)
@Pseudo
@Mixin(targets = "ca.spottedleaf.starlight.common.light.BlockStarLightEngine", remap = false)
public class BlockStarLightEngineMixin {
    private static final String EMISSION = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I";

    @WrapOperation(method = "checkBlock", at = @At(value = "INVOKE", target = EMISSION))
    private int copycats$checkBlock(BlockState state, Operation<Integer> original, @Local(argsOnly = true) LightChunkGetter chunks,
                                    @Local(argsOnly = true, ordinal = 0) int x, @Local(argsOnly = true, ordinal = 1) int y,
                                    @Local(argsOnly = true, ordinal = 2) int z) {
        return CopycatLight.emission(original.call(state), state, chunks, x, y, z);
    }

    @WrapOperation(method = "calculateLightValue", at = @At(value = "INVOKE", target = EMISSION))
    private int copycats$calculateLightValue(BlockState state, Operation<Integer> original, @Local(argsOnly = true) LightChunkGetter chunks,
                                             @Local(argsOnly = true, ordinal = 0) int x, @Local(argsOnly = true, ordinal = 1) int y,
                                             @Local(argsOnly = true, ordinal = 2) int z) {
        return CopycatLight.emission(original.call(state), state, chunks, x, y, z);
    }

    // getSources scans each section by palette index: chunk origin x/z (locals 4, 5), section origin y (10), index (11)
    @WrapOperation(method = "getSources", at = @At(value = "INVOKE", target = EMISSION))
    private int copycats$getSources(BlockState state, Operation<Integer> original, @Local(argsOnly = true) ChunkAccess chunk,
                                    @Local(index = 4) int offX, @Local(index = 5) int offZ, @Local(index = 10) int offY,
                                    @Local(index = 11) int index) {
        int emission = original.call(state);
        if (!CopycatLight.isCopycat(state))
            return emission;
        return CopycatLight.emission(emission, state, chunk, new BlockPos(offX | (index & 15), offY | (index >>> 8), offZ | ((index >>> 4) & 15)));
    }

    @WrapOperation(method = "lambda$getSources$0", at = @At(value = "INVOKE", target = EMISSION))
    private static int copycats$copycatsMayEmit(BlockState state, Operation<Integer> original) {
        int emission = original.call(state);
        return emission == 0 && CopycatLight.isCopycat(state) ? 1 : emission;
    }

    @WrapOperation(method = "lightChunk", at = @At(value = "INVOKE", target = EMISSION))
    private int copycats$lightChunk(BlockState state, Operation<Integer> original, @Local(argsOnly = true) ChunkAccess chunk,
                                    @Local BlockPos pos) {
        int emission = original.call(state);
        if (!CopycatLight.isCopycat(state))
            return emission;
        return CopycatLight.emission(emission, state, chunk, pos);
    }
}
