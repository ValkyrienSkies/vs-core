package org.valkyrienskies.core.datastructures;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;
import org.joml.Vector3ic;

/**
 * Naive implementation of IBlockPosSet, basically just a wrapper around a HashSet&lt;BlockPos&gt;.
 * Used for testing purposes only.
 */
public class NaiveBlockPosSet implements IBlockPosSet {

	private final Set<Vector3ic> blockPosSet;

	public NaiveBlockPosSet() {
		this.blockPosSet = new HashSet<>();
	}

	@Override
	public boolean add(int x, int y, int z) {
		return blockPosSet.add(new Vector3i(x, y, z));
	}

	@Override
	public boolean remove(int x, int y, int z) {
		return blockPosSet.remove(new Vector3i(x, y, z));
	}

	@Override
	public boolean contains(int x, int y, int z) {
		return blockPosSet.contains(new Vector3i(x, y, z));
	}

	@Override
	public boolean canStore(int x, int y, int z) {
		return true;
	}

	@Override
	public int size() {
		return blockPosSet.size();
	}

	@NotNull
	@Override
	public Iterator<Vector3ic> iterator() {
		return blockPosSet.iterator();
	}

	@Override
	public void clear() {
		blockPosSet.clear();
	}

}
