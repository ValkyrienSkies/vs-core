package org.valkyrienskies.core.game

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.datastructures.ChunkClaimMap
import java.util.*

/**
 * This object stores all the [ShipData] in a world. It can quickly query [ShipData] by their [UUID] and chunk positions
 * that they have claimed.
 */
@JsonDeserialize(using = QueryableShipData.Companion.QueryableShipDataDeserializer::class)
@JsonSerialize(using = QueryableShipData.Companion.QueryableShipDataSerializer::class)
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
        return chunkClaimToShipData.getDataAtChunkPosition(chunkX, chunkZ)
    }

    fun addShipData(shipData: ShipData) {
        if (getShipDataFromUUID(shipData.shipUUID) != null) {
            throw IllegalArgumentException("Adding shipData $shipData failed because of duplicated UUID.")
        }
        uuidToShipData[shipData.shipUUID] = shipData
        chunkClaimToShipData.addChunkClaim(shipData.chunkClaim, shipData)
    }

    fun removeShipData(shipData: ShipData) {
        if (getShipDataFromUUID(shipData.shipUUID) == null) {
            throw IllegalArgumentException("Removing $shipData failed because it wasn't in the UUID map.")
        }
        uuidToShipData.remove(shipData.shipUUID)
        chunkClaimToShipData.removeChunkClaim(shipData.chunkClaim)
    }

    fun getShipDataIntersecting(aabb: AABBdc): Iterator<ShipData> {
        // TODO("Use https://github.com/tzaeschke/phtree")
        return uuidToShipData.values.filter { shipData: ShipData -> shipData.shipAABB.intersectsAABB(aabb as AABBd) }
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

    companion object {
        class QueryableShipDataSerializer : StdSerializer<QueryableShipData>(QueryableShipData::class.java) {
            override fun serialize(value: QueryableShipData, gen: JsonGenerator, provider: SerializerProvider) {
                gen.writeStartArray()
                for (shipData in value) {
                    gen.writeObject(shipData)
                }
                gen.writeEndArray()
            }
        }

        class QueryableShipDataDeserializer : StdDeserializer<QueryableShipData>(QueryableShipData::class.java) {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): QueryableShipData {
                val queryableShipData = QueryableShipData()
                val shipDataList = p.readValueAs(Array<ShipData>::class.java)
                for (shipData in shipDataList) {
                    queryableShipData.addShipData(shipData)
                }
                return queryableShipData
            }
        }
    }
}