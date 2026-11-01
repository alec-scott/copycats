package com.copycatsplus.copycats.datagen.recipes.fabric;

import com.copycatsplus.copycats.datagen.recipes.gen.GeneratedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public class CCStandardRecipesImpl {


    public static GeneratedRecipeBuilder create(Supplier<ItemLike> result) {
        return new GeneratedRecipeBuilderNeoForge("/", result);
    }

    public static GeneratedRecipeBuilder create(Identifier result) {
        return new GeneratedRecipeBuilderNeoForge("/", result);
    }
}

