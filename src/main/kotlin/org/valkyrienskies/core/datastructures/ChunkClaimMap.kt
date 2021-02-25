package org.valkyrienskies.core.datastructures

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import org.valkyrienskies.core.game.ChunkClaim

/**
 * Maps [ChunkClaim]s to [T].
 *
 * The [get] function allows accessing the [T] that claims that chunk position (if there is one) in
 * O(1) time. It makes no objects so its very efficient.
 */
class ChunkClaimMap<T> {

    private val backingMap: Long2ObjectMap<T> = Long2ObjectOpenHashMap()

    operator fun set(chunkClaim: ChunkClaim, data: T) {
        val claimAsLong = chunkClaim.toLong()
        if (backingMap.containsKey(claimAsLong)) {
            // There is already data at this claim, throw exception
            throw IllegalArgumentException("Tried adding $data at $chunkClaim, but a value already exists at $chunkClaim")
        }
        backingMap.put(claimAsLong, data)
    }

    fun remove(chunkClaim: ChunkClaim) {
        val claimAsLong = chunkClaim.toLong()
        if (backingMap.remove(claimAsLong) == null) {
            // Throw exception if we didn't remove anything
            throw IllegalArgumentException("Tried to remove data at $chunkClaim, but that claim wasn't in the chunk claim map!")
        }
    }

    operator fun get(chunkClaim: ChunkClaim): T? {
        return backingMap[chunkClaim.toLong()]
    }

    operator fun get(chunkX: Int, chunkZ: Int): T? {
        val chunkPosToClaimAsLong = ChunkClaim.getClaimThenToLong(chunkX, chunkZ)
        return backingMap[chunkPosToClaimAsLong]
    }

}