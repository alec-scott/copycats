package com.copycatsplus.copycats.content.copycat.cogwheel;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.MaterialItemStorage;
import com.zurrtum.create.content.contraptions.StructureTransform;
import com.zurrtum.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.zurrtum.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CopycatCogWheelBlockEntity extends BracketedKineticBlockEntity implements IMultiStateCopycatBlockEntity {
    private MaterialItemStorage storage;

    public CopycatCogWheelBlockEntity(BlockPos pos, BlockState state) {
        // PORT-NOTE(26.1): the type comes through BETypeOverride (Create Fly hardwires its own)
        super(pos, state);
        init();
    }

    @Override
    public MaterialItemStorage getMaterialItemStorage() {
        return storage;
    }

    @Override
    public void setMaterialItemStorageInternal(MaterialItemStorage storage) {
        this.storage = storage;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state) {
        return IMultiStateCopycatBlockEntity.super.getRequiredItems(state).union(super.getRequiredItems(state));
    }

    @Override
    public void transform(BlockEntity blockEntity, StructureTransform transform) {
        super.transform(blockEntity, transform);
        IMultiStateCopycatBlockEntity.super.transform(blockEntity, transform);
    }

    @Override
    public void read(ValueInput tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        IMultiStateCopycatBlockEntity.read(this, tag, clientPacket);
    }

    @Override
    public void writeSafe(ValueOutput tag) {
        super.writeSafe(tag);
        IMultiStateCopycatBlockEntity.writeSafe(this, tag);
    }

    @Override
    public void write(ValueOutput tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        IMultiStateCopycatBlockEntity.write(this, tag, clientPacket);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        super.preRemoveSideEffects(pos, oldState);
        IMultiStateCopycatBlockEntity.dropConsumedItems(this, pos);
    }
}
