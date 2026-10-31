package com.copycatsplus.copycats.utility;

import com.zurrtum.create.client.foundation.item.TooltipModifier;
import org.jetbrains.annotations.NotNull;

public class TooltipUtils {
    @NotNull
    public static TooltipModifier sequential(TooltipModifier... modifiers) {
        return com.copycatsplus.copycats.utility.fabric.TooltipUtilsImpl.sequential(modifiers);
    }
}