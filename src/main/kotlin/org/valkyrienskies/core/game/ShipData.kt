package org.valkyrienskies.core.game

import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet
import org.valkyrienskies.core.chunk_tracking.ShipActiveChunksSet
import org.valkyrienskies.core.datastructures.IBlockPosSet
import org.valkyrienskies.core.datastructures.IBlockPosSetAABB
import org.valkyrienskies.core.datastructures.SmallBlockPosSetAABB
import java.util.*

/**
 * The purpose of [ShipData] is to keep track of the state of a ship; it does not manage the behavior of a ship.
 *
 * See [ShipObject] to find the code that defines ship behavior (movement, player interactions, etc)
 */
data class ShipData(
    val shipUUID: UUID,
    var name: String,
    val chunkClaim: ChunkClaim,
    val physicsData: ShipPhysicsData,
    val inertiaData: ShipInertiaData,
    var shipTransform: ShipTransform,
    var prevTickShipTransform: ShipTransform,
    var shipAABB: AABBdc,
    val blockPositionSet: IBlockPosSetAABB,
    val shipActiveChunksSet: IShipActiveChunksSet
) {
    /**
     * Updates the [IBlockPosSet] and [ShipInertiaData] for this [ShipData]
     */
    internal fun onSetBlock(posX: Int, posY: Int, posZ: Int, blockType: VSBlockType, oldBlockMass: Double, newBlockMass: Double) {
        // Update [blockPositionsSet]
        if (blockType != VSBlockType.AIR) {
            blockPositionSet.add(posX, posY, posZ)
        } else {
            blockPositionSet.remove(posX, posY, posZ)
        }

        // Update [inertiaData]
        inertiaData.onSetBlock(posX, posY, posZ, oldBlockMass, newBlockMass)
    }

    companion object {
        /**
         * Creates a new [ShipData] from the given name and coordinates. The resulting [ShipData] is completely empty,
         * so it must be filled with blocks by other code.
         */
        internal fun newEmptyShipData(name: String, chunkClaim: ChunkClaim, shipCenterInWorldCoordinates: Vector3dc, shipCenterInShipCoordinates: Vector3dc): ShipData {
            val shipUUID = UUID.randomUUID()
            val physicsData = ShipPhysicsData.newEmptyShipPhysicsData()
            val inertiaData = ShipInertiaData.newEmptyShipInertiaData()
            val shipTransform = ShipTransform.newShipTransformFromCoordinates(shipCenterInWorldCoordinates, shipCenterInShipCoordinates)
            val prevTickShipTransform = shipTransform
            val shipAABB = AABBd()
            val blockPositionSet = SmallBlockPosSetAABB(chunkClaim)
            val shipActiveChunksSet = ShipActiveChunksSet.createNewShipActiveChunkSet()

            return ShipData(
                shipUUID = shipUUID,
                name = name,
                chunkClaim = chunkClaim,
                physicsData = physicsData,
                inertiaData = inertiaData,
                shipTransform = shipTransform,
                prevTickShipTransform = prevTickShipTransform,
                shipAABB = shipAABB,
                blockPositionSet = blockPositionSet,
                shipActiveChunksSet = shipActiveChunksSet
            )
        }
    }
}