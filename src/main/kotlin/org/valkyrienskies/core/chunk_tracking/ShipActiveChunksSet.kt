package org.valkyrienskies.core.chunk_tracking

import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet.Companion.chunkPosToLong
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet.Companion.longToChunkX
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet.Companion.longToChunkZ

class ShipActiveChunksSet private constructor(
    private val chunkClaimSet: LongOpenHashSet
) : IShipActiveChunksSet {
    override fun addChunkPos(chunkX: Int, chunkZ: Int): Boolean {
        return chunkClaimSet.add(chunkPosToLong(chunkX, chunkZ))
    }

    override fun removeChunkPos(chunkX: Int, chunkZ: Int): Boolean {
        return chunkClaimSet.remove(chunkPosToLong(chunkX, chunkZ))
    }

    override fun iterateChunkPos(func: (Int, Int) -> Unit) {
        val chunkClaimIterator = chunkClaimSet.iterator()
        while (chunkClaimIterator.hasNext()) {
            val currentChunkClaimAsLong = chunkClaimIterator.nextLong()
            val chunkX = longToChunkX(currentChunkClaimAsLong)
            val chunkZ = longToChunkZ(currentChunkClaimAsLong)
            func(chunkX, chunkZ)
        }
    }

    companion object {
        fun createNewShipActiveChunkSet(): ShipActiveChunksSet {
            return ShipActiveChunksSet(LongOpenHashSet())
        }
    }
}