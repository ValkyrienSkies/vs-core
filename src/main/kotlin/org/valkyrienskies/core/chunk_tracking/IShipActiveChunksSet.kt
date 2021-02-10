package org.valkyrienskies.core.chunk_tracking

interface IShipActiveChunksSet {
    fun addChunkPos(chunkX: Int, chunkZ: Int): Boolean
    fun removeChunkPos(chunkX: Int, chunkZ: Int): Boolean
    fun iterateChunkPos(func: (Int, Int) -> Unit)

    companion object {
        fun chunkPosToLong(chunkX: Int, chunkZ: Int): Long {
            return (chunkX.toLong() shl 32) or chunkZ.toLong()
        }

        fun longToChunkX(chunkLong: Long): Int {
            return (chunkLong shr 32).toInt()
        }

        fun longToChunkZ(chunkLong: Long): Int {
            return (chunkLong and 0xFFFFFFFF).toInt()
        }
    }
}