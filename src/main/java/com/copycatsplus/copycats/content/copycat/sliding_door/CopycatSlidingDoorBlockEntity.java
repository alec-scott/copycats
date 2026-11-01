package com.copycatsplus.copycats.content.copycat.sliding_door;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.zurrtum.create.content.contraptions.StructureTransform;
import com.zurrtum.create.content.decoration.slidingDoor.SlidingDoorBlockEntity;
import com.zurrtum.create.content.schematics.requirement.ItemRequirement;
import com.zurrtum.create.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CopycatSlidingDoorBlockEntity extends SlidingDoorBlockEntity implements ICopycatBlockEntity {

    protected BlockState material;
    protected ItemStack consumedItem;
    protected boolean enableCT;

    public CopycatSlidingDoorBlockEntity(BlockPos pos, BlockState state) {
        // PORT-NOTE(26.1): the type comes through BETypeOverride (Create Fly hardwires its own)
        super(pos, state);
        ICopycatBlockEntity.super.init();
    }

    public LerpedFloat animation() {
        return animation; // PORT-NOTE(26.1): public in Create Fly
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
    public void transform(BlockEntity blockEntity, StructureTransform transform) {
        ICopycatBlockEntity.super.transform(blockEntity, transform);
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
