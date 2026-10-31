package com.copycatsplus.copycats;

import com.copycatsplus.copycats.config.FeatureToggle;
import com.copycatsplus.copycats.content.copycat.board.CopycatBoxItem;
import com.copycatsplus.copycats.content.copycat.board.CopycatCatwalkItem;
import com.copycatsplus.copycats.foundation.tooltip.CopycatCharacteristics;
import com.copycatsplus.copycats.foundation.tooltip.CopycatDescription;
import com.copycatsplus.copycats.port.create.foundation.data.AssetLookup;
import com.copycatsplus.copycats.port.create.foundation.data.CreateRegistrate;
import com.copycatsplus.copycats.port.registrate.util.entry.ItemEntry;

public class CCItems {

    private static final CreateRegistrate REGISTRATE = Copycats.getRegistrate();


    public static final ItemEntry<CopycatBoxItem> COPYCAT_BOX =
            REGISTRATE.item("copycat_box", CopycatBoxItem::new)
                    .onRegister(CopycatDescription.register(
                            CopycatCharacteristics.COPYCAT,
                            CopycatCharacteristics.CT_TOGGLE,
                            CopycatCharacteristics.MULTI_STATE,
                            CopycatCharacteristics.PRE_ASSEMBLED
                    ))
                    .transform(FeatureToggle.registerDependent(CCBlocks.COPYCAT_BOARD))
                    .register();

    public static final ItemEntry<CopycatCatwalkItem> COPYCAT_CATWALK =
            REGISTRATE.item("copycat_catwalk", CopycatCatwalkItem::new)
                    .onRegister(CopycatDescription.register(
                            CopycatCharacteristics.COPYCAT,
                            CopycatCharacteristics.CT_TOGGLE,
                            CopycatCharacteristics.MULTI_STATE,
                            CopycatCharacteristics.PRE_ASSEMBLED
                    ))
                    .transform(FeatureToggle.registerDependent(CCBlocks.COPYCAT_BOARD))
                    .register();

    public static void register() {
    }
}
