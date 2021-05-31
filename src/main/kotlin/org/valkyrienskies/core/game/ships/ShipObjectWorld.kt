package org.valkyrienskies.core.game.ships

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.plus
import org.valkyrienskies.core.game.ShipId
import org.valkyrienskies.core.game.VSBlockType
import org.valkyrienskies.core.util.coroutines.TickableCoroutineDispatcher

/**
 * Manages all the [ShipObject]s in a world.
 */
abstract class ShipObjectWorld(
    open val queryableShipData: QueryableShipDataCommon,
) {

    private val _dispatcher = TickableCoroutineDispatcher()

    val dispatcher: CoroutineDispatcher = _dispatcher
    val coroutineScope = MainScope() + _dispatcher

    abstract val shipObjects: Map<ShipId, ShipObject>

    var tickNumber = 0
        private set
    
    /**
     * Should be run on the physics thread
     */
    open fun tickShips() {
        _dispatcher.tick()
        tickNumber++
    }

    fun onSetBlock(
        posX: Int,
        posY: Int,
        posZ: Int,
        blockType: VSBlockType,
        oldBlockMass: Double,
        newBlockMass: Double
    ) {
        // If there is a ShipData at this position, then tell it about the block update
        queryableShipData.getShipDataFromChunkPos(posX shr 4, posZ shr 4)
            ?.onSetBlock(posX, posY, posZ, blockType, oldBlockMass, newBlockMass)

        // TODO: Update the physics voxel world here
        // voxelWorld.onSetBlock(posX, posY, posZ, blockType)
    }
}
