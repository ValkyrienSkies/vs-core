package org.valkyrienskies.core.datastructures

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.valkyrienskies.core.VSRandomUtils

internal class ChunkClaimMapTest {

    @Test
    fun addChunkClaim() {
        val chunkClaimMap = ChunkClaimMap<Int>()
        val chunkClaim = VSRandomUtils.randomChunkClaim()
        val value = VSRandomUtils.randomIntegerNotCloseToLimit()
        chunkClaimMap.set(chunkClaim, value)
        assertEquals(value, chunkClaimMap.get(chunkClaim.xStart, chunkClaim.zStart))
        assertThrows<IllegalArgumentException> { chunkClaimMap.set(chunkClaim, value) }
    }

    @Test
    fun removeChunkClaim() {
        val chunkClaimMap = ChunkClaimMap<Int>()
        val chunkClaim = VSRandomUtils.randomChunkClaim()
        val value = VSRandomUtils.randomIntegerNotCloseToLimit()
        chunkClaimMap.set(chunkClaim, value)
        assertEquals(value, chunkClaimMap.get(chunkClaim.xStart, chunkClaim.zStart))
        chunkClaimMap.remove(chunkClaim)
        assertEquals(null, chunkClaimMap.get(chunkClaim.xStart, chunkClaim.zStart))
        assertThrows<IllegalArgumentException> { chunkClaimMap.remove(chunkClaim) }
    }

    @Test
    fun getDataAtChunkPosition() {
        val chunkClaimMap = ChunkClaimMap<Int>()
        val chunkClaim = VSRandomUtils.randomChunkClaim()
        val value = VSRandomUtils.randomIntegerNotCloseToLimit()
        chunkClaimMap.set(chunkClaim, value)
        assertEquals(value, chunkClaimMap.get(chunkClaim.xStart, chunkClaim.zStart))
    }
}