package org.valkyrienskies.core.game

import org.joml.Quaterniond
import org.joml.Vector3d
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
    internal fun onSetBlock(
        posX: Int,
        posY: Int,
        posZ: Int,
        blockType: VSBlockType,
        oldBlockMass: Double,
        newBlockMass: Double
    ) {
        // Sanity check
        require(
            chunkClaim.contains(
                posX shr 4,
                posZ shr 4
            )
        ) { "Block at <$posX, $posY, $posZ> is not in the chunk claim belonging to $this" }

        // Update [blockPositionsSet]
        if (blockType != VSBlockType.AIR) {
            blockPositionSet.add(posX, posY, posZ)
        } else {
            blockPositionSet.remove(posX, posY, posZ)
        }

        // Update [inertiaData]
        inertiaData.onSetBlock(posX, posY, posZ, oldBlockMass, newBlockMass)

        // Add the chunk to the active chunk set
        shipActiveChunksSet.addChunkPos(posX shr 4, posZ shr 4)
        // Add the neighbors too (Required for rendering code in MC 1.16, chunks without neighbors won't render)
        // TODO: Make a separate set for keeping track of neighbors
        shipActiveChunksSet.addChunkPos((posX shr 4) - 1, (posZ shr 4))
        shipActiveChunksSet.addChunkPos((posX shr 4) + 1, (posZ shr 4))
        shipActiveChunksSet.addChunkPos((posX shr 4), (posZ shr 4) - 1)
        shipActiveChunksSet.addChunkPos((posX shr 4), (posZ shr 4) + 1)
    }

    companion object {
        /**
         * Creates a new [ShipData] from the given name and coordinates. The resulting [ShipData] is completely empty,
         * so it must be filled with blocks by other code.
         */
        internal fun newEmptyShipData(
            name: String,
            chunkClaim: ChunkClaim,
            shipCenterInWorldCoordinates: Vector3dc,
            shipCenterInShipCoordinates: Vector3dc
        ): ShipData {
            val shipUUID = UUID.randomUUID()
            val physicsData = ShipPhysicsData.newEmptyShipPhysicsData()
            val inertiaData = ShipInertiaData.newEmptyShipInertiaData()
            val shipTransform =
                ShipTransform.newShipTransformFromCoordinatesAndRotationAndScaling(shipCenterInWorldCoordinates, shipCenterInShipCoordinates, Quaterniond().fromAxisAngleDeg(0.0, 1.0, 0.0, 45.0), Vector3d(.5, .5, .5))
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