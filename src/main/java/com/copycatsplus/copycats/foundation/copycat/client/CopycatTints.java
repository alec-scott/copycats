package com.copycatsplus.copycats.foundation.copycat.client;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * PORT-NOTE(26.1): block colours are a list of {@link BlockTintSource}s, one per tint index, and the NeoForge port
 * picked a multi-state part through a sprite wrapper seen by a renderer mixin. Here the copycat model rewrites each
 * quad's tint index instead: simple copycats keep the material's index, multi-state copycats use
 * {@code partIndex * TINTS_PER_PART + materialIndex} (see {@link #multiStateTintIndex}).
 */
public class CopycatTints {
    public static final int TINTS_PER_PART = 4;
    /** Upper bound on parts per multi-state copycat (the byte has 8). */
    public static final int MAX_PARTS = 16;

    public static List<BlockTintSource> simple() {
        List<BlockTintSource> sources = new ArrayList<>(TINTS_PER_PART);
        for (int i = 0; i < TINTS_PER_PART; i++)
            sources.add(new MaterialTint(i, (level, pos) -> ICopycatBlock.getMaterial(level, pos)));
        return sources;
    }

    public static List<BlockTintSource> multiState() {
        List<BlockTintSource> sources = new ArrayList<>(TINTS_PER_PART * MAX_PARTS);
        for (int part = 0; part < MAX_PARTS; part++) {
            int partIndex = part;
            for (int i = 0; i < TINTS_PER_PART; i++)
                sources.add(new MaterialTint(i, (level, pos) -> partMaterial(level, pos, partIndex)));
        }
        return sources;
    }

    public static int multiStateTintIndex(IMultiStateCopycatBlock block, String property, int materialTintIndex) {
        if (materialTintIndex < 0 || materialTintIndex >= TINTS_PER_PART)
            return materialTintIndex;
        int part = partIndex(block, property);
        if (part < 0 || part >= MAX_PARTS)
            return materialTintIndex;
        return part * TINTS_PER_PART + materialTintIndex;
    }

    public static int partIndex(IMultiStateCopycatBlock block, String property) {
        return block.storageProperties().stream().sorted().toList().indexOf(property);
    }

    private static BlockState partMaterial(BlockAndTintGetter level, BlockPos pos, int partIndex) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof IMultiStateCopycatBlock block) {
            List<String> parts = block.storageProperties().stream().sorted().toList();
            if (partIndex < parts.size())
                return IMultiStateCopycatBlock.getMaterial(level, pos, parts.get(partIndex));
        }
        return ICopycatBlock.getMaterial(level, pos);
    }

    private record MaterialTint(int tintIndex, BiFunction<BlockAndTintGetter, BlockPos, BlockState> material) implements BlockTintSource {
        @Override
        public int color(BlockState state) {
            return GrassColor.getDefaultColor();
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            List<BlockTintSource> sources = Minecraft.getInstance().getBlockColors().getTintSources(material.apply(level, pos));
            if (sources.size() <= tintIndex)
                return -1;
            return sources.get(tintIndex).colorInWorld(state, level, pos);
        }

        @Override
        public int colorAsTerrainParticle(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            List<BlockTintSource> sources = Minecraft.getInstance().getBlockColors().getTintSources(material.apply(level, pos));
            if (sources.size() <= tintIndex)
                return -1;
            return sources.get(tintIndex).colorAsTerrainParticle(state, level, pos);
        }
    }
}
