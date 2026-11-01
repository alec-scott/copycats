package com.copycatsplus.copycats.mixin.copycat.pick;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Pick block on a copycat gives the copied material; sneaking picks the copycat itself (as upstream).
 * <p>
 * PORT-NOTE(26.1): getCloneItemStack lost its player argument (pick block is handled on the server), so the sneak
 * check happens here.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @WrapOperation(
            method = "handlePickItemFromBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCloneItemStack(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack copycats$pickCopycatWhenSneaking(BlockState state, LevelReader level, BlockPos pos, boolean includeData, Operation<ItemStack> original) {
        if (state.getBlock() instanceof ICopycatBlock && player.isShiftKeyDown())
            return new ItemStack(state.getBlock());
        return original.call(state, level, pos, includeData);
    }
}
