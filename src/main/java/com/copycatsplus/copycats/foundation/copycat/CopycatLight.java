package com.copycatsplus.copycats.foundation.copycat;

import com.copycatsplus.copycats.utility.BlockEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import org.jetbrains.annotations.Nullable;

/**
 * Light emission of copycats from their materials.
 * <p>
 * PORT-NOTE(26.1): NeoForge has per-position light emission (AuxiliaryLightManager); Fabric doesn't, so mixins in
 * the light engines (vanilla's, and Starlight's for ScalableLux) call {@link #emission} where they read a block's
 * emission, and {@link BlockEntityUtils#updateLight} re-checks the block when the material changes. Light engines run
 * off the main thread, so this only reads already-loaded block entities and never throws.
 */
public final class CopycatLight {
    private CopycatLight() {
    }

    public static boolean isCopycat(BlockState state) {
        return state.getBlock() instanceof ICopycatBlock;
    }

    /**
     * The emission to use for a block: its own, or for a copycat the brightest material's.
     */
    public static int emission(int original, BlockState state, @Nullable BlockGetter chunk, BlockPos pos) {
        if (chunk == null || !isCopycat(state))
            return original;
        try {
            BlockEntity be;
            if (chunk instanceof LevelChunk levelChunk)
                be = levelChunk.getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
            else if (chunk instanceof ImposterProtoChunk imposter)
                // The server light engine reads through this wrapper. ModernFix makes its getBlockEntity return null off the
                // main thread, so read the wrapped chunk's map (no pending block entities are created here)
                be = imposter.getWrapped().getBlockEntities().get(pos);
            else
                be = chunk.getBlockEntity(pos);
            if (be == null)
                return original;
            return Math.max(original, BlockEntityUtils.getLightEmission(be));
        } catch (RuntimeException e) {
            // concurrent material change while lighting; the block is re-checked after the change anyway
            return original;
        }
    }

    public static int emission(int original, BlockState state, @Nullable LightChunkGetter chunks, int x, int y, int z) {
        if (chunks == null || !isCopycat(state))
            return original;
        return emission(original, state, chunks.getChunkForLighting(x >> 4, z >> 4), new BlockPos(x, y, z));
    }
}
