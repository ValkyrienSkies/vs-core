package org.valkyrienskies.core.chunk_tracking

import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet.Companion.chunkPosToLong
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet.Companion.longToChunkX
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet.Companion.longToChunkZ

class ShipActiveChunksSet private constructor(
    private val chunkClaimSet: LongOpenHashSet
) : IShipActiveChunksSet {

    override fun clear() {
        chunkClaimSet.clear()
    }

    override fun setFrom(other: IShipActiveChunksSet) {
        chunkClaimSet.clear()
        other.iterateChunkPos { x, z -> addChunkPos(x, z) }
    }

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

    override fun equals(other: Any?): Boolean {
        if (super.equals(other)) {
            return true
        }
        if (other is ShipActiveChunksSet) {
            return this.chunkClaimSet == other.chunkClaimSet
        }
        return false
    }

    override fun hashCode(): Int {
        return chunkClaimSet.hashCode()
    }

    companion object {
        fun create(): ShipActiveChunksSet {
            return ShipActiveChunksSet(LongOpenHashSet())
        }
    }
}
