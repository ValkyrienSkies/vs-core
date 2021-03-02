package org.valkyrienskies.core.game

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.RepeatedTest
import org.valkyrienskies.core.VSRandomUtils
import org.valkyrienskies.core.game.ships.ShipData
import org.valkyrienskies.core.util.serialization.VSJacksonUtil

internal class ShipDataTest {

	/**
	 * Tests the correctness of ShipData serialization and deserialization.
	 */
	@RepeatedTest(25)
	fun testSerializationAndDeSerialization() {
		val shipData = VSRandomUtils.randomShipData()
		// Now serialize and deserialize and verify that they are the same
		val blockPosSetSerialized = VSJacksonUtil.defaultMapper.writeValueAsBytes(shipData)
		val blockPosSetDeserialized = VSJacksonUtil.defaultMapper.readValue(
			blockPosSetSerialized,
			ShipData::class.java
		)

		// Verify that both are equal
		Assertions.assertEquals(shipData, blockPosSetDeserialized)
	}
}