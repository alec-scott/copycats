package com.copycatsplus.copycats.foundation.copycat.multistate;

import net.minecraft.world.level.ScheduledTickAccess;

import net.minecraft.world.level.LevelReader;

import net.minecraft.util.RandomSource;

import com.zurrtum.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Base class for waterlogged multi-state copycat blocks. Extend this class for multi-state copycat blocks that do not require
 * another base class and can be waterlogged.
 */
@ParametersAreNonnullByDefault
public abstract class WaterloggedMultiStateCopycatBlock extends MultiStateCopycatBlock implements ProperWaterloggedBlock {
    public WaterloggedMultiStateCopycatBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(WATERLOGGED));
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return fluidState(pState);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return withWater(super.getStateForPlacement(pContext), pContext);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState pState, LevelReader pLevel, ScheduledTickAccess ticks, BlockPos pCurrentPos, Direction pDirection, BlockPos pNeighborPos, BlockState pNeighborState, RandomSource random) {
        super.updateShape(pState, pLevel, ticks, pCurrentPos, pDirection, pNeighborPos, pNeighborState, random);
        updateWater(pLevel, ticks, pState, pCurrentPos);
        return pState;
    }
}
