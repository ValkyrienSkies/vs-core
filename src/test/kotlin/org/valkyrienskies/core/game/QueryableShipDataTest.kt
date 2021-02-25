package org.valkyrienskies.core.game

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.valkyrienskies.core.VSRandomUtils
import org.valkyrienskies.core.util.serialization.VSJacksonUtil
import kotlin.random.Random

internal class QueryableShipDataTest {

    /**
     * Tests getting [ShipData] from [java.util.UUID].
     */
    @Test
    fun testGetShipFromUUID() {
        val queryableShipData = QueryableShipData()
        val shipData = VSRandomUtils.randomShipData()
        queryableShipData.addShipData(shipData)
        assertEquals(shipData, queryableShipData.getShipDataFromUUID(shipData.shipUUID))
    }

    /**
     * Tests getting [ShipData] from [ChunkClaim].
     */
    @RepeatedTest(25)
    fun testGetShipFromChunkClaim() {
        val queryableShipData = QueryableShipData()
        val shipData = VSRandomUtils.randomShipData()
        queryableShipData.addShipData(shipData)
        val shipChunkClaim = shipData.chunkClaim

        // Test chunks inside of the claim
        for (count in 1 .. 1000) {
            val chunkX = Random.nextInt(shipChunkClaim.xStart, shipChunkClaim.xEnd + 1)
            val chunkZ = Random.nextInt(shipChunkClaim.zStart, shipChunkClaim.zEnd + 1)
            assertEquals(shipData, queryableShipData.getShipDataFromChunkPos(chunkX, chunkZ))
        }

        // Test chunks outside of the claim
        for (count in 1 .. 1000) {
            val chunkX = if (Random.nextBoolean()) {
                shipChunkClaim.xStart - Random.nextInt(1, 1000)
            } else {
                shipChunkClaim.xEnd + Random.nextInt(1, 1000)
            }
            val chunkZ = if (Random.nextBoolean()) {
                shipChunkClaim.zStart - Random.nextInt(1, 1000)
            } else {
                shipChunkClaim.zEnd + Random.nextInt(1, 1000)
            }
            assertEquals(null, queryableShipData.getShipDataFromChunkPos(chunkX, chunkZ))
        }

        // Test more chunks outside of the claim
        assertEquals(null, queryableShipData.getShipDataFromChunkPos(shipChunkClaim.xStart, shipChunkClaim.zStart - 1))
        assertEquals(null, queryableShipData.getShipDataFromChunkPos(shipChunkClaim.xStart - 1, shipChunkClaim.zStart))
        assertEquals(null, queryableShipData.getShipDataFromChunkPos(shipChunkClaim.xEnd, shipChunkClaim.zEnd + 1))
        assertEquals(null, queryableShipData.getShipDataFromChunkPos(shipChunkClaim.xEnd + 1, shipChunkClaim.zEnd))
    }

    /**
     * Test adding duplicate [ShipData].
     */
    @Test
    fun testAddDuplicateShip() {
        val queryableShipData = QueryableShipData()
        val shipData = VSRandomUtils.randomShipData()
        queryableShipData.addShipData(shipData)
        assertThrows(IllegalArgumentException::class.java) {
            queryableShipData.addShipData(shipData)
        }
    }

    /**
     * Test removing [ShipData] in [QueryableShipData].
     */
    @Test
    fun testRemovingShipNotInQueryableShipData() {
        val queryableShipData = QueryableShipData()
        val shipData = VSRandomUtils.randomShipData()
        val otherShipData = VSRandomUtils.randomShipData()
        queryableShipData.addShipData(shipData)
        assertThrows(IllegalArgumentException::class.java) {
            queryableShipData.removeShipData(otherShipData)
        }
    }

    /**
     * Test getting a [ShipData] by its [org.joml.primitives.AABBdc]
     */
    @Test
    fun testGettingShipByBoundingBox() {
        val queryableShipData = QueryableShipData()
        val shipData = VSRandomUtils.randomShipData()
        queryableShipData.addShipData(shipData)
        val shipsIntersectingBB = queryableShipData.getShipDataIntersecting(shipData.shipAABB)
        assertTrue(shipsIntersectingBB.hasNext())
        assertEquals(shipsIntersectingBB.next(), shipData)
        assertFalse(shipsIntersectingBB.hasNext())
    }

    /**
     * Tests the correctness of [QueryableShipData] serialization and deserialization.
     */
    @RepeatedTest(25)
    fun testSerializationAndDeSerialization() {
        val queryableShipData = VSRandomUtils.randomQueryableShipData(size=Random.nextInt(20))
        // Now serialize and deserialize and verify that they are the same
        val queryableShipDataSerialized = VSJacksonUtil.defaultMapper.writeValueAsBytes(queryableShipData.toList())
        val queryableShipDataDeserialized = QueryableShipData(VSJacksonUtil.defaultMapper.readValue(queryableShipDataSerialized))
        // Verify that both are equal
        assertEquals(queryableShipData, queryableShipDataDeserialized)
    }
}