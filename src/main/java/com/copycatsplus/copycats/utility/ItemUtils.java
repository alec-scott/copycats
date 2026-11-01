package com.copycatsplus.copycats.utility;

import com.zurrtum.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemUtils {

    /**
     * Platform-independent implementation of copying an ItemStack with a different size.
     */
    @NotNull
    public static ItemStack copyStackWithSize(ItemStack itemStack, int size) {
        return com.copycatsplus.copycats.utility.fabric.ItemUtilsImpl.copyStackWithSize(itemStack, size);
    }
}
