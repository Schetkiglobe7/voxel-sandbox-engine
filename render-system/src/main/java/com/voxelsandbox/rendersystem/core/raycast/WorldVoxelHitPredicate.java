package com.voxelsandbox.rendersystem.core.raycast;

import com.voxelsandbox.rendersystem.core.world.IVoxelWorldView;

import java.util.Objects;


/**
 * {@link VoxelHitPredicate} backed by a voxel world view.
 *
 * <p>
 *     Delegates hit testing to an {@link IVoxelWorldView}
 *     instance.
 * </p>
 *
 * <p>
 *     This class acts as an adapter between the ray traversal
 *     system and the voxel world representation.
 * </p>
 */
public final class WorldVoxelHitPredicate implements VoxelHitPredicate {

    private final IVoxelWorldView world;

    /**
     * Creates a world-backed hit predicate.
     *
     * @param world read-only voxel world view
     */
    public WorldVoxelHitPredicate(IVoxelWorldView world) {
        this.world = Objects.requireNonNull(world, "world must not be null");
    }

    @Override
    public boolean isHit(int x, int y, int z) {
        return world.isSolid(x, y, z);
    }
}