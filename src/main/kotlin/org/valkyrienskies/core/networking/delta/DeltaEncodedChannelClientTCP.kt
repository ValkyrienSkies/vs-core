package org.valkyrienskies.core.networking.delta

import io.netty.buffer.ByteBuf

class DeltaEncodedChannelClientTCP<T>(
    private val algorithm: DeltaAlgorithm<T>,
    initialSnapshot: T
) {
    var latestSnapshot = initialSnapshot
        private set

    fun decode(data: ByteBuf): T {
    }
}
