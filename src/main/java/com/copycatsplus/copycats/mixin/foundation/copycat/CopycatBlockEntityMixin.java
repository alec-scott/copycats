package com.copycatsplus.copycats.mixin.foundation.copycat;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.zurrtum.create.content.decoration.copycat.CopycatBlockEntity;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Expand Create's {@link CopycatBlockEntity} with CT toggle.
 * <p>
 * Also implement {@link ICopycatBlockEntity} to unify instanceof checks.
 */
@Mixin(value = CopycatBlockEntity.class, remap = false)
public abstract class CopycatBlockEntityMixin implements ICopycatBlockEntity {
    @Shadow
    private BlockState material;

    @Shadow
    private ItemStack consumedItem;

    @Unique
    private boolean copycats$enableCT = true;

    @Override
    public boolean isCTEnabled() {
        return copycats$enableCT;
    }

    @Override
    public void setMaterialInternal(BlockState material) {
        this.material = material;
    }

    @Override
    public void setConsumedItemInternal(ItemStack consumedItem) {
        this.consumedItem = consumedItem;
    }

    @Override
    public void setCTEnabledInternal(boolean value) {
        this.copycats$enableCT = value;
    }

    @Inject(
            at = @At("HEAD"),
            method = "write(Lnet/minecraft/world/level/storage/ValueOutput;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)V"
    )
    private void writeCT(ValueOutput view, ItemStack stack, BlockState material, CallbackInfo ci) {
        view.putBoolean("EnableCT", copycats$enableCT);
    }

    @Inject(
            at = @At("HEAD"),
            method = "read"
    )
    private void readCT(ValueInput view, boolean clientPacket, CallbackInfo ci) {
        // EnableCT may be missing on copycats migrated from C:Connected
        copycats$enableCT = view.getBooleanOr("EnableCT", true);
    }
}
