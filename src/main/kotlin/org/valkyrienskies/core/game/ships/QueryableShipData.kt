package org.valkyrienskies.core.game.ships

import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.datastructures.ChunkClaimMap
import org.valkyrienskies.core.game.ShipId

typealias QueryableShipDataServer = QueryableShipData<ShipData>
typealias QueryableShipDataCommon = QueryableShipData<ShipDataCommon>
typealias MutableQueryableShipDataCommon = MutableQueryableShipData<ShipDataCommon>

interface QueryableShipData<out ShipDataType : ShipDataCommon> : Iterable<ShipDataType> {
    val uuidToShipData: Map<ShipId, ShipDataType>
    override fun iterator(): Iterator<ShipDataType>
    fun getShipDataFromUUID(uuid: ShipId): ShipDataType?
    fun getShipDataFromChunkPos(chunkX: Int, chunkZ: Int): ShipDataType?
}

interface MutableQueryableShipData<ShipDataType : ShipDataCommon> : QueryableShipData<ShipDataType> {
    fun addShipData(shipData: ShipDataType)
    fun removeShipData(shipData: ShipDataType)
    fun removeShipData(id: ShipId)
}

interface MutableQueryableShipDataServer : MutableQueryableShipData<ShipData> {
    fun getShipDataIntersecting(aabb: AABBdc): Iterator<ShipData>
}

class QueryableShipDataServerImpl(
    data: Iterable<ShipData> = emptyList()
) : QueryableShipDataCommonImpl<ShipData>(data), MutableQueryableShipDataServer {
    override fun getShipDataIntersecting(aabb: AABBdc): Iterator<ShipData> {
        // TODO("Use https://github.com/tzaeschke/phtree")
        return _uuidToShipData.values
            .filter { it.shipAABB.intersectsAABB(aabb as AABBd) }
            .iterator()
    }
}

open class QueryableShipDataCommonImpl<ShipDataType : ShipDataCommon>(
    data: Iterable<ShipDataType> = emptyList()
) : MutableQueryableShipData<ShipDataType> {

    protected val _uuidToShipData: HashMap<ShipId, ShipDataType> = HashMap()
    override val uuidToShipData: Map<ShipId, ShipDataType> = _uuidToShipData
    protected val chunkClaimToShipData: ChunkClaimMap<ShipDataType> = ChunkClaimMap()

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
        chunkClaimToShipData[shipData.chunkClaim] = shipData
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QueryableShipDataCommonImpl<*>

        if (uuidToShipData != other.uuidToShipData) return false

        return true
    }

    override fun hashCode(): Int {
        return uuidToShipData.hashCode()
    }
}
