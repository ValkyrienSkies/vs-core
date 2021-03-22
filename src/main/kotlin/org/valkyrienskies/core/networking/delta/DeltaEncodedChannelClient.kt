package org.valkyrienskies.core.networking.delta

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.shorts.Short2ObjectRBTreeMap
import org.valkyrienskies.core.networking.channel.VSNetworkChannel
import org.valkyrienskies.core.util.toIntUnsigned

/**
 * Manages delta encoded communications to keep a collection of objects with IDs in sync.
 *
 * [T] is the type of object being delta-encoded
 */
class DeltaEncodedChannelClient<T>(
    private val algorithm: DeltaAlgorithm<T>,
    private val channel: VSNetworkChannel
) {

    /**
     * Contains the snapshot index -> delta
     */
    private val history = Short2ObjectRBTreeMap<T>()

    fun initialize(obj: T) {
        history.put(Short.MIN_VALUE, obj)
    }

    fun decode(data: ByteBuf): T {
        // This is the index of this snapshot
        val newIndex = data.readShort()
        // This is the index of the snapshot this delta is encoded relative to
        val oldIndex = data.readShort()
        val oldSnapshot = history.get(oldIndex)

        val newSnapshot = algorithm.apply(oldSnapshot, data)

        history.put(newIndex, newSnapshot)
        pruneOldSnapshots(oldIndex)

        return newSnapshot
    }

    /**
     * [current] is the most recent snapshot index the server has delta encoded against
     * (i.e. the latest index the server is known to have received an ack for)
     */
    private fun pruneOldSnapshots(current: Short) {
        // spooky modulus  - remove the last half of snapshots older than current
        val pruneGEQ = (current.toIntUnsigned() - 32768 % 65536).toShort()
        val pruneLT = current

        // $pruneGEQ <= removed < $pruneLT

        // Remove anything less than than pruneLT
        // 0 <= removed < pruneLT
        history.subMap(0, pruneLT).clear()
        // Remove anything greater than or equal to pruneGEQ
        // pruneGEQ <= removed < 0
        history.subMap(pruneGEQ, 0).clear()
    }

    fun ack(dest: ByteBuf): ByteBuf {
        return dest
    }
}

