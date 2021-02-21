package org.valkyrienskies.core.game

import org.joml.Vector3i
import org.valkyrienskies.core.game.ChunkClaim.Companion.DIAMETER
import java.lang.Math.floorDiv

/**
 * Each ChunkClaim claims all chunks between the coordinates
 * ([xIndex] * [DIAMETER], [zIndex] * [DIAMETER]) and ([xIndex] * [DIAMETER] + [DIAMETER] - 1, [zIndex] * [DIAMETER] + [DIAMETER] - 1)
 *
 * So for example, if [xIndex] is 5, [zIndex] is 10, and [DIAMETER] is 50, then this claim contains all chunks between
 * (250, 500) and (299, 549)
 */
data class ChunkClaim(val xIndex: Int, val zIndex: Int) {

    companion object {
        /**
         * Every ship is given [DIAMETER] x [DIAMETER] chunks, hard-coded.
         */
        const val DIAMETER: Int = 256

        private const val BOTTOM_32_BITS_MASK: Long = 0xFFFFFFFFL

        /**
         * Get the claim for a specific chunk
         */
        fun getClaim(chunkX: Int, chunkZ: Int) =
            ChunkClaim(getClaimXIndex(chunkX), getClaimZIndex(chunkZ))

        fun getClaimXIndex(chunkX: Int) = floorDiv(chunkX, DIAMETER)
        fun getClaimZIndex(chunkZ: Int) = floorDiv(chunkZ, DIAMETER)

        private fun claimToLong(claimXIndex: Int, claimZIndex: Int): Long {
            return ((claimXIndex.toLong() shl 32) or (claimZIndex.toLong() and BOTTOM_32_BITS_MASK))
        }

        fun getClaimThenToLong(chunkX: Int, chunkZ: Int): Long {
            // Compute the coordinates of the claim this chunk is in (not the same as chunk coordinates)
            val claimXIndex = getClaimXIndex(chunkX)
            val claimZIndex = getClaimZIndex(chunkZ)
            // Then convert
            return claimToLong(claimXIndex, claimZIndex)
        }
    }

    /**
     * x start (inclusive)
     */
    val xStart = xIndex * DIAMETER

    /**
     * x end (inclusive)
     */
    val xEnd = (xIndex * DIAMETER) + DIAMETER - 1

    /**
     * z start (inclusive)
     */
    val zStart = zIndex * DIAMETER

    /**
     * z end (inclusive)
     */
    val zEnd = (zIndex * DIAMETER) + DIAMETER - 1

    /**
     * The number of chunks owned by this claim
     */
    val size = (xEnd - xStart + 1) * (zEnd - zStart + 1)

    fun toLong(): Long {
        return claimToLong(xIndex, zIndex)
    }

    fun contains(x: Int, z: Int) =
        (x in xStart .. xEnd) and (z in zStart .. zEnd)

    fun getCenterBlockCoordinates(destination: Vector3i): Vector3i {
        val minBlockX = xStart * 16
        val maxBlockX = (xEnd * 16) - 1
        val minBlockZ = zStart * 16
        val maxBlockZ = (zEnd * 16) - 1

        val centerX = (minBlockX + maxBlockX) / 2
        val centerY = 127
        val centerZ = (minBlockZ + maxBlockZ) / 2
        return destination.set(centerX, centerY, centerZ)
    }

    fun getBlockSize(destination: Vector3i): Vector3i {
        val xSize = (xEnd - xStart + 1) * 16
        val ySize = 256
        val zSize = (zEnd - zStart + 1) * 16
        return destination.set(xSize, ySize, zSize)
    }

}