package org.valkyrienskies.core.datastructures;

import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.primitives.AABBd;

/**
 * Do not serialize.
 */
public class NaiveVoxelFieldAABBMaker implements IVoxelFieldAABBMaker {

	private final Vector3ic centerPos;
	private final TreeMap<Integer, TreeSet<TwoInts>> xMap;
	private final TreeMap<Integer, TreeSet<TwoInts>> yMap;
	private final TreeMap<Integer, TreeSet<TwoInts>> zMap;
	private Vector3ic minCoords;
	private Vector3ic maxCoords;
	private int voxelCount;

	public NaiveVoxelFieldAABBMaker(int x, int z) {
		this.centerPos = new Vector3i(x, 0, z);
		this.xMap = new TreeMap<>();
		this.yMap = new TreeMap<>();
		this.zMap = new TreeMap<>();
		this.minCoords = null;
		this.maxCoords = null;
		this.voxelCount = 0;
	}

	@Override
	public AABBd makeVoxelFieldAABB() {
		if (voxelCount == 0) {
			return null;
		}
		AABBd inLocal =
			new AABBd(minCoords.x(), minCoords.y(), minCoords.z(), maxCoords.x(), maxCoords.y(), maxCoords.z());
		return inLocal.translate(centerPos.x(), centerPos.y(), centerPos.z());
	}

	@Override
	public boolean addVoxel(int x, int y, int z) {
		// Put xyz into local coordinates.
		x -= centerPos.x();
		y -= centerPos.y();
		z -= centerPos.z();

		assertValidInputs(x, y, z);

		// Update the treemaps.
		boolean isVoxelNew = false;
		if (!xMap.containsKey(x)) {
			xMap.put(x, new TreeSet<>());
		}
		TwoInts yz = new TwoInts(y, z);
		if (xMap.get(x).add(yz)) {
			isVoxelNew = true;
		}
		if (!yMap.containsKey(y)) {
			yMap.put(y, new TreeSet<>());
		}
		TwoInts xz = new TwoInts(x, z);
		if (yMap.get(y).add(xz)) {
			isVoxelNew = true;
		}
		if (!zMap.containsKey(z)) {
			zMap.put(z, new TreeSet<>());
		}
		TwoInts xy = new TwoInts(x, y);
		if (zMap.get(z).add(xy)) {
			isVoxelNew = true;
		}

		if (!isVoxelNew) {
			// Nothing removed, nothing to change.
			return false;
		}
		// A voxel was added, update the voxel count
		voxelCount++;

		// Update max/min coords
		if (minCoords == null || maxCoords == null) {
			minCoords = new Vector3i(x, y, z);
			maxCoords = new Vector3i(x, y, z);
			return true;
		}
		if (x > maxCoords.x() || y > maxCoords.y() || z > maxCoords.z()) {
			maxCoords = new Vector3i(Math.max(x, maxCoords.x()), Math.max(y, maxCoords.y()),
				Math.max(z, maxCoords.z()));
		}
		if (x < minCoords.x() || y < minCoords.y() || z < minCoords.z()) {
			minCoords = new Vector3i(Math.min(x, minCoords.x()), Math.min(y, minCoords.y()),
				Math.min(z, minCoords.z()));
		}
		return true;
	}

	@Override
	public boolean removeVoxel(int x, int y, int z) {
		// Put xyz into local coordinates.
		x -= centerPos.x();
		y -= centerPos.y();
		z -= centerPos.z();

		assertValidInputs(x, y, z);

		// Update the treemaps.
		boolean isVoxelRemoved = false;
		if (!xMap.containsKey(x)) {
			xMap.put(x, new TreeSet<>());
		}
		TwoInts yz = new TwoInts(y, z);
		if (xMap.get(x).remove(yz)) {
			isVoxelRemoved = true;
		}
		if (!yMap.containsKey(y)) {
			yMap.put(y, new TreeSet<>());
		}
		TwoInts xz = new TwoInts(x, z);
		if (yMap.get(y).remove(xz)) {
			isVoxelRemoved = true;
		}
		if (!zMap.containsKey(z)) {
			zMap.put(z, new TreeSet<>());
		}
		TwoInts xy = new TwoInts(x, y);
		if (zMap.get(z).remove(xy)) {
			isVoxelRemoved = true;
		}

		if (!isVoxelRemoved) {
			// Nothing removed, nothing to change.
			return false;
		}
		// A voxel was removed, update the voxel count
		voxelCount--;

		// Update max/min coords
		// Update maxCoords.
		if (x == maxCoords.x() || y == maxCoords.y() || z == maxCoords.z()) {
			int newMaxX = maxCoords.x();
			int newMaxY = maxCoords.y();
			int newMaxZ = maxCoords.z();
			if (x == maxCoords.x()) {
				for (int i = newMaxX; i >= MIN_X; i--) {
					if (xMap.containsKey(i)) {
						if (!xMap.get(i).isEmpty()) {
							newMaxX = i;
							break;
						}
					}
				}
			}
			if (y == maxCoords.y()) {
				for (int i = newMaxY; i >= MIN_Y; i--) {
					if (yMap.containsKey(i)) {
						if (!yMap.get(i).isEmpty()) {
							newMaxY = i;
							break;
						}
					}
				}
			}
			if (z == maxCoords.z()) {
				for (int i = newMaxZ; i >= MIN_Z; i--) {
					if (zMap.containsKey(i)) {
						if (!zMap.get(i).isEmpty()) {
							newMaxZ = i;
							break;
						}
					}
				}
			}
			maxCoords = new Vector3i(newMaxX, newMaxY, newMaxZ);
		}
		// Update minCoords.
		if (x == minCoords.x() || y == minCoords.y() || z == minCoords.z()) {
			int newMinX = minCoords.x();
			int newMinY = minCoords.y();
			int newMinZ = minCoords.z();
			if (x == minCoords.x()) {
				for (int i = newMinX; i <= MAX_X; i++) {
					if (xMap.containsKey(i)) {
						if (!xMap.get(i).isEmpty()) {
							newMinX = i;
							break;
						}
					}
				}
			}
			if (y == minCoords.y()) {
				for (int i = newMinY; i <= MAX_Y; i++) {
					if (yMap.containsKey(i)) {
						if (!yMap.get(i).isEmpty()) {
							newMinY = i;
							break;
						}
					}
				}
			}
			if (z == minCoords.z()) {
				for (int i = newMinZ; i <= MAX_Z; i++) {
					if (zMap.containsKey(i)) {
						if (!zMap.get(i).isEmpty()) {
							newMinZ = i;
							break;
						}
					}
				}
			}
			minCoords = new Vector3i(newMinX, newMinY, newMinZ);
		}

		return true;
	}

	@Nonnull
	@Override
	public Vector3ic getFieldCenter() {
		return centerPos;
	}

	@Override
	public void clear() {
		this.xMap.clear();
		this.yMap.clear();
		this.zMap.clear();
		this.minCoords = null;
		this.maxCoords = null;
		this.voxelCount = 0;
	}

	@Override
	public int size() {
		return voxelCount;
	}

	private void assertValidInputs(int x, int y, int z) throws IllegalArgumentException {
		if (x < MIN_X || x > MAX_X || y < MIN_Y || y > MAX_Y || z < MIN_Z || z > MAX_Z) {
			throw new IllegalArgumentException(
				x + ":" + y + ":" + z + " is out of range from " + getFieldCenter());
		}
	}

	/**
	 * Actually just two integers.
	 */
	private static class TwoInts implements Comparable<TwoInts> {

		final int first;
		final int second;

		TwoInts(int first, int second) {
			this.first = first;
			this.second = second;
		}

		// This needs to be sortable to work with TreeSet.
		@Override
		public int compareTo(TwoInts other) {
			if (first != other.first) {
				return first - other.first;
			} else {
				return second - other.second;
			}
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof TwoInts)) {
				return false;
			}
			TwoInts otherInts = (TwoInts) other;
			return first == otherInts.first && second == otherInts.second;
		}

		@Override
		public int hashCode() {
			return ((first + 512) << 14) | (second + 512);
		}
	}

}
