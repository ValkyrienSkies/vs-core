package org.valkyrienskies.core.datastructures

import gnu.trove.map.TLongObjectMap
import gnu.trove.map.hash.TLongObjectHashMap
import org.valkyrienskies.core.game.ChunkClaim

/**
 * Maps [ChunkClaim]s to [T].
 *
 * The [getDataAtChunkPosition] function allows accessing the [T] that claims that chunk position (if there is one) in
 * O(1) time. It makes no objects so its very efficient.
 */
class ChunkClaimMap<T> {

    private val backingMap: TLongObjectMap<T> = TLongObjectHashMap()

    fun addChunkClaim(chunkClaim: ChunkClaim, data: T) {
        val claimAsLong = chunkClaim.toLong()
        if (backingMap.containsKey(claimAsLong)) {
            // There is already data at this claim, throw exception
            throw IllegalArgumentException("Tried adding $data at $chunkClaim, but a value already exists at $chunkClaim")
        }
        backingMap.put(claimAsLong, data)
    }

    fun removeChunkClaim(chunkClaim: ChunkClaim) {
        val claimAsLong = chunkClaim.toLong()
        if (backingMap.remove(claimAsLong) == null) {
            // Throw exception if we didn't remove anything
            throw IllegalArgumentException("Tried to remove data at $chunkClaim, but that claim wasn't in the chunk claim map!")
        }
    }

    fun getDataAtChunkPosition(chunkX: Int, chunkZ: Int): T? {
        val chunkPosToClaimAsLong = ChunkClaim.getClaimThenToLong(chunkX, chunkZ)
        return backingMap[chunkPosToClaimAsLong]
    }

    private fun convertChunkCoordinatesToLong(chunkX: Int, chunkZ: Int): Long {
        return ChunkClaim.getClaimThenToLong(chunkX, chunkZ)
    }
}