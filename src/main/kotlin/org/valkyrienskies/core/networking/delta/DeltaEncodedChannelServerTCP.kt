package org.valkyrienskies.core.networking.delta

import io.netty.buffer.ByteBuf

class DeltaEncodedChannelServerTCP<T>(
    private val algorithm: DeltaAlgorithm<T>,
    initialSnapshot: T
) {

    var latestSnapshot = initialSnapshot
        private set

    fun encode(newSnapshot: T, buf: ByteBuf): ByteBuf {
        algorithm.encode(latestSnapshot, newSnapshot, buf)
        latestSnapshot = newSnapshot
        return buf
    }
}
