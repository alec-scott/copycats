package com.copycatsplus.copycats.mixin.copycat;

import com.zurrtum.create.foundation.blockEntity.SyncedBlockEntity;
import com.copycatsplus.copycats.utility.VirtualLevelCheck;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for Create 6.0.7 and up, where calling `notifyUpdate` on a virtual world causes a crash due to getChunk being
 * unimplemented in VirtualRenderWorld
 */
@Mixin(SyncedBlockEntity.class)
public class SyncedBlockEntityMixin {
    @Inject(
            method = "notifyUpdate()V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void doNotSyncVirtual(CallbackInfo ci) {
        Level level = ((SyncedBlockEntity) (Object) this).getLevel();
        // Virtual render worlds are client only; never touch the client class on a server level
        if (level != null && !(level instanceof ServerLevel) && VirtualLevelCheck.isVirtual(level)) {
            ci.cancel();
        }
    }
}
