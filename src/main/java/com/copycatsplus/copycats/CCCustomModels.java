package com.copycatsplus.copycats;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.port.create.foundation.data.CreateRegistrate;
import com.copycatsplus.copycats.port.registrate.util.nullness.NonNullConsumer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class CCCustomModels {

    public static @NotNull <Model extends CopycatModelCore> NonNullConsumer<? super Block> createBlockModel(Supplier<Model> model) {
        return CreateRegistrate.blockModel(() -> original -> CopycatModelCore.createModel(original, model.get()));
    }

    public static @NotNull <Model extends CopycatModelCore> NonNullConsumer<? super Block> createBlockModel(Function<BakedModel, BakedModel> baseModel, Supplier<Model> model) {
        return CreateRegistrate.blockModel(() -> original -> CopycatModelCore.createModel(baseModel.apply(original), model.get()));
    }

    //Just in case we want/need it
    public static @NotNull <Model extends CopycatModelCore> NonNullConsumer<? super BlockItem> createItemModel(Supplier<Model> model) {
        return CreateRegistrate.itemModel(() -> original -> CopycatModelCore.createModel(original, model.get()));
    }

    public static @NotNull <Model extends CopycatModelCore> NonNullConsumer<? super BlockItem> createItemModel(Function<BakedModel, BakedModel> baseModel, Supplier<Model> model) {
        return CreateRegistrate.itemModel(() -> original -> CopycatModelCore.createModel(baseModel.apply(original), model.get()));
    }

    public static BakedModel getFluidPipeModel(BakedModel original, CopycatModelCore copycat, boolean disableAO) {
        return com.copycatsplus.copycats.fabric.CCCustomModelsImpl.getFluidPipeModel(original, copycat, disableAO);
    }
}
