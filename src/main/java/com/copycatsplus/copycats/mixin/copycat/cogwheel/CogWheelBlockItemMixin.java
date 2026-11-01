package com.copycatsplus.copycats.mixin.copycat.cogwheel;

import com.copycatsplus.copycats.content.copycat.cogwheel.CopycatCogWheelBlock;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlock;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.zurrtum.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Skip the placement helper in cogwheels if the player is trying to fill a copycat cogwheel
 * <p>
 * PORT-NOTE(26.1): Create Fly's onItemUseFirst is a static hook that returns null to fall through to the normal use.
 */
@Mixin(value = CogwheelBlockItem.class, priority = 1200, remap = false)
// Use a higher priority because this may be overwritten by other mods
public class CogWheelBlockItemMixin {
    @Inject(
            method = "onItemUseFirst",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void disablePlacementForCopycat(Level world, @Nullable Player player, ItemStack stack, InteractionHand hand,
                                                   BlockHitResult ray, BlockPos pos, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(stack.getItem() instanceof CogwheelBlockItem item))
            return;
        Block block = item.getBlock();
        if (block instanceof ICopycatBlock)
            return;
        if (!(block instanceof CogWheelBlock cogWheelBlock))
            return;
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof CopycatCogWheelBlock copycatBlock) {
            String property = copycatBlock.getPropertyFromInteraction(state, world, pos, ray.getLocation(), ray.getDirection(), true);
            if (property.equals(CopycatCogWheelBlock.Part.COGWHEEL.getSerializedName())
                    && IMultiStateCopycatBlock.getMaterial(world, pos, property).is(AllBlocks.COPYCAT_BASE)
                    && cogWheelBlock.isLargeCog() == copycatBlock.isLargeCog()) {
                cir.setReturnValue(null);
            }
        }
    }
}
