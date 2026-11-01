package com.copycatsplus.copycats.foundation.copycat.model.kinetic;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.zurrtum.create.client.foundation.virtualWorld.VirtualRenderWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A virtual world to render the kinetic copycat models in.
 * <p>
 * PORT-NOTE(26.1): no model data; the copycat model reads the block entity through {@link #getBlockEntity}.
 */
@ApiStatus.Internal
public class WrappedRenderWorld extends VirtualRenderWorld {
    protected final Level level;
    protected final BlockPos targetPos;
    protected final BlockState material;

    public WrappedRenderWorld(ICopycatBlockEntity be) {
        super(be.getLevel(), be.getLevel().getMinY(), be.getLevel().getHeight(), Vec3i.ZERO, () -> {
        });
        this.level = be.getLevel();
        this.targetPos = be.getBlockPos();
        this.material = be.getMaterial();
    }

    public Level getWrappedLevel() {
        return this.level;
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(@NotNull BlockPos pos) {
        if (!pos.equals(targetPos)) return null;
        return level.getBlockEntity(pos);
    }

    @Override
    public @NotNull BlockState getBlockState(@NotNull BlockPos pos) {
        if (!pos.equals(targetPos)) return material;
        return level.getBlockState(pos);
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockPos pos) {
        if (!pos.equals(targetPos)) return Fluids.EMPTY.defaultFluidState();
        return level.getFluidState(pos);
    }

    @Override
    public CardinalLighting cardinalLighting() {
        return new CardinalLighting(1, 1, 1, 1, 1, 1);
    }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver resolver) {
        Biome plainsBiome = Minecraft.getInstance().getConnection().registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS).value();
        return resolver.getColor(plainsBiome, pos.getX(), pos.getZ());
    }

    @Override
    public void blockEntityChanged(BlockPos pos) {
        // no-op
        // cannot set changed state because getChunk is not implemented
    }
}
