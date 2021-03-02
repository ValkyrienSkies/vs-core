package org.valkyrienskies.core.datastructures;

import javax.annotation.Nullable;
import org.joml.primitives.AABBd;

/**
 * An IBlockPosSet that also has support for creating an AxisAlignedBB that contains all positions in the set.
 */
public interface IBlockPosSetAABB extends IBlockPosSet {

	/**
	 * Creates a tight AxisAlignedBB that contains all block positions in the set.
	 *
	 * @return Null if there are no block positions in this set.
	 */
	@Nullable
	AABBd makeAABB();
}
