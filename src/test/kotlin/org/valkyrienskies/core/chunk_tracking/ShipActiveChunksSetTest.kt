package org.valkyrienskies.core.chunk_tracking

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.valkyrienskies.core.VSRandomUtils

internal class ShipActiveChunksSetTest {

    @Test
    fun addChunkPos() {
        val shipActiveChunksSet = ShipActiveChunksSet.createNewShipActiveChunkSet()
        assertTrue(shipActiveChunksSet.addChunkPos(0, 0))
        assertTrue(shipActiveChunksSet.addChunkPos(1, 1))
        assertFalse(shipActiveChunksSet.addChunkPos(0, 0))
    }

    @RepeatedTest(25)
    fun removeChunkPos() {
        val shipActiveChunksSet = ShipActiveChunksSet.createNewShipActiveChunkSet()
        val chunkX = VSRandomUtils.randomIntegerNotCloseToLimit()
        val chunkZ = VSRandomUtils.randomIntegerNotCloseToLimit()
        assertTrue(shipActiveChunksSet.addChunkPos(chunkX, chunkZ))
        assertTrue(shipActiveChunksSet.removeChunkPos(chunkX, chunkZ))
        assertFalse(shipActiveChunksSet.removeChunkPos(chunkX, chunkZ))
    }

    @Test
    fun iterateChunkPos() {
        val shipActiveChunksSet = ShipActiveChunksSet.createNewShipActiveChunkSet()
        assertTrue(shipActiveChunksSet.addChunkPos(200, 300))

        val sum: (Int, Int) -> Unit = { chunkX: Int, chunkZ: Int ->
            assertEquals(chunkX, 200)
            assertEquals(chunkZ, 300)
        }

        shipActiveChunksSet.iterateChunkPos(sum)
    }
}