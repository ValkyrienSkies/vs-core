package org.valkyrienskies.core.chunk_tracking

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet.Companion.chunkPosToLong
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet.Companion.longToChunkX
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet.Companion.longToChunkZ

@JsonDeserialize(using = ShipActiveChunksSet.Companion.ShipActiveChunksSetDeserializer::class)
@JsonSerialize(using = ShipActiveChunksSet.Companion.ShipActiveChunksSetSerializer::class)
class ShipActiveChunksSet private constructor(
    private val chunkClaimSet: LongOpenHashSet
) : IShipActiveChunksSet {
    override fun addChunkPos(chunkX: Int, chunkZ: Int): Boolean {
        return chunkClaimSet.add(chunkPosToLong(chunkX, chunkZ))
    }

    override fun removeChunkPos(chunkX: Int, chunkZ: Int): Boolean {
        return chunkClaimSet.remove(chunkPosToLong(chunkX, chunkZ))
    }

    override fun iterateChunkPos(func: (Int, Int) -> Unit) {
        val chunkClaimIterator = chunkClaimSet.iterator()
        while (chunkClaimIterator.hasNext()) {
            val currentChunkClaimAsLong = chunkClaimIterator.nextLong()
            val chunkX = longToChunkX(currentChunkClaimAsLong)
            val chunkZ = longToChunkZ(currentChunkClaimAsLong)
            func(chunkX, chunkZ)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (super.equals(other)) {
            return true
        }
        if (other is ShipActiveChunksSet) {
            return this.chunkClaimSet == other.chunkClaimSet
        }
        return false
    }

    override fun hashCode(): Int {
        return chunkClaimSet.hashCode()
    }

    companion object {
        fun createNewShipActiveChunkSet(): ShipActiveChunksSet {
            return ShipActiveChunksSet(LongOpenHashSet())
        }

        class ShipActiveChunksSetSerializer : StdSerializer<ShipActiveChunksSet>(ShipActiveChunksSet::class.java) {
            override fun serialize(value: ShipActiveChunksSet, gen: JsonGenerator, provider: SerializerProvider) {
                gen.writeStartArray()
                value.iterateChunkPos { chunkX, chunkZ ->
                    run {
                        gen.writeNumber(chunkX)
                        gen.writeNumber(chunkZ)
                    }
                }
                gen.writeEndArray()
            }
        }

        class ShipActiveChunksSetDeserializer : StdDeserializer<ShipActiveChunksSet>(ShipActiveChunksSet::class.java) {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ShipActiveChunksSet {
                val shipActiveChunkSet = createNewShipActiveChunkSet()
                val chunkPositionArray = p.readValueAs(Array<Int>::class.java)
                for (i in 0 until (chunkPositionArray.size / 2)) {
                    val chunkX = chunkPositionArray[i * 2]
                    val chunkZ = chunkPositionArray[i * 2 + 1]
                    shipActiveChunkSet.addChunkPos(chunkX, chunkZ)
                }
                return shipActiveChunkSet
            }
        }
    }
}