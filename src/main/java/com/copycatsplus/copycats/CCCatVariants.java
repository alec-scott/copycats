package com.copycatsplus.copycats;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.feline.CatVariant;

/**
 * PORT-NOTE(26.1): cat variants are data driven; the copy cat is {@code data/copycats/cat_variant/copy_cat.json}
 * (no spawn conditions, so it never spawns naturally). 26.1 also needs a kitten texture, which Copycats+ doesn't
 * have, so copy cat kittens use the all-black kitten texture.
 */
public class CCCatVariants {
    public static final ResourceKey<CatVariant> COPY_CAT = ResourceKey.create(Registries.CAT_VARIANT, Copycats.asResource("copy_cat"));

    public static void register() {
    }
}
