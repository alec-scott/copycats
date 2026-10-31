package com.copycatsplus.copycats.datagen;

import com.copycatsplus.copycats.foundation.copycat.CopycatBaseBlock;
import com.copycatsplus.copycats.content.copycat.fluid_pipe.CopycatGlassFluidPipeBlock;
import com.copycatsplus.copycats.port.registrate.providers.DataGenContext;
import com.copycatsplus.copycats.port.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;

public class CCBlockStateGen {

    public static void glassPipe(DataGenContext<Block, CopycatGlassFluidPipeBlock> c, RegistrateBlockstateProvider p) {
    com.copycatsplus.copycats.datagen.fabric.CCBlockStateGenImpl.glassPipe(c, p);}

    public static void base(DataGenContext<Block, CopycatBaseBlock> c, RegistrateBlockstateProvider p) {
    com.copycatsplus.copycats.datagen.fabric.CCBlockStateGenImpl.base(c, p);}
}
