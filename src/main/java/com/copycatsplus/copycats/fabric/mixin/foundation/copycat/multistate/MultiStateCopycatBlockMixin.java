package com.copycatsplus.copycats.fabric.mixin.foundation.copycat.multistate;

import com.copycatsplus.copycats.content.copycat.cogwheel.CopycatCogWheelBlock;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.MaterialItemStorage;
import com.copycatsplus.copycats.foundation.copycat.multistate.MultiStateCopycatBlock;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.foundation.block.AppearanceControlBlock;
import com.zurrtum.create.foundation.block.EnchantingControlBlock;
import com.zurrtum.create.foundation.block.ResistanceControlBlock;
import com.zurrtum.create.foundation.block.SlipperinessControlBlock;
import com.zurrtum.create.foundation.block.SoundControlBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Implement platform-specific methods for multi-state copycat blocks.
 * <p>
 * All multi-state copycats should register their blocks here instead of writing their own platform-specific implementations.
 * <p>
 * PORT-NOTE(26.1): NeoForge's per-position block extensions map onto Create Fly's control interfaces; see the
 * simple-copycat CopycatBlockMixin for what has no Fabric equivalent. The pick-block item comes from the copycat's
 * first custom material, since 26.1's clone hook has no hit result to find the targeted part.
 */
@Mixin({
        MultiStateCopycatBlock.class,
        CopycatCogWheelBlock.class
})
public abstract class MultiStateCopycatBlockMixin extends Block implements IMultiStateCopycatBlock, SoundControlBlock,
        SlipperinessControlBlock, ResistanceControlBlock, EnchantingControlBlock, AppearanceControlBlock {

    public MultiStateCopycatBlockMixin(Properties properties) {
        super(properties);
    }

    @Unique
    @Nullable
    private BlockState copycats$firstCustomMaterial(BlockGetter level, BlockPos pos) {
        IMultiStateCopycatBlockEntity copycatBE = getCopycatBlockEntity(level, pos);
        if (copycatBE == null)
            return null;
        for (MaterialItemStorage.MaterialItem materialItem : copycatBE.getMaterialItemStorage().getAllMaterialItems()) {
            if (materialItem.hasCustomMaterial())
                return materialItem.material();
        }
        return null;
    }

    @Override
    public SoundType getSoundGroup(LevelReader level, BlockPos pos) {
        BlockState material = copycats$firstCustomMaterial(level, pos);
        if (material == null)
            material = ICopycatBlock.getMaterial(level, pos);
        return material.isAir() ? defaultBlockState().getSoundType() : material.getSoundType();
    }

    @Override
    public float getSlipperiness(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        IMultiStateCopycatBlockEntity copycatBE = getCopycatBlockEntity(level, pos);
        if (copycatBE == null)
            return getFriction();
        float bonus = 0f;
        int count = 0;
        for (String property : copycatBE.getMaterialItemStorage().getAllProperties()) {
            if (!partExists(state, property)) continue;
            BlockState mat = copycatBE.getMaterialItemStorage().getMaterialItem(property).material();
            count++;
            bonus += mat.isAir() ? getFriction() : mat.getBlock().getFriction();
        }
        return count == 0 ? getFriction() : bonus / count;
    }

    @Override
    public float getResistance(BlockGetter level, BlockPos pos) {
        float resistance = getExplosionResistance();
        IMultiStateCopycatBlockEntity copycatBE = getCopycatBlockEntity(level, pos);
        if (copycatBE == null)
            return resistance;
        for (BlockState material : copycatBE.getMaterialItemStorage().getAllMaterials())
            resistance = Math.max(resistance, material.getBlock().getExplosionResistance());
        return resistance;
    }

    @Override
    public BlockState getEnchantmentPowerProvider(Level level, BlockPos pos) {
        IMultiStateCopycatBlockEntity copycatBE = getCopycatBlockEntity(level, pos);
        if (copycatBE != null)
            for (BlockState material : copycatBE.getMaterialItemStorage().getAllMaterials())
                if (material.is(BlockTags.ENCHANTMENT_POWER_PROVIDER))
                    return material;
        return defaultBlockState();
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndLightGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        return IMultiStateCopycatBlock.getAppearance(this, state, level, pos, side, queryState, queryPos);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        BlockState material = copycats$firstCustomMaterial(level, pos);
        if (material == null || material.is(AllBlocks.COPYCAT_BASE))
            return new ItemStack(this);
        return material.getCloneItemStack(level, pos, includeData);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        BlockHitResult hitResult = level.clip(new ClipContext(entity.position(), entity.position().add(0, -2, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, entity));
        String property = getPropertyFromInteraction(state, level, pos, hitResult, true);
        IMultiStateCopycatBlockEntity copycatBE = getCopycatBlockEntity(level, pos);
        MaterialItemStorage.MaterialItem item = copycatBE == null || property == null ? null : copycatBE.getMaterialItemStorage().getMaterialItem(property);
        if (item == null || item.material().isAir()) {
            super.fallOn(level, state, pos, entity, fallDistance);
            return;
        }
        BlockState material = item.material();
        material.getBlock().fallOn(level, material, pos, entity, fallDistance);
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
