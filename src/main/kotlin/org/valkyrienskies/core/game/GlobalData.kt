package org.valkyrienskies.core.game

typealias ShipId = Long

// TODO: serialize this
object GlobalData {

    private var nextShipId = 0L

    fun allocateShipId(): ShipId = nextShipId++
}
