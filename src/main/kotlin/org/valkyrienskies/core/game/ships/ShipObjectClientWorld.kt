package org.valkyrienskies.core.game.ships

import org.valkyrienskies.core.game.ShipId
import org.valkyrienskies.core.game.ships.networking.ShipObjectNetworkManagerClient

class ShipObjectClientWorld(
    override val queryableShipData: MutableQueryableShipDataCommon
) : ShipObjectWorld(queryableShipData) {

    private val shipObjectMap = HashMap<ShipId, ShipObjectClient>()
    override val shipObjects: Map<ShipId, ShipObjectClient> = shipObjectMap

    val networkManager = ShipObjectNetworkManagerClient(this)

    fun addShipObject(data: ShipDataCommon) {
        require(shipObjectMap.putIfAbsent(data.id, ShipObjectClient(data)) == null) { "Ship was already present" }
        queryableShipData.addShipData(data)
    }

    fun removeShipObject(id: ShipId) {
        shipObjectMap.remove(id)
        queryableShipData.removeShipData(id)
    }

    override fun tickShips() {
        super.tickShips()
        // For now, just make a [ShipObject] for every [ShipData]
        for (shipData in queryableShipData) {
            val shipID = shipData.id
            shipObjectMap.computeIfAbsent(shipID) { ShipObjectClient(shipData) }
        }
    }
}
