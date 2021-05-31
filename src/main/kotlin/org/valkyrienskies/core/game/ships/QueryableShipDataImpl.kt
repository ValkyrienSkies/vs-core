package org.valkyrienskies.core.game.ships

import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.datastructures.ChunkClaimMap
import org.valkyrienskies.core.game.ShipId

typealias QueryableShipDataServer = QueryableShipData<ShipData>
typealias QueryableShipDataCommon = QueryableShipData<ShipDataCommon>
typealias MutableQueryableShipDataServer = MutableQueryableShipData<ShipData>
typealias MutableQueryableShipDataCommon = MutableQueryableShipData<ShipDataCommon>

interface QueryableShipData<out ShipDataType : ShipDataCommon> : Iterable<ShipDataType> {
    val uuidToShipData: Map<ShipId, ShipDataType>
    override fun iterator(): Iterator<ShipDataType>
    fun getShipDataFromUUID(uuid: ShipId): ShipDataType?
    fun getShipDataFromChunkPos(chunkX: Int, chunkZ: Int): ShipDataType?
    fun getShipDataIntersecting(aabb: AABBdc): Iterator<ShipDataType>
}

interface MutableQueryableShipData<ShipDataType : ShipDataCommon> : QueryableShipData<ShipDataType> {
    fun addShipData(shipData: ShipDataType)
    fun removeShipData(shipData: ShipDataType)
    fun removeShipData(id: ShipId)
}

open class QueryableShipDataImpl<ShipDataType : ShipDataCommon>(
    data: Iterable<ShipDataType> = emptyList()
) : MutableQueryableShipData<ShipDataType> {

    private val _uuidToShipData: HashMap<ShipId, ShipDataType> = HashMap()
    override val uuidToShipData: Map<ShipId, ShipDataType> = _uuidToShipData
    private val chunkClaimToShipData: ChunkClaimMap<ShipDataType> = ChunkClaimMap()

    init {
        data.forEach(::addShipData)
    }

    override fun iterator(): Iterator<ShipDataType> {
        return _uuidToShipData.values.iterator()
    }

    override fun getShipDataFromUUID(uuid: ShipId): ShipDataType? {
        return _uuidToShipData[uuid]
    }

    override fun getShipDataFromChunkPos(chunkX: Int, chunkZ: Int): ShipDataType? {
        return chunkClaimToShipData.get(chunkX, chunkZ)
    }

    override fun addShipData(shipData: ShipDataType) {
        if (getShipDataFromUUID(shipData.id) != null) {
            throw IllegalArgumentException("Adding shipData $shipData failed because of duplicated UUID.")
        }
        _uuidToShipData[shipData.id] = shipData
        chunkClaimToShipData.set(shipData.chunkClaim, shipData)
    }

    override fun removeShipData(id: ShipId) {
        removeShipData(getShipDataFromUUID(id)!!)
    }

    override fun removeShipData(shipData: ShipDataType) {
        if (getShipDataFromUUID(shipData.id) == null) {
            throw IllegalArgumentException("Removing $shipData failed because it wasn't in the UUID map.")
        }
        _uuidToShipData.remove(shipData.id)
        chunkClaimToShipData.remove(shipData.chunkClaim)
    }

    override fun getShipDataIntersecting(aabb: AABBdc): Iterator<ShipDataType> {
        // TODO("Use https://github.com/tzaeschke/phtree")
        return _uuidToShipData.values
            .filter { it.shipAABB.intersectsAABB(aabb as AABBd) }
            .iterator()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QueryableShipDataImpl<*>

        if (uuidToShipData != other.uuidToShipData) return false

        return true
    }

    override fun hashCode(): Int {
        return uuidToShipData.hashCode()
    }
}
