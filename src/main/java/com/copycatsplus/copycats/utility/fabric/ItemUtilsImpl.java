package com.copycatsplus.copycats.utility.fabric;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class ItemUtilsImpl {

    public static ItemStack copyStackWithSize(ItemStack itemStack, int size) {
        return itemStack.copyWithCount(size);
    }
}
