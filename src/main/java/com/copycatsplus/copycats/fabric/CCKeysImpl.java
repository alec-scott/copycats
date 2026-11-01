package com.copycatsplus.copycats.fabric;

import com.copycatsplus.copycats.CCKeys;
import com.copycatsplus.copycats.Copycats;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class CCKeysImpl {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Copycats.asResource("keys"));

    public static void register() {
        for (CCKeys key : CCKeys.values()) {
            key.keybind = new KeyMapping(key.description, key.key, CATEGORY);
            if (!key.modifiable)
                continue;
            KeyMappingHelper.registerKeyMapping(key.keybind);
        }
    }
}
