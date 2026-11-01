package com.copycatsplus.copycats.foundation.copycat.multistate;

import com.copycatsplus.copycats.utility.ItemUtils;
import com.zurrtum.create.AllBlocks;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Stores copycat data, including material, consumed item and CT toggle, for each part of a multi-state copycat.
 */
public class MaterialItemStorage {

    private Map<String, MaterialItem> storage;

    private MaterialItemStorage(Set<String> properties) {
        storage = new HashMap<>(properties.size());
        for (String property : properties) {
            storage.put(property, new MaterialItem(AllBlocks.COPYCAT_BASE.defaultBlockState(), ItemStack.EMPTY));
        }
    }

    public static MaterialItemStorage create(Set<String> properties) {
        return new MaterialItemStorage(properties);
    }

    /**
     * Replace the material item for a specific property.
     */
    public void storeMaterialItem(String property, MaterialItem materialItem) {
        storage.put(property, materialItem);
    }

    /**
     * Get the material item for a specific property.
     */
    public @Nullable MaterialItem getMaterialItem(String property) {
        return storage.get(property);
    }

    /**
     * Get all properties stored in this storage, regardless of whether they have a custom material.
     */
    public Set<String> getAllProperties() {
        return storage.keySet();
    }

    /**
     * Get all materials stored in this storage, regardless of whether they have a custom material or whether they exist according to the copycat's block state.
     */
    public Set<BlockState> getAllMaterials() {
        return storage.values().stream().map(MaterialItem::material).collect(Collectors.toSet());
    }

    /**
     * Get all material items stored in this storage, regardless of whether they have a custom material or whether they exist according to the copycat's block state.
     */
    public Set<MaterialItem> getAllMaterialItems() {
        return new HashSet<>(storage.values());
    }

    /**
     * Get all consumed items stored in this storage. Empty stacks are not included.
     */
    public List<ItemStack> getAllConsumedItems() {
        return storage.values().stream().map(MaterialItem::consumedItem).filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
    }

    /**
     * Get a map of all properties and their corresponding materials stored in this storage.
     */
    public Map<String, BlockState> getMaterialMap() {
        return storage.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, s -> s.getValue().material));
    }

    /**
     * Check if a specific property has a custom material.
     */
    public boolean hasCustomMaterial(String property) {
        return storage.get(property).hasCustomMaterial();
    }

    /**
     * Modify the keys of all stored materials using the provided key mapper.
     *
     * @param keyMapper A function which accepts a key and returns a new key where the corresponding material should be stored.
     */
    public void remapStorage(Function<String, String> keyMapper) {
        Map<String, MaterialItem> newStorage = new HashMap<>();
        storage.forEach((key, materialItem) -> newStorage.put(keyMapper.apply(key), materialItem));
        storage = newStorage;
    }

    private static final Codec<Map<String, MaterialItem>> STORAGE_CODEC = Codec.unboundedMap(Codec.STRING, MaterialItem.CODEC);

    public void write(ValueOutput view, String key) {
        view.store(key, STORAGE_CODEC, storage);
    }

    public void writeSafe(ValueOutput view, String key) {
        Map<String, MaterialItem> safe = new HashMap<>();
        storage.forEach((k, materialItem) -> safe.put(k, materialItem.withoutComponents()));
        view.store(key, STORAGE_CODEC, safe);
    }

    public boolean read(ValueInput view, String key) {
        AtomicBoolean anyUpdated = new AtomicBoolean(false);
        view.read(key, STORAGE_CODEC).orElse(Map.of()).forEach((k, newVersion) -> {
            MaterialItem oldVersion = storage.put(k, newVersion);
            if (oldVersion != null &&
                    (newVersion.material() != oldVersion.material() || newVersion.enableCT() != oldVersion.enableCT()) &&
                    !anyUpdated.get()) {
                anyUpdated.set(true);
            }
        });
        return anyUpdated.get();
    }

    public void clear() {
        storage.clear();
    }

    /**
     * Stores copycat data for a single part of a multi-state copycat.
     */
    public static class MaterialItem {

        private BlockState material;
        private ItemStack consumedItem;
        private boolean enableCT;

        public MaterialItem(BlockState material, ItemStack consumedItem) {
            this(material, consumedItem, true);
        }

        public MaterialItem(BlockState material, ItemStack consumedItem, boolean enableCT) {
            this.material = material;
            this.consumedItem = consumedItem;
            this.enableCT = enableCT;
        }

        // PORT-NOTE(26.1): NBT layout kept (material, consumedItem, enableCT) so existing copycats load
        public static final Codec<MaterialItem> CODEC = RecordCodecBuilder.create(i -> i.group(
                BlockState.CODEC.fieldOf("material").forGetter(MaterialItem::material),
                ItemStack.OPTIONAL_CODEC.optionalFieldOf("consumedItem", ItemStack.EMPTY).forGetter(MaterialItem::consumedItem),
                Codec.BOOL.optionalFieldOf("enableCT", true).forGetter(MaterialItem::enableCT)
        ).apply(i, MaterialItem::new));

        public MaterialItem withoutComponents() {
            return new MaterialItem(material, new ItemStack(consumedItem.typeHolder(), consumedItem.getCount(), DataComponentPatch.EMPTY), enableCT);
        }

        public BlockState material() {
            return material;
        }

        public ItemStack consumedItem() {
            return consumedItem;
        }

        public boolean enableCT() {
            return enableCT;
        }

        public void setMaterial(BlockState material) {
            this.material = material;
        }

        public void setConsumedItem(ItemStack stack) {
            consumedItem = ItemUtils.copyStackWithSize(stack, 1);
        }

        public void setEnableCT(boolean enableCT) {
            this.enableCT = enableCT;
        }

        public boolean hasCustomMaterial() {
            return !material.is(AllBlocks.COPYCAT_BASE);
        }
    }

}
