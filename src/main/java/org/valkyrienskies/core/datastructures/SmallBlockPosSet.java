package org.valkyrienskies.core.datastructures;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.IOException;
import java.util.Iterator;
import javax.annotation.Nonnull;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.valkyrienskies.core.game.ChunkClaim;
import org.valkyrienskies.core.util.VSIterationUtils;

/**
 * An implementation of IBlockPosSet that stores block positions as 1 integer. This is accomplished by storing each
 * position as its relative coordinates to the centerX and centerZ values of this set. In this implementation the x and
 * z positions are 12 bits each, so they can range anywhere from -2048 to + 2047 relative to centerX and centerZ. This
 * leaves 8 bits for storing the y coordinate, which allows it the range of 0 to 255, exactly the same as Minecraft.
 */
@JsonDeserialize(using = SmallBlockPosSet.SmallBlockPosSetDeserializer.class)
@JsonSerialize(using = SmallBlockPosSet.SmallBlockPosSetSerializer.class)
public class SmallBlockPosSet implements IBlockPosSet {

	private static final int BOT_12_BITS = 0x00000FFF;
	private static final int BOT_8_BITS = 0x000000FF;

	@Nonnull
	private final IntList compressedBlockPosList;
	@Nonnull
	private final Int2IntMap listValueToIndex;
	private final int centerX;
	private final int centerZ;

	public SmallBlockPosSet(ChunkClaim chunkClaim) {
		final Vector3ic centerCoordinates = chunkClaim.getCenterBlockCoordinates(new Vector3i());
		this.compressedBlockPosList = new IntArrayList();
		this.listValueToIndex = new Int2IntOpenHashMap();
		this.centerX = centerCoordinates.x();
		this.centerZ = centerCoordinates.z();
	}

	public SmallBlockPosSet(int centerX, int centerZ) {
		this.compressedBlockPosList = new IntArrayList();
		this.listValueToIndex = new Int2IntOpenHashMap();
		this.centerX = centerX;
		this.centerZ = centerZ;
	}

	@Override
	public boolean add(int x, int y, int z) throws IllegalArgumentException {
		if (!canStore(x, y, z)) {
			throw new IllegalArgumentException("Cannot store block position at <" + x + "," + y + "," + z + ">");
		}
		int compressedPos = compress(x, y, z);
		if (listValueToIndex.containsKey(compressedPos)) {
			return false;
		}
		compressedBlockPosList.add(compressedPos);
		listValueToIndex.put(compressedPos, compressedBlockPosList.size() - 1);
		return true;
	}

	@Override
	public boolean remove(int x, int y, int z) {
		if (!canStore(x, y, z)) {
			throw new IllegalArgumentException("Cannot remove block position at <" + x + "," + y + "," + z + ">");
		}
		int compressedPos = compress(x, y, z);
		if (!listValueToIndex.containsKey(compressedPos)) {
			return false;
		}

		int elementIndex = listValueToIndex.get(compressedPos);

		if (elementIndex == compressedBlockPosList.size() - 1) {
			// If the element we're removing is at the end then its EZ
			compressedBlockPosList.removeInt(elementIndex);
		} else {
			// Otherwise, swap the last element with the one we're removing, and then remove the end
			int lastElementValue = compressedBlockPosList.removeInt(compressedBlockPosList.size() - 1);
			compressedBlockPosList.set(elementIndex, lastElementValue);
			listValueToIndex.put(lastElementValue, elementIndex);
		}
		listValueToIndex.remove(compressedPos);

		return true;
	}

	@Override
	public boolean contains(int x, int y, int z) {
		if (!canStore(x, y, z)) {
			// This pos cannot exist in this set
			return false;
		}
		return listValueToIndex.containsKey(compress(x, y, z));
	}

	@Override
	public boolean canStore(int x, int y, int z) {
		int xLocal = x - centerX;
		int zLocal = z - centerZ;
		return !(y < 0 | y > 255 | xLocal < -2048 | xLocal > 2047 | zLocal < -2048 | zLocal > 2047);
	}

	@Override
	public int size() {
		return compressedBlockPosList.size();
	}

	@Nonnull
	@Override
	public Iterator<Vector3ic> iterator() {
		return new SmallBlockPosIterator(compressedBlockPosList.iterator());
	}

	@Override
	public void forEach(@Nonnull VSIterationUtils.IntTernaryConsumer action) {
		IntIterator iterator = compressedBlockPosList.iterator();
		while (iterator.hasNext()) {
			int compressed = iterator.nextInt();
			// Repeated code from decompress() because java has no output parameters.
			int z = compressed >> 20;
			int y = (compressed >> 12) & BOT_8_BITS;
			// this basically left-pads the int when casting so that the sign is preserved
			// not sure if there is a better way
			int x = (compressed & BOT_12_BITS) << 20 >> 20;
			action.accept(x + centerX, y, z + centerZ);
		}
	}

	@Override
	public void clear() {
		compressedBlockPosList.clear();
		listValueToIndex.clear();
	}

	@Nonnull
	private Vector3ic decompress(int compressed) {
		return decompressMutable(compressed, new Vector3i());
	}

	private Vector3i decompressMutable(int compressed, Vector3i mutableBlockPos) {
		int z = compressed >> 20;
		int y = (compressed >> 12) & BOT_8_BITS;
		// this basically left-pads the int when casting so that the sign is preserved
		// not sure if there is a better way
		int x = (compressed & BOT_12_BITS) << 20 >> 20;
		mutableBlockPos.set(x + centerX, y, z + centerZ);
		return mutableBlockPos;
	}

	private int compress(int x, int y, int z) {
		// Allocate 12 bits for x, 12 bits for z, and 8 bits for y.
		int xBits = (x - centerX) & BOT_12_BITS;
		int yBits = y & BOT_8_BITS;
		int zBits = (z - centerZ) & BOT_12_BITS;
		return xBits | (yBits << 12) | (zBits << 20);
	}

	@Override
	public void forEachUnsafe(@Nonnull VSIterationUtils.IntTernaryConsumer action) {
		int curIndex = 0;
		Vector3i mutableBlockPos = new Vector3i();
		while (compressedBlockPosList.size() >= curIndex) {
			try {
				int currentValue = compressedBlockPosList.get(curIndex);
				curIndex++;
				decompressMutable(currentValue, mutableBlockPos);
				action.accept(mutableBlockPos.x(), mutableBlockPos.y(), mutableBlockPos.z());
			} catch (Exception e) {
				// Catch concurrent read/write race condition
				return;
			}
		}
	}

	public int getCenterX() {
		return centerX;
	}

	public int getCenterZ() {
		return centerZ;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof IBlockPosSet) {
			return (((IBlockPosSet) other).size() == size()) && ((IBlockPosSet) other).containsAll(this);
		}
		return false;
	}

	private class SmallBlockPosIterator implements Iterator<Vector3ic> {

		private final IntIterator iterator;

		SmallBlockPosIterator(IntIterator intIterator) {
			this.iterator = intIterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Vector3ic next() {
			return decompress(iterator.next());
		}

	}

	public static class SmallBlockPosSetSerializer extends StdSerializer<SmallBlockPosSet> {

		public SmallBlockPosSetSerializer() {
			super((Class<SmallBlockPosSet>) null);
		}

		@Override
		public void serialize(SmallBlockPosSet value, JsonGenerator gen,
			SerializerProvider provider) throws IOException {

			gen.writeStartObject();

			gen.writeFieldName("positions");
			gen.writeStartArray(value.compressedBlockPosList, value.compressedBlockPosList.size());
			IntIterator iter = value.compressedBlockPosList.iterator();

			while (iter.hasNext()) {
				gen.writeNumber(iter.nextInt());
			}
			gen.writeEndArray();

			gen.writeNumberField("centerX", value.centerX);
			gen.writeNumberField("centerZ", value.centerZ);

			gen.writeEndObject();
		}

	}

	public static class SmallBlockPosSetDeserializer extends StdDeserializer<SmallBlockPosSet> {

		public SmallBlockPosSetDeserializer() {
			super((Class<?>) null);
		}

		@Override
		public SmallBlockPosSet deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException {
			JsonNode node = p.getCodec().readTree(p);

			int centerX = node.get("centerX").asInt();
			int centerZ = node.get("centerZ").asInt();

			SmallBlockPosSet set = new SmallBlockPosSet(centerX, centerZ);

			for (JsonNode elem : node.get("positions")) {
				int positionInt = elem.asInt();
				set.compressedBlockPosList.add(positionInt);
				set.listValueToIndex.put(positionInt, set.compressedBlockPosList.size() - 1);
			}

			return set;
		}
	}
}
