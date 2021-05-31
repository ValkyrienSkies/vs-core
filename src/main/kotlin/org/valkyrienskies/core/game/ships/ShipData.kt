package org.valkyrienskies.core.game.ships

import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet
import org.valkyrienskies.core.chunk_tracking.ShipActiveChunksSet
import org.valkyrienskies.core.game.ChunkClaim
import org.valkyrienskies.core.game.GlobalData
import org.valkyrienskies.core.game.ShipId
import org.valkyrienskies.core.game.VSBlockType

/**
 * The purpose of [ShipData] is to keep track of the state of a ship; it does not manage the behavior of a ship.
 *
 * See [ShipObject] to find the code that defines ship behavior (movement, player interactions, etc)
 */
class ShipData(
    id: ShipId,
    name: String,
    chunkClaim: ChunkClaim,
    physicsData: ShipPhysicsData,
    @PacketIgnore
    private val inertiaData: ShipInertiaData,
    shipTransform: ShipTransform,
    prevTickShipTransform: ShipTransform,
    @PacketIgnore
    var shipAABB: AABBdc,
    shipActiveChunksSet: IShipActiveChunksSet
) : ShipDataCommon(id, name, chunkClaim, physicsData, shipTransform, prevTickShipTransform, shipActiveChunksSet) {

    override fun copy() = ShipData(
        id, name, chunkClaim, physicsData, inertiaData,
        shipTransform, prevTickShipTransform, shipAABB, shipActiveChunksSet
    )

    override fun onSetBlock(
        posX: Int, posY: Int, posZ: Int, blockType: VSBlockType, oldBlockMass: Double, newBlockMass: Double
    ) {
        super.onSetBlock(posX, posY, posZ, blockType, oldBlockMass, newBlockMass)

        // Update [inertiaData]
        inertiaData.onSetBlock(posX, posY, posZ, oldBlockMass, newBlockMass)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ShipData

        if (inertiaData != other.inertiaData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + inertiaData.hashCode()
        return result
    }

    companion object {
        /**
         * Creates a new [ShipData] from the given name and coordinates. The resulting [ShipData] is completely empty,
         * so it must be filled with blocks by other code.
         */
        internal fun createEmpty(
            name: String,
            chunkClaim: ChunkClaim,
            shipCenterInWorldCoordinates: Vector3dc,
            shipCenterInShipCoordinates: Vector3dc
        ): ShipData {
            val shipTransform = ShipTransform.createFromCoordinatesAndRotationAndScaling(
                shipCenterInWorldCoordinates,
                shipCenterInShipCoordinates,
                Quaterniond().fromAxisAngleDeg(0.0, 1.0, 0.0, 45.0),
                Vector3d(.5, .5, .5)
            )

            return ShipData(
                id = GlobalData.allocateShipId(),
                name = name,
                chunkClaim = chunkClaim,
                physicsData = ShipPhysicsData.createEmpty(),
                inertiaData = ShipInertiaData.newEmptyShipInertiaData(),
                shipTransform = shipTransform,
                prevTickShipTransform = shipTransform,
                shipAABB = AABBd(),
                shipActiveChunksSet = ShipActiveChunksSet.create()
            )
        }
    }
}
