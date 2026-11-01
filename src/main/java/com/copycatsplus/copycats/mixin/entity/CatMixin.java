package com.copycatsplus.copycats.mixin.entity;

import com.copycatsplus.copycats.CCBlocks;
import com.copycatsplus.copycats.CCCatVariants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.animal.feline.CatVariants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Add a copycat variant to cats.
 */
@Mixin(Cat.class)
public abstract class CatMixin extends TamableAnimal {
    @Unique
    private static final TagKey<Item> WRENCHES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "tools/wrench"));

    @Shadow
    public abstract Holder<CatVariant> getVariant();

    @Shadow
    private void setVariant(Holder<CatVariant> variant) {
    }

    protected CatMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Unique
    private static final EntityDataAccessor<Holder<CatVariant>> DATA_NATURAL_VARIANT_ID = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.CAT_VARIANT);

    @Unique
    public Holder<CatVariant> getNaturalVariant() {
        return this.entityData.get(DATA_NATURAL_VARIANT_ID);
    }

    @Unique
    public void setNaturalVariant(Holder<CatVariant> pVariant) {
        this.entityData.set(DATA_NATURAL_VARIANT_ID, pVariant);
    }

    @Unique
    private Holder<CatVariant> copycats$copyCat() {
        return this.registryAccess().lookupOrThrow(Registries.CAT_VARIANT).getOrThrow(CCCatVariants.COPY_CAT);
    }

    @Inject(at = @At("RETURN"), method = "defineSynchedData")
    private void defineNaturalVariant(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DATA_NATURAL_VARIANT_ID, VariantUtils.getDefaultOrAny(this.registryAccess(), CatVariants.BLACK));
    }

    @Inject(at = @At("RETURN"), method = "addAdditionalSaveData")
    private void addNaturalVariantData(ValueOutput output, CallbackInfo ci) {
        this.getNaturalVariant().unwrapKey().ifPresent(key -> output.store("NaturalVariant", Identifier.CODEC, key.identifier()));
    }

    @Inject(at = @At("RETURN"), method = "readAdditionalSaveData")
    private void readNaturalVariantData(ValueInput input, CallbackInfo ci) {
        input.read("NaturalVariant", Identifier.CODEC)
                .flatMap(id -> this.registryAccess().lookupOrThrow(Registries.CAT_VARIANT).get(net.minecraft.resources.ResourceKey.create(Registries.CAT_VARIANT, id)))
                .ifPresent(this::setNaturalVariant);
    }

    @Inject(at = @At("HEAD"), method = "usePlayerItem")
    private void useCopycat(Player pPlayer, InteractionHand pHand, ItemStack pStack, CallbackInfo ci) {
        if (pStack.is(CCBlocks.COPYCAT_BLOCK.asItem())) {
            this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 75f, .95f);
        }
    }

    @Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
    private void copycatInteract(Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (stack.is(CCBlocks.COPYCAT_BLOCK.asItem())) {
            Holder<CatVariant> currentVariant = getVariant();
            if (currentVariant.is(CCCatVariants.COPY_CAT)) return;

            if (!level().isClientSide()) {
                this.setNaturalVariant(currentVariant);
                this.setVariant(copycats$copyCat());
                this.usePlayerItem(pPlayer, pHand, stack);
                this.setPersistenceRequired();
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else if (stack.is(WRENCHES)) {
            Holder<CatVariant> currentVariant = getVariant();
            if (!currentVariant.is(CCCatVariants.COPY_CAT)) return;

            if (level() instanceof ServerLevel serverLevel) {
                this.setVariant(this.getNaturalVariant());
                this.setPersistenceRequired();
                this.spawnAtLocation(serverLevel, CCBlocks.COPYCAT_BLOCK.asItem());
                SoundType soundType = CCBlocks.COPYCAT_BLOCK.getDefaultState().getSoundType();
                this.playSound(soundType.getBreakSound(), (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
