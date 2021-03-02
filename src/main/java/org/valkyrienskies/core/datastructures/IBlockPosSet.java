package org.valkyrienskies.core.datastructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nonnull;
import org.joml.Vector3ic;
import org.valkyrienskies.core.util.VSIterationUtils;

/**
 * Acts just like a <code>Set&lt;Vector3ic&gt;</code>, but it can store the data however it wants to.
 */
public interface IBlockPosSet extends Set<Vector3ic> {

	boolean add(int x, int y, int z) throws IllegalArgumentException;

	boolean remove(int x, int y, int z);

	boolean contains(int x, int y, int z);

	/**
	 * The IBlockPosSet is not guaranteed to be able to store everything. Call canStore() to know what can be stored and
	 * what cannot.
	 */
	boolean canStore(int x, int y, int z);

	void clear();

	/**
	 * Fast way to iterate over all BlockPos in this Set that does not require us to create BlockPos objects. Although
	 * this default implementation is still slow, it should be accelerated by the data structure.
	 */
	default void forEach(@Nonnull VSIterationUtils.IntTernaryConsumer action) {
		forEach(blockPos -> action.accept(blockPos.x(), blockPos.y(), blockPos.z()));
	}

	/**
	 * Allows other threads (for example physics threads) to iterate over the elements in this list unsafely. Though the
	 * iteration is almost always correct, it is possible to iterate over an element more than once, or not all. But the
	 * chances of these events occurring is very small.
	 */
	default void forEachUnsafe(@Nonnull VSIterationUtils.IntTernaryConsumer action) {
		throw new UnsupportedOperationException();
	}

	default boolean add(@Nonnull Vector3ic pos) throws IllegalArgumentException {
		return add(pos.x(), pos.y(), pos.z());
	}

	default boolean remove(@Nonnull Vector3ic pos) {
		return remove(pos.x(), pos.y(), pos.z());
	}

	default boolean contains(@Nonnull Vector3ic pos) {
		return contains(pos.x(), pos.y(), pos.z());
	}

	default boolean canStore(@Nonnull Vector3ic pos) {
		return canStore(pos.x(), pos.y(), pos.z());
	}

	default boolean containsAll(@Nonnull Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	default boolean addAll(@Nonnull Collection<? extends Vector3ic> c) throws IllegalArgumentException {
		boolean modified = false;
		for (Vector3ic pos : c) {
			modified |= add(pos);
		}
		return modified;
	}

	default boolean removeAll(@Nonnull Collection<?> c) {
		boolean modified = false;
		for (Object o : c) {
			modified |= remove(o);
		}
		return modified;
	}

	default boolean retainAll(@Nonnull Collection<?> c) {
		boolean modified = false;
		for (Vector3ic pos : this) {
			if (!c.contains(pos)) {
				remove(pos);
				modified = true;
			}
		}
		return modified;
	}

	default boolean isEmpty() {
		return size() == 0;
	}

	default boolean remove(@Nonnull Object o) throws IllegalArgumentException {
		if (o instanceof Vector3ic) {
			return remove((Vector3ic) o);
		} else {
			throw new IllegalArgumentException("Object " + o + " is not a BlockPos!");
		}
	}

	default boolean contains(@Nonnull Object o) {
		if (o instanceof Vector3ic) {
			return contains((Vector3ic) o);
		} else {
			return false;
		}
	}

	/**
	 * Not recommended, this forces the IBlockPosSet to create a ton of BlockPos objects.
	 */
	@Nonnull
	default Object[] toArray() {
		Vector3ic[] arr = new Vector3ic[size()];
		Iterator<Vector3ic> iter = iterator();
		for (int i = 0; i < size(); i++) {
			arr[i] = iter.next();
		}
		return arr;
	}

	/**
	 * Not recommended, this forces the IBlockPosSet to create a ton of BlockPos objects.
	 */
	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	default <T> T[] toArray(@Nonnull T[] a) {
		return (T[]) toArray();
	}

}
