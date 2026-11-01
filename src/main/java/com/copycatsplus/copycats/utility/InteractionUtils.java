package com.copycatsplus.copycats.utility;

import com.zurrtum.create.catnip.placement.IPlacementHelper;
import com.zurrtum.create.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import java.util.function.Supplier;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class InteractionUtils {
    /**
     * Sequentially runs a list of handlers until one consumes the action.
     */
    @SafeVarargs
    public static InteractionResult sequential(Supplier<InteractionResult>... handlers) {
        for (Supplier<InteractionResult> handler : handlers) {
            InteractionResult result = handler.get();
            if (result.consumesAction()) {
                return result;
            }
        }
        return InteractionResult.PASS;
    }

    /**
     * Sequentially runs a list of handlers until one consumes the action.
     */
    @SafeVarargs
    public static InteractionResult sequentialItem(Supplier<InteractionResult>... handlers) {
        for (Supplier<InteractionResult> handler : handlers) {
            InteractionResult result = handler.get();
            if (result.consumesAction()) {
                return result;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    /**
     * Handles the use action with a registered placement helper.
     */
    public static InteractionResult usePlacementHelper(int placementHelperId, ItemStack heldItem, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        if (!player.isShiftKeyDown() && player.mayBuild()) {
            IPlacementHelper placementHelper = PlacementHelpers.get(placementHelperId);
            if (placementHelper.matchesItem(heldItem)) {
                placementHelper.getOffset(player, world, state, pos, ray)
                        .placeInWorld(world, (BlockItem) heldItem.getItem(), player, hand);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    public static AttributeInstance getPlayerReach(Player player) {
        return com.copycatsplus.copycats.utility.fabric.InteractionUtilsImpl.getPlayerReach(player);
    }
}
