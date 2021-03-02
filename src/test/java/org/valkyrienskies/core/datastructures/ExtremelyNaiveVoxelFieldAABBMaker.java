package org.valkyrienskies.core.datastructures;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.primitives.AABBd;

/**
 * Only used for testing, don't actually use this ever. Its inefficient.
 */
public class ExtremelyNaiveVoxelFieldAABBMaker implements IVoxelFieldAABBMaker {

	private final Set<Vector3ic> blockPosSet;
	private final Vector3ic voxelFieldWorldCenter;

	public ExtremelyNaiveVoxelFieldAABBMaker(int x, int z) {
		this.blockPosSet = new HashSet<>();
		this.voxelFieldWorldCenter = new Vector3i(x, 0, z);
	}

	@Override
	public AABBd makeVoxelFieldAABB() {
		int minX;
		int minY;
		int minZ;
		int maxX;
		int maxY;
		int maxZ;
		minX = minY = minZ = Integer.MAX_VALUE;
		maxX = maxY = maxZ = Integer.MIN_VALUE;
		if (blockPosSet.isEmpty()) {
			return null;
		}

		for (Vector3ic pos : blockPosSet) {
			minX = Math.min(minX, pos.x());
			minY = Math.min(minY, pos.y());
			minZ = Math.min(minZ, pos.z());
			maxX = Math.max(maxX, pos.x());
			maxY = Math.max(maxY, pos.y());
			maxZ = Math.max(maxZ, pos.z());
		}
		return new AABBd(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public boolean addVoxel(int x, int y, int z) {
		assertValidInputs(x - getFieldCenter().x(), y - getFieldCenter().y(),
			z - getFieldCenter().z());
		return blockPosSet.add(new Vector3i(x, y, z));
	}

	@Override
	public boolean removeVoxel(int x, int y, int z) {
		assertValidInputs(x - getFieldCenter().x(), y - getFieldCenter().y(),
			z - getFieldCenter().z());
		return blockPosSet.remove(new Vector3i(x, y, z));
	}

	@Nonnull
	@Override
	public Vector3ic getFieldCenter() {
		return voxelFieldWorldCenter;
	}

	@Override
	public void clear() {
		blockPosSet.clear();
	}

	@Override
	public int size() {
		return blockPosSet.size();
	}

	private void assertValidInputs(int x, int y, int z) throws IllegalArgumentException {
		if (x < MIN_X || x > MAX_X || y < MIN_Y || y > MAX_Y || z < MIN_Z || z > MAX_Z) {
			throw new IllegalArgumentException(
				x + ":" + y + ":" + z + " is out of range from " + getFieldCenter());
		}
	}
}
