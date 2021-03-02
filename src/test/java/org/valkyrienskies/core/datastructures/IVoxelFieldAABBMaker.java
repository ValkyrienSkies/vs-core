package org.valkyrienskies.core.datastructures;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3ic;
import org.joml.primitives.AABBd;

/**
 * This class enables the updating of ship AABBs in O(1) time
 */
public interface IVoxelFieldAABBMaker {

	int MIN_X = -512;
	int MAX_X = 511;
	int MIN_Y = 0;
	int MAX_Y = 255;
	int MIN_Z = -512;
	int MAX_Z = 511;

	/**
	 * Creates an AABB in world space.
	 *
	 * @return Null if there are no voxels in the voxel field.
	 */
	@Nullable
	AABBd makeVoxelFieldAABB();

	/**
	 * Adds a voxel to the field
	 *
	 * @param x Must be between [getFieldCenter().getX() - 512, getFieldCenter().getX() + 511].
	 * @param y Must be between [0, 255].
	 * @param z Must be between [getFieldCenter().getZ() - 512, getFieldCenter().getZ() + 511].
	 * @return True if a voxel didn't already exist at xyz, false otherwise.
	 */
	boolean addVoxel(int x, int y, int z) throws IllegalArgumentException;

	/**
	 * Removes a voxel from the field
	 *
	 * @param x Must be between [getFieldCenter().getX() - 512, getFieldCenter().getX() + 511].
	 * @param y Must be between [0, 255].
	 * @param z Must be between [getFieldCenter().getZ() - 512, getFieldCenter().getZ() + 511].
	 * @return True if a voxel existed at xyz, false otherwise.
	 */
	boolean removeVoxel(int x, int y, int z) throws IllegalArgumentException;

	/**
	 * @return The BlockPos at the center of this voxel field.
	 */
	@Nonnull
	Vector3ic getFieldCenter();

	// region OVERLOADS

	default boolean addVoxel(Vector3ic vec) {
		return this.addVoxel(vec.x(), vec.y(), vec.z());
	}

	default boolean removeVoxel(Vector3ic vec) {
		return this.removeVoxel(vec.x(), vec.y(), vec.z());
	}

	void clear();

	int size();
	// endregion
}
