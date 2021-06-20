package org.valkyrienskies.core.game.ships

import com.fasterxml.jackson.databind.JsonNode
import org.valkyrienskies.core.networking.delta.DeltaEncodedChannelClientTCP
import org.valkyrienskies.core.networking.delta.JsonDiffDeltaAlgorithm

class ShipObjectClient(
    shipData: ShipDataCommon,
    shipDataJson: JsonNode
) : ShipObject(shipData) {
    companion object {
        private val jsonDiffDeltaAlgorithm = JsonDiffDeltaAlgorithm(ShipDataCommon.deltaMapper)
    }

    val renderTransform get() = shipData.shipTransform

    val shipDataChannel = DeltaEncodedChannelClientTCP(jsonDiffDeltaAlgorithm, shipDataJson)
}
