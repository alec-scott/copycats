package com.copycatsplus.copycats.fabric.mixin.foundation.copycat;

import com.copycatsplus.copycats.content.copycat.button.CopycatButtonBlock;
import com.copycatsplus.copycats.content.copycat.door.CopycatDoorBlock;
import com.copycatsplus.copycats.content.copycat.fence.CopycatFenceBlock;
import com.copycatsplus.copycats.content.copycat.fence_gate.CopycatFenceGateBlock;
import com.copycatsplus.copycats.content.copycat.fluid_pipe.CopycatFluidPipeBlock;
import com.copycatsplus.copycats.content.copycat.fluid_pipe.CopycatGlassFluidPipeBlock;
import com.copycatsplus.copycats.content.copycat.ladder.CopycatLadderBlock;
import com.copycatsplus.copycats.content.copycat.pane.CopycatPaneBlock;
import com.copycatsplus.copycats.content.copycat.pressure_plate.CopycatPressurePlateBlock;
import com.copycatsplus.copycats.content.copycat.pressure_plate.CopycatWeightedPressurePlate;
import com.copycatsplus.copycats.content.copycat.shaft.CopycatShaftBlock;
import com.copycatsplus.copycats.content.copycat.sliding_door.CopycatSlidingDoorBlock;
import com.copycatsplus.copycats.content.copycat.stairs.CopycatStairsBlock;
import com.copycatsplus.copycats.content.copycat.trapdoor.CopycatTrapdoorBlock;
import com.copycatsplus.copycats.content.copycat.wall.CopycatWallBlock;
import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.foundation.block.AppearanceControlBlock;
import com.zurrtum.create.foundation.block.EnchantingControlBlock;
import com.zurrtum.create.foundation.block.ResistanceControlBlock;
import com.zurrtum.create.foundation.block.SlipperinessControlBlock;
import com.zurrtum.create.foundation.block.SoundControlBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Implement platform-specific methods for copycat blocks.
 * <p>
 * All non-multi-state copycats should register their blocks here instead of writing their own platform-specific implementations.
 * <p>
 * PORT-NOTE(26.1): NeoForge's per-position block extensions map onto Create Fly's control interfaces (the same set
 * Create Fly's own copycats implement). Light emission, harvest checks, entity destruction and landing/running
 * particles have no Fabric equivalent and use the copycat's own behaviour.
 */
@Mixin({
        CCCopycatBlock.class,
        CopycatButtonBlock.class,
        CopycatFenceBlock.class,
        CopycatFenceGateBlock.class,
        CopycatLadderBlock.class,
        CopycatPressurePlateBlock.class,
        CopycatWeightedPressurePlate.class,
        CopycatStairsBlock.class,
        CopycatDoorBlock.class,
        CopycatTrapdoorBlock.class,
        CopycatWallBlock.class,
        CopycatShaftBlock.class,
        CopycatFluidPipeBlock.class,
        CopycatGlassFluidPipeBlock.class,
        CopycatSlidingDoorBlock.class,
        CopycatPaneBlock.class
})
public abstract class CopycatBlockMixin extends Block implements ICopycatBlock, SoundControlBlock, SlipperinessControlBlock,
        ResistanceControlBlock, EnchantingControlBlock, AppearanceControlBlock {

    public CopycatBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public SoundType getSoundGroup(LevelReader level, BlockPos pos) {
        BlockState material = ICopycatBlock.getMaterial(level, pos);
        return material.isAir() ? defaultBlockState().getSoundType() : material.getSoundType();
    }

    @Override
    public float getSlipperiness(LevelReader level, BlockPos pos) {
        BlockState material = ICopycatBlock.getMaterial(level, pos);
        if (material.getBlock() instanceof SlipperinessControlBlock control)
            return control.getSlipperiness(level, pos);
        return material.isAir() ? getFriction() : material.getBlock().getFriction();
    }

    @Override
    public float getResistance(BlockGetter level, BlockPos pos) {
        BlockState material = ICopycatBlock.getMaterial(level, pos);
        return material.isAir() ? getExplosionResistance() : material.getBlock().getExplosionResistance();
    }

    @Override
    public BlockState getEnchantmentPowerProvider(Level level, BlockPos pos) {
        return ICopycatBlock.getMaterial(level, pos);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndLightGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        return ICopycatBlock.getAppearance(this, state, level, pos, side, queryState, queryPos);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        BlockState material = ICopycatBlock.getMaterial(level, pos);
        if (material.isAir() || material.is(AllBlocks.COPYCAT_BASE))
            return super.getCloneItemStack(level, pos, state, includeData);
        return material.getCloneItemStack(level, pos, includeData);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        BlockState material = ICopycatBlock.getMaterial(level, pos);
        if (material.isAir()) {
            super.fallOn(level, state, pos, entity, fallDistance);
            return;
        }
        material.getBlock().fallOn(level, material, pos, entity, fallDistance);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        BlockState material = ICopycatBlock.getMaterial(level, pos);
        if (material.isAir())
            return super.getDestroyProgress(state, player, level, pos);
        return material.getDestroyProgress(player, level, pos);
    }

    /*
     * The following overrides are just to make the compiler happy about this mixin class.
     * They do not actually overwrite the corresponding methods.
     */
    @Override
    @Unique
    public abstract BlockState rotate(BlockState state, Rotation rotation);

    @Override
    @Unique
    public abstract BlockState mirror(BlockState state, Mirror mirror);

    @Override
    @Unique
    public abstract InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult);

    @Override
    @Unique
    public abstract InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult);
}
