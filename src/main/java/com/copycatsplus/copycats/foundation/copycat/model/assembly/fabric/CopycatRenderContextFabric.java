package com.copycatsplus.copycats.foundation.copycat.model.assembly.fabric;

import com.copycatsplus.copycats.foundation.copycat.model.assembly.*;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadAutoCull;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadTransform;
import com.zurrtum.create.client.foundation.model.BakedModelHelper;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * PORT-NOTE(26.1): the NeoForge render context worked on int[] vertex data; 26.1's {@link BakedQuad} is an immutable
 * record (positions, packed UVs, light face, material info), so assembled quads are rebuilt from a
 * {@link MutableQuad}. Cropping uses Create Fly's {@link BakedModelHelper#cropAndMove}.
 */
@ApiStatus.Internal
@ParametersAreNonnullByDefault
public class CopycatRenderContextFabric extends CopycatRenderContext.Base<List<CopycatRenderContextFabric.CopycatQuad>, List<CopycatRenderContextFabric.CopycatQuad>> {

    public CopycatRenderContextFabric(List<CopycatQuad> source, List<CopycatQuad> destination, String property) {
        super(source, destination, property);
    }

    @Override
    public void assemblePiece(AssemblyTransform assemblyTransform, MutableVec3 offset, MutableAABB select, MutableCullFace cull) {
        assemblyTransform.apply(select);
        assemblyTransform.apply(offset);
        assemblyTransform.apply(cull);
        AABB aabb = select.toAABB();
        Vec3 vec3 = offset.toVec3().subtract(select.minX, select.minY, select.minZ);
        for (CopycatQuad quad : source()) {
            if (cull.isCulled(quad.quad().direction()))
                continue;
            assembleQuad(quad, destination(), aabb, vec3, assemblyTransform);
        }
    }

    @Override
    public void assemblePiece(AssemblyTransform assemblyTransform, MutableVec3 offset, MutableAABB select, MutableCullFace cull, QuadTransform... transforms) {
        assemblyTransform.apply(select);
        assemblyTransform.apply(offset);
        assemblyTransform.apply(cull);
        AABB aabb = select.toAABB();
        Vec3 vec3 = offset.toVec3().subtract(select.minX, select.minY, select.minZ);
        for (CopycatQuad quad : source()) {
            if (cull.isCulled(quad.quad().direction()))
                continue;
            assembleQuad(quad, destination(), aabb, vec3, assemblyTransform, transforms);
        }
    }

    @Override
    public void assembleAll() {
        destination().addAll(source());
    }

    @Override
    public void assembleRaw(AABB crop, Vec3 move) {
        for (CopycatQuad quad : source())
            assembleQuad(quad, destination(), crop, move, AssemblyTransform.IDENTITY);
    }

    @Override
    public void assembleRaw(AABB crop, Vec3 move, QuadTransform... transforms) {
        for (CopycatQuad quad : source())
            assembleQuad(quad, destination(), crop, move, AssemblyTransform.IDENTITY, transforms);
    }

    private static void assembleQuad(CopycatQuad src, List<CopycatQuad> dest, AABB crop, Vec3 move, AssemblyTransform assemblyTransform, QuadTransform... transforms) {
        BakedQuad cropped = BakedModelHelper.cropAndMove(src.quad(), crop, move);
        MutableQuad mutableQuad = getMutableQuad(cropped, src.cullFace());
        assemblyTransform.apply(mutableQuad);
        mutableQuad.undoMutate();
        for (QuadTransform transform : transforms) {
            if (!transform.transformQuad(mutableQuad, src.quad().materialInfo().sprite()))
                return;
        }
        if (!mutableQuad.disableFinalAutoCull)
            if (!QuadAutoCull.BLOCK.transformQuad(mutableQuad, src.quad().materialInfo().sprite()))
                return;
        mutableQuad.mutate();
        BakedQuad result = new BakedQuad(
                mutableQuad.vertices.get(0).xyz.toVec3().toVector3f(),
                mutableQuad.vertices.get(1).xyz.toVec3().toVector3f(),
                mutableQuad.vertices.get(2).xyz.toVec3().toVector3f(),
                mutableQuad.vertices.get(3).xyz.toVec3().toVector3f(),
                packUV(mutableQuad.vertices.get(0)),
                packUV(mutableQuad.vertices.get(1)),
                packUV(mutableQuad.vertices.get(2)),
                packUV(mutableQuad.vertices.get(3)),
                mutableQuad.computeLightFace(),
                cropped.materialInfo()
        );
        dest.add(new CopycatQuad(result, mutableQuad.cullFace, src.property()));
    }

    private static long packUV(MutableVertex vertex) {
        return UVPair.pack(vertex.uv.u, vertex.uv.v);
    }

    public static MutableQuad getMutableQuad(BakedQuad quad, @Nullable Direction cullFace) {
        List<MutableVertex> vertices = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            MutableVec3 xyz = new MutableVec3(new Vec3(quad.position(i)));
            long uv = quad.packedUV(i);
            vertices.add(new MutableVertex(xyz, new MutableUV(UVPair.unpackU(uv), UVPair.unpackV(uv))));
        }
        return new MutableQuad(vertices, cullFace);
    }

    /**
     * A quad of the source or assembled model, with the face it is culled against and the multi-state part it belongs to.
     */
    public record CopycatQuad(BakedQuad quad, @Nullable Direction cullFace, String property) {
    }
}
