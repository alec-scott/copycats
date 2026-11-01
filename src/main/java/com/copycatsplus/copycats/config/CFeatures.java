package com.copycatsplus.copycats.config;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all configs related to the feature toggle.
 * Values in this class should NOT be accessed directly. Please access via {@link FeatureToggle} instead.
 */
public class CFeatures extends SyncConfigBase {

    @Override
    public String getName() {
        return "features";
    }

    final Map<Identifier, ModConfigSpec.ConfigValue<Boolean>> toggles = new HashMap<>();

    Map<Identifier, Boolean> synchronizedToggles;

    @Override
    public void registerAll(ModConfigSpec.Builder builder) {
        FeatureToggle.TOGGLEABLE_FEATURES.forEach((r) -> toggles.put(r, builder.define(r.getPath(), true)));
    }

    @ApiStatus.Internal
    public boolean hasToggle(Identifier key) {
        return (synchronizedToggles != null && synchronizedToggles.containsKey(key)) || toggles.containsKey(key);
    }

    @ApiStatus.Internal
    public boolean isEnabled(Identifier key) {
        if (this.synchronizedToggles != null) {
            Boolean synced = synchronizedToggles.get(key);
            if (synced != null) return synced;
        }
        ModConfigSpec.ConfigValue<Boolean> value = toggles.get(key);
        if (value != null)
            return value.get();
        return true;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        FeatureToggle.refreshItemVisibility();
    }

    @Override
    public void onReload() {
        super.onReload();
        FeatureToggle.refreshItemVisibility();
    }

    @Override
    protected void readSyncConfig(CompoundTag nbt) {
        synchronizedToggles = new HashMap<>();
        for (String key : nbt.keySet()) {
            Identifier location = Identifier.parse(key);
            synchronizedToggles.put(location, nbt.getBooleanOr(key, false));
        }
        FeatureToggle.refreshItemVisibility();
    }

    @Override
    protected ModConfig.Type type() {
        return ModConfig.Type.COMMON;
    }

    @Override
    protected void writeSyncConfig(CompoundTag nbt) {
        toggles.forEach((key, value) -> nbt.putBoolean(key.toString(), value.get()));
    }
}
