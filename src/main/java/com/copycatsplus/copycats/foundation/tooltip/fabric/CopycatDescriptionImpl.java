package com.copycatsplus.copycats.foundation.tooltip.fabric;

import com.copycatsplus.copycats.foundation.tooltip.CopycatDescription;
import com.zurrtum.create.client.foundation.item.TooltipModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CopycatDescriptionImpl extends CopycatDescription implements TooltipModifier {
    protected CopycatDescriptionImpl(Item item) {
        super(item);
    }

    @Override
    public void modify(List<Component> tooltip, Player player) {
        super.modify(item, tooltip);
    }

    @NotNull
    public static TooltipModifier create(ItemLike item) {
        return new CopycatDescriptionImpl(item.asItem());
    }
}
