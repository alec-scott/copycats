package com.copycatsplus.copycats.config.fabric;

import com.copycatsplus.copycats.Copycats;
import com.copycatsplus.copycats.config.FeatureToggle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * PORT-NOTE(26.1): NeoForge's {@code copycats:feature_enabled} recipe condition as a Fabric resource condition
 * ({@code fabric:load_conditions}); the generated recipes were converted to use it.
 */
public record FeatureEnabledCondition(Identifier feature, boolean invert) implements ResourceCondition {
    public static final MapCodec<FeatureEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                    Identifier.CODEC.fieldOf("feature").forGetter(FeatureEnabledCondition::feature),
                    Codec.BOOL.optionalFieldOf("invert", false).forGetter(FeatureEnabledCondition::invert))
            .apply(builder, FeatureEnabledCondition::new));

    public static final ResourceConditionType<FeatureEnabledCondition> TYPE = ResourceConditionType.create(Copycats.asResource("feature_enabled"), CODEC);

    public static void register() {
        ResourceConditions.register(TYPE);
    }

    @Override
    public ResourceConditionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean test(@Nullable RegistryOps.RegistryInfoLookup registryInfo) {
        return FeatureToggle.isEnabled(feature) != invert;
    }
}
