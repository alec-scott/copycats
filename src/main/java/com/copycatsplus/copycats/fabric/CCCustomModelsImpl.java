package com.copycatsplus.copycats.fabric;

import com.copycatsplus.copycats.content.copycat.fluid_pipe.fabric.CopycatFluidPipeModelFabric;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel.UnbakedRoot;
import net.minecraft.world.level.block.state.BlockState;

public class CCCustomModelsImpl {

    public static UnbakedRoot getFluidPipeModel(BlockState state, UnbakedRoot original, CopycatModelCore copycat, boolean disableAO) {
        return new CopycatFluidPipeModelFabric(state, original, copycat, disableAO);
    }
}
