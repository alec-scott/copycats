package com.copycatsplus.copycats.foundation.copycat;

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
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Base class for simple copycat blocks. Extend this class for simple copycat blocks that do not require
 * another base class and can be waterlogged.
 */
@ParametersAreNonnullByDefault
public abstract class CCWaterloggedCopycatBlock extends CCCopycatBlock implements ProperWaterloggedBlock {

    public CCWaterloggedCopycatBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
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
    public BlockState updateShape(BlockState pState, LevelReader pLevel, ScheduledTickAccess ticks, BlockPos pCurrentPos, Direction pDirection, BlockPos pNeighborPos, BlockState pNeighborState, RandomSource random) {
        updateWater(pLevel, ticks, pState, pCurrentPos);
        return pState;
    }

}

