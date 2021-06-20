package org.valkyrienskies.core.game.ships

import org.valkyrienskies.core.game.ShipId
import org.valkyrienskies.core.game.ships.networking.ShipObjectNetworkManagerClient

class ShipObjectClientWorld(
    override val queryableShipData: MutableQueryableShipDataCommon
) : ShipObjectWorld(queryableShipData) {

    private val shipObjectMap = HashMap<ShipId, ShipObjectClient>()
    override val shipObjects: Map<ShipId, ShipObjectClient> = shipObjectMap

    val networkManager = ShipObjectNetworkManagerClient(this)

    fun addShipObject(obj: ShipObjectClient) {
        require(shipObjectMap.putIfAbsent(obj.id, obj) == null) { "Ship was already present" }
        queryableShipData.addShipData(obj.shipData)
    }

    fun removeShipObject(id: ShipId) {
        shipObjectMap.remove(id)
        queryableShipData.removeShipData(id)
    }

    override fun tickShips() {
        super.tickShips()
    }
}
