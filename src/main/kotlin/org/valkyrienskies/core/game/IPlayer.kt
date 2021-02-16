package org.valkyrienskies.core.game

import org.joml.Vector3d
import java.util.*

/**
 * An interface that represents players.
 */
interface IPlayer {
    /**
     * Sets [dest] to be the current position of this [IPlayer], and then returns dest.
     */
    fun getPosition(dest: Vector3d): Vector3d

    fun getPlayerID(): UUID
}