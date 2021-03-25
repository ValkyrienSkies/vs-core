package org.valkyrienskies.core.networking.delta

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.shorts.Short2ObjectRBTreeMap
import org.valkyrienskies.core.networking.channel.VSNetworkChannel

/**
 * Manages delta encoded communications to keep a collection of objects with IDs in sync.
 *
 * [T] is the type of object being delta-encoded
 */
class DeltaEncodedChannelServer<T>(
    private val algorithm: DeltaAlgorithm<T>,
    clients: Iterable<VSNetworkChannel>
) {

    data class Snapshot<T>(val index: Short, val data: T)

    /**
     * Contains the snapshot index -> snapshot
     */
    private val history = Short2ObjectRBTreeMap<T>()

    private val clients = clients.toHashSet()

    /**
     * Contains the client -> last acknowledged snapshot
     */
    private val clientHistory = HashMap<VSNetworkChannel, Snapshot<T>>()

    fun receiveAck(client: VSNetworkChannel, idx: Int, dest: ByteBuf): ByteBuf {
        return dest
    }

    fun initialize(obj: T) {
        history.put(0, obj)
    }

    fun encode() {
    }
}
