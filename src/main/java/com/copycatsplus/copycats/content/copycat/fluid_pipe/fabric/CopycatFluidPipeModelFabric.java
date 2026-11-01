package com.copycatsplus.copycats.content.copycat.fluid_pipe.fabric;

import com.copycatsplus.copycats.content.copycat.fluid_pipe.CopycatFluidPipeModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.fabric.CopycatModelFabric;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.zurrtum.create.content.fluids.FluidTransportBehaviour;
import com.zurrtum.create.content.fluids.pipes.FluidPipeBlock;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

// PORT-NOTE(26.1): the pipe data is gathered from the world while meshing, instead of through NeoForge model data
public class CopycatFluidPipeModelFabric extends CopycatModelFabric {

    public CopycatFluidPipeModelFabric(BlockState state, UnbakedRoot unbaked, CopycatModelCore core, boolean disableAO) {
        super(state, unbaked, core, disableAO);
    }

    @Override
    protected void prepareModelCore(BlockAndTintGetter world, BlockPos pos, BlockState state) {
        super.prepareModelCore(world, pos, state);
        if (!(core instanceof CopycatModelCore.WithData<?>))
            return;
        CopycatFluidPipeModelCore.PipeModelData data = new CopycatFluidPipeModelCore.PipeModelData();
        FluidTransportBehaviour transport = BlockEntityBehaviour.get(world, pos, FluidTransportBehaviour.TYPE);
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(world, pos, BracketedBlockEntityBehaviour.TYPE);

        if (transport != null)
            for (Direction d : Iterate.directions)
                data.putAttachment(d, transport.getRenderedRimAttachment(world, pos, state, d));
        if (bracket != null)
            data.putBracket(bracket.getBracket());

        data.setEncased(FluidPipeBlock.shouldDrawCasing(world, pos, state));

        @SuppressWarnings("unchecked")
        CopycatModelCore.WithData<CopycatFluidPipeModelCore.PipeModelData> dataCore = (CopycatModelCore.WithData<CopycatFluidPipeModelCore.PipeModelData>) core;
        dataCore.setData(data);
    }
}
