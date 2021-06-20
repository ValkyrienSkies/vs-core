package org.valkyrienskies.core.game.ships

/**
 * A [ShipObject] is essentially a [ShipData] that has been loaded.
 *
 * Its just is to interact with the player. This includes stuff like rendering, colliding with entities, and adding
 * a rigid body to the physics engine.
 */
open class ShipObject(
    shipData: ShipDataCommon
) {
    val id get() = shipData.id

    @Suppress("CanBePrimaryConstructorProperty") // don't want to refer to open val in constructor
    open val shipData: ShipDataCommon = shipData
}
