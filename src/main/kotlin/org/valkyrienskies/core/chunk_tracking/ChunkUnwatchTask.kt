package org.valkyrienskies.core.chunk_tracking

data class ChunkUnwatchTask<P>(private val chunkPos: Long, val playersNeedUnwatching: Iterable<P>) {
    fun getChunkX(): Int = IShipActiveChunksSet.longToChunkX(chunkPos)
    fun getChunkZ(): Int = IShipActiveChunksSet.longToChunkZ(chunkPos)
}
