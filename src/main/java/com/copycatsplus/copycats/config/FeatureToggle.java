package com.copycatsplus.copycats.config;

import com.copycatsplus.copycats.compat.Mods;
import com.copycatsplus.copycats.mixin.feature_toggle.CreativeModeTabsAccessor;
import com.copycatsplus.copycats.utility.LogicalSidedProvider;
import com.copycatsplus.copycats.utility.Platform;
import com.copycatsplus.copycats.port.registrate.builders.Builder;
import com.copycatsplus.copycats.port.registrate.util.entry.BlockEntry;
import com.copycatsplus.copycats.port.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FeatureToggle {
    public static final Set<Identifier> TOGGLEABLE_FEATURES = new HashSet<>();
    public static final Map<Identifier, Identifier> DEPENDENT_FEATURES = new HashMap<>();
    public static final Map<Identifier, Set<FeatureCategory>> FEATURE_CATEGORIES = new HashMap<>();

    public static void register(Identifier key) {
        TOGGLEABLE_FEATURES.add(key);
    }

    public static void register(Identifier key, FeatureCategory... categories) {
        register(key);
        FEATURE_CATEGORIES.put(key, Set.of(categories));
    }

    public static void registerDependent(Identifier key, Identifier dependency) {
        DEPENDENT_FEATURES.put(key, dependency);
    }

    public static void registerDependent(Identifier key, Identifier dependency, FeatureCategory... categories) {
        registerDependent(key, dependency);
        FEATURE_CATEGORIES.put(key, Set.of(categories));
    }

    /**
     * Register this object to be a feature that is toggleable by the user
     */
    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> register() {
        return b -> {
            register(Identifier.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()));
            return b;
        };
    }

    /**
     * Register this object to be a feature that is toggleable by the user
     */
    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> register(FeatureCategory... categories) {
        return b -> {
            register(Identifier.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()), categories);
            return b;
        };
    }

    /**
     * Register this object to be dependent on another feature.
     * This object cannot be toggled directly, and will only be enabled if the dependency is enabled.
     */
    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> registerDependent(Identifier dependency) {
        return b -> {
            registerDependent(Identifier.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()), dependency);
            return b;
        };
    }

    /**
     * Register this object to be dependent on another feature.
     * This object cannot be toggled directly, and will only be enabled if the dependency is enabled.
     */
    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> registerDependent(Identifier dependency, FeatureCategory... categories) {
        return b -> {
            registerDependent(Identifier.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()), dependency, categories);
            return b;
        };
    }

    /**
     * Register this object to be dependent on another feature.
     * This object cannot be toggled directly, and will only be enabled if the dependency is enabled.
     */
    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> registerDependent(BlockEntry<?> dependency) {
        return b -> {
            registerDependent(Identifier.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()), dependency.getId());
            return b;
        };
    }

    /**
     * Register this object to be dependent on another feature.
     * This object cannot be toggled directly, and will only be enabled if the dependency is enabled.
     */
    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> registerDependent(BlockEntry<?> dependency, FeatureCategory... categories) {
        return b -> {
            registerDependent(Identifier.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()), dependency.getId(), categories);
            return b;
        };
    }

    private static CFeatures getToggles() {
        return CCConfigs.common().toggle;
    }

    private static CFeatureCategories getCategories() {
        return CCConfigs.common().categories;
    }

    /**
     * Check whether a feature is enabled.
     * If the provided {@link Identifier} is not registered with this feature toggle, it is assumed to be enabled.
     *
     * @param key The {@link Identifier} of the feature.
     * @return Whether the feature is enabled.
     */
    public static boolean isEnabled(Identifier key) {
        if (FEATURE_CATEGORIES.containsKey(key)) {
            Set<FeatureCategory> categories = FEATURE_CATEGORIES.get(key);
            for (FeatureCategory category : categories) {
                if (!getCategories().isEnabled(category)) return false;
            }
        }
        if (getToggles().hasToggle(key)) {
            return getToggles().isEnabled(key);
        } else {
            Identifier dependency = DEPENDENT_FEATURES.get(key);
            if (dependency != null) return isEnabled(dependency);
        }
        return true;
    }

    /**
     * Refresh item visibility in all places when the list of enabled features has changed
     */
    static void refreshItemVisibility() {
        Platform.Environment.CLIENT.runIfCurrent(() -> () ->
                LogicalSidedProvider.WORKQUEUE.get(Platform.Environment.CLIENT).submit(() -> {
                    CreativeModeTab.ItemDisplayParameters cachedParameters = CreativeModeTabsAccessor.getCACHED_PARAMETERS();
                    if (cachedParameters != null) {
                        CreativeModeTabsAccessor.callBuildAllTabContents(cachedParameters);
                    }
                    Mods.JEI.executeIfInstalled(() -> com.copycatsplus.copycats.compat.recipe_viewers.CopycatsJEI::refreshItemList);
                })
        );
    }
}
