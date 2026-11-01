package com.copycatsplus.copycats;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.port.create.foundation.data.CreateRegistrate;
import com.copycatsplus.copycats.port.registrate.util.nullness.NonNullConsumer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel.UnbakedRoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Supplier;

// PORT-NOTE(26.1): models wrap the block's unbaked block state model (Create Fly's AllModels.register) instead of a
// baked model; the unused item model variants were dropped.
public class CCCustomModels {

    public static @NotNull <Model extends CopycatModelCore> NonNullConsumer<? super Block> createBlockModel(Supplier<Model> model) {
        return CreateRegistrate.blockModel(() -> (state, original) -> CopycatModelCore.createModel(state, original, model.get()));
    }

    public static @NotNull <Model extends CopycatModelCore> NonNullConsumer<? super Block> createBlockModel(BiFunction<BlockState, UnbakedRoot, UnbakedRoot> baseModel, Supplier<Model> model) {
        return CreateRegistrate.blockModel(() -> (state, original) -> CopycatModelCore.createModel(state, baseModel.apply(state, original), model.get()));
    }

    public static UnbakedRoot getFluidPipeModel(BlockState state, UnbakedRoot original, CopycatModelCore copycat, boolean disableAO) {
        return com.copycatsplus.copycats.fabric.CCCustomModelsImpl.getFluidPipeModel(state, original, copycat, disableAO);
    }
}
