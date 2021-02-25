package org.valkyrienskies.core.game

import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.datastructures.ChunkClaimMap
import java.util.*

/**
 * This object stores all the [ShipData] in a world. It can quickly query [ShipData] by their [UUID] and chunk positions
 * that they have claimed.
 */
class QueryableShipData(data: Iterable<ShipData> = emptyList()) : Iterable<ShipData> {

    private val uuidToShipData: MutableMap<UUID, ShipData>
    private val chunkClaimToShipData: ChunkClaimMap<ShipData>

    init {
        uuidToShipData = HashMap<UUID, ShipData>()
        chunkClaimToShipData = ChunkClaimMap()

        data.forEach(::addShipData)
    }

    override fun iterator(): Iterator<ShipData> {
        return uuidToShipData.values.iterator()
    }

    fun getShipDataFromUUID(uuid: UUID): ShipData? {
        return uuidToShipData[uuid]
    }

    fun getShipDataFromChunkPos(chunkX: Int, chunkZ: Int): ShipData? {
        return chunkClaimToShipData.get(chunkX, chunkZ)
    }

    fun addShipData(shipData: ShipData) {
        if (getShipDataFromUUID(shipData.shipUUID) != null) {
            throw IllegalArgumentException("Adding shipData $shipData failed because of duplicated UUID.")
        }
        uuidToShipData[shipData.shipUUID] = shipData
        chunkClaimToShipData.set(shipData.chunkClaim, shipData)
    }

    fun removeShipData(shipData: ShipData) {
        if (getShipDataFromUUID(shipData.shipUUID) == null) {
            throw IllegalArgumentException("Removing $shipData failed because it wasn't in the UUID map.")
        }
        uuidToShipData.remove(shipData.shipUUID)
        chunkClaimToShipData.remove(shipData.chunkClaim)
    }

    fun getShipDataIntersecting(aabb: AABBdc): Iterator<ShipData> {
        // TODO("Use https://github.com/tzaeschke/phtree")
        return uuidToShipData.values
            .filter { it.shipAABB.intersectsAABB(aabb as AABBd) }
            .iterator()
    }

    override fun equals(other: Any?): Boolean {
        if (super.equals(other)) {
            return true
        }
        if (other is QueryableShipData) {
            return other.uuidToShipData == this.uuidToShipData
        }
        return false
    }

    override fun hashCode(): Int {
        return uuidToShipData.hashCode()
    }
}