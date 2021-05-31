package org.valkyrienskies.core.networking.delta

import io.netty.buffer.ByteBuf

/**
 * Delta encodes type T. It does not necessarily delta the whole object, it may just do a few fields.
 */
interface DeltaAlgorithm<T> {
    /**
     * Takes the [old] and the [new] and writes the size of the delta and the delta to [dest], then returns it.
     */
    fun encode(old: T, new: T, dest: ByteBuf): ByteBuf

    /**
     * Takes the [old] and the [delta] (with the size), generated from [encode], and produces the "new" T.
     * This method must not mutate [old].
     */
    fun apply(old: T, delta: ByteBuf): T
}
