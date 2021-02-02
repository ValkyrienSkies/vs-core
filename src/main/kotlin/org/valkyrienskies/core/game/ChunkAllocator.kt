package org.valkyrienskies.core.game

import org.joml.Vector2i
import org.joml.Vector3ic

/**
 * Allocates [ChunkClaim]s to be used by [ShipData].
 */
data class ChunkAllocator(
    private var nextClaimX: Int,
    private var nextClaimZ: Int,
) {
    companion object {
        /**
         * ChunkAllocator]s will allocate [ChunkClaim]s within the rectangle of positions between these coordinates.
         *
         * Remember that [ChunkClaim] coordinates aren't the same as block or chunk coordinates.
         *
         * The following block positions are calculated assuming that [ChunkClaim.DIAMETER]=256. See [ChunkClaim] for more information.
          */
        private const val X_INDEX_START = -7000 // Start at X=-28672000 block coordinates
        private const val X_INDEX_END = 7000 // End at X=28672000 block coordinates
        private const val Z_INDEX_START = 3000 // Start at Z=12288000 block coordinates
        private const val Z_INDEX_END = 7000 // End at Z=28672000 block coordinates

        fun newChunkAllocator(): ChunkAllocator {
            return ChunkAllocator(X_INDEX_START, Z_INDEX_START)
        }
    }

    /**
     * A quick way of determining if a Chunk is within the "shipyard", which is the region where ship chunks are stored
     */
    fun isChunkInShipyard(chunkX: Int, chunkZ: Int): Boolean {
        val claimXIndex = ChunkClaim.getClaimXIndex(chunkX)
        val claimZIndex = ChunkClaim.getClaimZIndex(chunkZ)

        return (claimXIndex in X_INDEX_START .. X_INDEX_END) and (claimZIndex in Z_INDEX_START .. Z_INDEX_END)
    }

    /**
     * Determines whether or not a chunk is in the shipyard
     * @param chunkPos The position of the chunk
     * @return True if the chunk is in the shipyard
     */
    fun isChunkInShipyard(chunkPos: Vector2i): Boolean {
        return isChunkInShipyard(chunkPos.x, chunkPos.y)
    }

    /**
     * Determines whether or not a block is in the shipyard
     * @param posX The X position of the block
     * @param posY The Y position of the block
     * @param posZ The Z position of the block
     * @return True if the block is in the shipyard
     */
    fun isBlockInShipyard(posX: Int, posY: Int, posZ: Int): Boolean {
        return isChunkInShipyard(posX shr 4, posZ shr 4)
    }

    /**
     * Determines whether or not a block is in the shipyard
     * @param blockPos The position of the block
     * @return True if the block is in the shipyard
     */
    fun isBlockInShipyard(blockPos: Vector3ic): Boolean {
        return isBlockInShipyard(blockPos.x(), blockPos.y(), blockPos.z())
    }

    /**
     * This finds the next empty chunkSet for use, currently only increases the xPos to get new
     * positions
     */
    fun allocateNewChunkClaim(): ChunkClaim {
        val nextClaim = ChunkClaim(nextClaimX, nextClaimZ)
        // Setup coordinates for the next claim
        nextClaimX++
        if (nextClaimX > X_INDEX_END) {
            nextClaimX = X_INDEX_START
            nextClaimZ++
        }

        // Sanity check
        if (nextClaimZ !in Z_INDEX_START .. Z_INDEX_END) {
            throw IllegalStateException("We ran out of chunk claims to allocate!")
        }

        return nextClaim
    }

}