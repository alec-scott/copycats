package com.copycatsplus.copycats.foundation.copycat.model.fabric;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel.UnbakedRoot;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CopycatModelCoreImpl {

    @NotNull
    public static UnbakedRoot createModel(BlockState state, UnbakedRoot original, CopycatModelCore core) {
        return new CopycatModelFabric(state, original, core, false);
    }

    @NotNull
    public static UnbakedRoot createKineticModel(BlockState state, UnbakedRoot original, CopycatModelCore core) {
        return new CopycatModelFabric(state, original, core, true);
    }
}
