package com.copycatsplus.copycats.foundation.copycat.model.fabric;

import com.copycatsplus.copycats.CCBlocks;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.client.CopycatTints;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.FilteredBlockAndTintGetter;
import com.copycatsplus.copycats.foundation.copycat.model.ScaledBlockAndTintGetter;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.fabric.CopycatRenderContextFabric;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.fabric.CopycatRenderContextFabric.CopycatQuad;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.MaterialItemStorage;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.catnip.levelWrappers.SchematicLevel;
import com.zurrtum.create.client.flywheel.lib.model.baked.VirtualBlockGetter;
import com.zurrtum.create.client.foundation.virtualWorld.VirtualRenderWorld;
import com.zurrtum.create.client.infrastructure.model.WrapperBlockStateModel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.copycatsplus.copycats.CCBlockStateProperties.BASE_TYPE;
import static com.copycatsplus.copycats.foundation.copycat.CopycatBaseBlock.BASE_TYPE_COUNT;
import static com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore.MATERIAL_KEY;
import static com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore.getModelOf;

/**
 * PORT-NOTE(26.1): replaces the NeoForge {@code CopycatModelNeoForge} (a baked model wrapper fed by model data).
 * 26.1 block models are {@link BlockStateModel}s that emit {@link BlockStateModelPart}s; Create Fly's
 * {@link WrapperBlockStateModel} gets the world and position while the chunk is meshed, so the materials are read
 * from the block entity here instead of from model data. Each model entry's material model is collected (CT models
 * see the filtered or scaled world), assembled by the core and emitted as one part per source part.
 */
public class CopycatModelFabric extends WrapperBlockStateModel {

    protected final CopycatModelCore core;
    private final boolean disableAO;
    protected final List<CopycatModelCore.ModelEntry> entries = new ArrayList<>();

    public CopycatModelFabric(BlockState state, UnbakedRoot unbaked, CopycatModelCore core, boolean disableAO) {
        super(state, unbaked);
        this.core = core;
        this.disableAO = disableAO;
        core.registerModels(entries);
    }

    /**
     * A model around an already baked model, for kinetic partial models assembled at runtime.
     */
    public CopycatModelFabric(BlockStateModel baked, CopycatModelCore core, boolean disableAO) {
        this.model = baked;
        this.core = core;
        this.disableAO = disableAO;
        core.registerModels(entries);
    }

    public static boolean isVirtual(BlockAndTintGetter world) {
        // WrappedRenderWorld (kinetic copycats) is a VirtualRenderWorld
        return world instanceof VirtualRenderWorld || world instanceof VirtualBlockGetter || world instanceof SchematicLevel;
    }

    /**
     * The copied materials by model key, read from the block entity.
     */
    public static Map<String, BlockState> getMaterials(BlockAndTintGetter world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof IMultiStateCopycatBlockEntity multiStateBE) {
            return new HashMap<>(multiStateBE.getMaterialItemStorage().getMaterialMap());
        } else if (be instanceof ICopycatBlockEntity copycatBE) {
            return Map.of(MATERIAL_KEY, copycatBE.getMaterial());
        }
        return Map.of();
    }

    @Override
    public void addPartsWithInfo(BlockAndTintGetter world, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        prepareModelCore(world, pos, state);

        Map<String, BlockState> materials = getMaterials(world, pos);
        final boolean isVirtual = isVirtual(world);

        for (CopycatModelCore.ModelEntry entry : entries) {
            BlockState material = entry.materialMapper().map(state, materials.get(entry.key()));

            if (entry.type().onlyWhenVirtual() && !isVirtual)
                continue;
            if (entry.type().useCopycatLogic() && material == null) {
                // Don't skip rendering if the world is empty since we might be rendering a placement helper
                if (materials.isEmpty() && isVirtual) {
                    material = AllBlocks.COPYCAT_BASE.defaultBlockState();
                } else continue;
            }

            BlockStateModel model = getModelForEntry(entry, state, material);
            if (model == null) continue;

            if (!entry.type().useCopycatLogic()) {
                List<BlockStateModelPart> sourceParts = new ArrayList<>();
                addModelParts(world, pos, state, random, model, sourceParts);
                for (BlockStateModelPart sourcePart : sourceParts)
                    emitEntry(entry, state, material, sourcePart, null, false, null, parts);
                continue;
            }

            OcclusionData occlusionData = new OcclusionData();
            BlockAndTintGetter renderWorld = world;
            IMultiStateCopycatBlock multiStateBlock = null;
            if (state.getBlock() instanceof IMultiStateCopycatBlock multiState) {
                multiStateBlock = multiState;
                Vec3i inner = multiState.getVectorFromProperty(state, entry.key());
                boolean enableCT = isCTEnabled(world, pos, entry.key());
                ScaledBlockAndTintGetter scaledWorld = new ScaledBlockAndTintGetter(entry.key(), world, pos, inner, multiState.vectorScale(state), p -> true);
                if (!isVirtual)
                    gatherOcclusionData(scaledWorld, pos, state, occlusionData);
                renderWorld = new ScaledBlockAndTintGetter(entry.key(), world, pos, inner, multiState.vectorScale(state),
                        targetPos -> enableCT && multiState.canConnectTexturesToward(entry.key(), scaledWorld, pos, targetPos, state));
            } else if (state.getBlock() instanceof ICopycatBlock copycatBlock) {
                if (!isVirtual)
                    gatherOcclusionData(world, pos, state, occlusionData);
                renderWorld = new FilteredBlockAndTintGetter(world, targetPos -> {
                    if (world.getBlockEntity(pos) instanceof ICopycatBlockEntity copycatBE && !copycatBE.isCTEnabled())
                        return false;
                    return copycatBlock.canConnectTexturesToward(world, pos, targetPos, state);
                });
            }

            // provide the original material to the model instead of the mapped material so that CT works and treats
            // the mapped material as the unmapped one
            BlockState originalMaterial = materials.get(entry.key());
            if (originalMaterial == null)
                originalMaterial = AllBlocks.COPYCAT_BASE.defaultBlockState();

            List<BlockStateModelPart> sourceParts = new ArrayList<>();
            addModelParts(renderWorld, pos, originalMaterial, random, model, sourceParts);
            for (BlockStateModelPart sourcePart : sourceParts)
                emitEntry(entry, state, material, sourcePart, occlusionData, true, multiStateBlock, parts);
        }
    }

    /**
     * Called before rendering, to configure the model core (for example with block entity data).
     */
    protected void prepareModelCore(BlockAndTintGetter world, BlockPos pos, BlockState state) {
        core.prepareForRender();
    }

    private static boolean isCTEnabled(BlockAndTintGetter world, BlockPos pos, String property) {
        if (!(world.getBlockEntity(pos) instanceof IMultiStateCopycatBlockEntity multiStateBE))
            return true;
        MaterialItemStorage.MaterialItem item = multiStateBE.getMaterialItemStorage().getMaterialItem(property);
        return item == null || item.enableCT();
    }

    private void emitEntry(CopycatModelCore.ModelEntry entry, BlockState state, BlockState material, BlockStateModelPart sourcePart,
                           @Nullable OcclusionData occlusionData, boolean copycatLogic, @Nullable IMultiStateCopycatBlock multiStateBlock,
                           List<BlockStateModelPart> parts) {
        List<CopycatQuad> quads = new ArrayList<>();
        for (Direction side : Iterate.directions) {
            for (BakedQuad quad : sourcePart.getQuads(side))
                quads.add(new CopycatQuad(quad, side, entry.key()));
        }
        for (BakedQuad quad : sourcePart.getQuads(null))
            quads.add(new CopycatQuad(quad, null, entry.key()));

        if (entry.part() != null) {
            List<CopycatQuad> assembled = new ArrayList<>();
            entry.part().emitCopycatQuads(entry.key(), state, new CopycatRenderContextFabric(quads, assembled, entry.key()), material);
            quads = assembled;
        }

        QuadCollection.Builder builder = new QuadCollection.Builder();
        boolean any = false;
        for (CopycatQuad quad : quads) {
            if (occlusionData != null && occlusionData.isOccluded(quad.cullFace()))
                continue;
            BakedQuad bakedQuad = quad.quad();
            if (multiStateBlock != null && bakedQuad.materialInfo().isTinted())
                bakedQuad = withTintIndex(bakedQuad, CopycatTints.multiStateTintIndex(multiStateBlock, entry.key(), bakedQuad.materialInfo().tintIndex()));
            // Copycat quads are already culled against the material's neighbours
            if (copycatLogic || quad.cullFace() == null)
                builder.addUnculledFace(bakedQuad);
            else
                builder.addCulledFace(quad.cullFace(), bakedQuad);
            any = true;
        }
        if (!any)
            return;
        boolean ao = !disableAO && sourcePart.useAmbientOcclusion();
        parts.add(new SimpleModelWrapper(builder.build(), ao, sourcePart.particleMaterial()));
    }

    private static BakedQuad withTintIndex(BakedQuad quad, int tintIndex) {
        BakedQuad.MaterialInfo info = quad.materialInfo();
        if (info.tintIndex() == tintIndex)
            return quad;
        BakedQuad.MaterialInfo newInfo = new BakedQuad.MaterialInfo(info.sprite(), info.layer(), info.itemRenderType(), tintIndex, info.shade(), info.lightEmission());
        return new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(), quad.direction(), newInfo);
    }

    protected void addModelParts(BlockAndTintGetter world, BlockPos pos, BlockState state, RandomSource random, BlockStateModel model, List<BlockStateModelPart> parts) {
        if (model instanceof WrapperBlockStateModel wrapper) {
            wrapper.addPartsWithInfo(world, pos, state, random, parts);
        } else {
            model.collectParts(random, parts);
        }
    }

    private static void gatherOcclusionData(BlockAndTintGetter world, BlockPos pos, BlockState state, OcclusionData occlusionData) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction face : Iterate.directions) {
            BlockState neighbourState = world.getBlockState(mutablePos.setWithOffset(pos, face));
            if (!Block.shouldRenderFace(state, neighbourState, face))
                occlusionData.occlude(face);
        }
    }

    @Nullable
    public BlockStateModel getModelForEntry(CopycatModelCore.ModelEntry entry, BlockState state, @Nullable BlockState material) {
        if (entry.model() == null)
            return model;
        if (core.colorize && state.getBlock() instanceof IMultiStateCopycatBlock multiState && material != null && material.is(AllBlocks.COPYCAT_BASE)) {
            material = CCBlocks.COPYCAT_BASE.getDefaultState().setValue(BASE_TYPE, multiState.getColorIndex(entry.key()) % BASE_TYPE_COUNT);
        }
        return entry.model().getModel(state, material);
    }

    @Override
    public boolean needUpdateTerrainParticle() {
        return true;
    }

    @Override
    public Material.Baked particleMaterialWithInfo(BlockAndTintGetter world, BlockPos pos, BlockState state) {
        Map<String, BlockState> materials = getMaterials(world, pos);
        if (materials.isEmpty())
            return model.particleMaterial();
        BlockState material = materials.values().stream()
                .filter(s -> !s.is(AllBlocks.COPYCAT_BASE))
                .findFirst()
                .orElse(materials.values().iterator().next());
        return getModelOf(material).particleMaterial();
    }

    public static class OcclusionData {
        private final boolean[] occluded = new boolean[6];

        public void occlude(Direction face) {
            occluded[face.get3DDataValue()] = true;
        }

        public boolean isOccluded(@Nullable Direction face) {
            return face != null && occluded[face.get3DDataValue()];
        }
    }
}
