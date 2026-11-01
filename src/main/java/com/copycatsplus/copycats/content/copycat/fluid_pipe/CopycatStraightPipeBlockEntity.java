package com.copycatsplus.copycats.content.copycat.fluid_pipe;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.zurrtum.create.content.fluids.pipes.StraightPipeBlockEntity;
import com.zurrtum.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


public class CopycatStraightPipeBlockEntity extends StraightPipeBlockEntity implements ICopycatBlockEntity {

    protected BlockState material;
    protected ItemStack consumedItem;
    protected boolean enableCT;

    public CopycatStraightPipeBlockEntity(BlockPos pos, BlockState state) {
        // PORT-NOTE(26.1): the type comes through BETypeOverride (Create Fly hardwires its own)
        super(pos, state);
        ICopycatBlockEntity.super.init();
    }

    @Override
    public BlockState getMaterial() {
        return material;
    }

    @Override
    public ItemStack getConsumedItem() {
        return consumedItem;
    }

    @Override
    public boolean isCTEnabled() {
        return enableCT;
    }

    @Override
    public void setMaterialInternal(BlockState material) {
        this.material = material;
    }

    @Override
    public void setConsumedItemInternal(ItemStack consumedItem) {
        this.consumedItem = consumedItem;
    }

    @Override
    public void setCTEnabledInternal(boolean value) {
        enableCT = value;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state) {
        return ICopycatBlockEntity.super.getRequiredItems(state).union(super.getRequiredItems(state));
    }

    @Override
    public void read(ValueInput tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        ICopycatBlockEntity.read(this, tag, clientPacket);
    }

    @Override
    public void writeSafe(ValueOutput tag) {
        super.writeSafe(tag);
        ICopycatBlockEntity.writeSafe(this, tag);
    }

    @Override
    public void write(ValueOutput tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        ICopycatBlockEntity.write(this, tag, clientPacket);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        super.preRemoveSideEffects(pos, oldState);
        ICopycatBlockEntity.dropConsumedItems(this, pos);
    }
}
