package org.valkyrienskies.core.networking.delta

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap

/**
 * Manages delta encoded communications to keep a collection of objects with IDs in sync.
 *
 * [T] is the type of object being delta-encoded
 */
class DeltaEncodedChannelClientUDP<T>(
    private val algorithm: DeltaAlgorithm<T>,
    initialSnapshot: T
) {

    /**
     * Contains the snapshot index -> delta
     */
    private val history = Int2ObjectRBTreeMap<T>()
    var latestSnapshot = initialSnapshot
        private set

    init {
        history.put(Int.MIN_VALUE, initialSnapshot)
    }

    /**
     * Takes [newIndex], [oldIndex], and [delta] and produces a snapshot.
     *
     * [newIndex] is the index of this snapshot
     * [oldIndex] is the index of the snapshot this delta is encoded relative to
     */
    fun decode(delta: ByteBuf, newIndex: Int, oldIndex: Int): T {
        val oldSnapshot = history.get(oldIndex)
        val newSnapshot = algorithm.apply(oldSnapshot, delta)

        history.put(newIndex, newSnapshot)
        latestSnapshot = newSnapshot
        pruneOldSnapshots(oldIndex)

        return newSnapshot
    }

    /**
     * [current] is the most recent snapshot index the server has delta encoded against
     * (i.e. the latest index the server is known to have received an ack for)
     */
    private fun pruneOldSnapshots(current: Int) {
        history.subMap(Int.MIN_VALUE, current).clear()
    }
}

