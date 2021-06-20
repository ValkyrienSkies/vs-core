package org.valkyrienskies.core.game.ships.networking

import com.fasterxml.jackson.module.kotlin.treeToValue
import io.netty.buffer.ByteBufInputStream
import kotlinx.coroutines.launch
import org.valkyrienskies.core.game.ShipId
import org.valkyrienskies.core.game.ships.ShipDataCommon
import org.valkyrienskies.core.game.ships.ShipObjectClient
import org.valkyrienskies.core.game.ships.ShipObjectClientWorld
import org.valkyrienskies.core.networking.VSNetworking
import org.valkyrienskies.core.networking.VSNetworking.Packet
import org.valkyrienskies.core.util.readQuatfAsDouble
import org.valkyrienskies.core.util.readVec3fAsDouble
import org.valkyrienskies.core.util.serialization.VSJacksonUtil

class ShipObjectNetworkManagerClient(
    private val parent: ShipObjectClientWorld
) {

    private val worldScope get() = parent.coroutineScope

    private val latestReceived = HashMap<ShipId, Int>()

    fun registerPacketListeners() {
        VSNetworking.Packets.UDP_SHIP_TRANSFORM.registerClientHandler(this::onShipTransform)
        VSNetworking.Packets.TCP_SHIP_DATA_CREATE.registerClientHandler(this::onShipDataCreate)
        VSNetworking.Packets.TCP_SHIP_DATA_DELTA.registerClientHandler(this::onShipDataDelta)
    }

    private fun onShipDataCreate(packet: Packet) = worldScope.launch {
        val mapper = VSJacksonUtil.defaultMapper

        val buf = packet.data
        val numShips = buf.readInt()

        repeat(numShips) {
            val size = buf.readInt()
            val shipDataJson = mapper.readTree(ByteBufInputStream(buf, size))
            val shipData = mapper.treeToValue<ShipDataCommon>(shipDataJson)!!

            val shipObject = ShipObjectClient(shipData, shipDataJson)
            parent.addShipObject(shipObject)
        }
    }

    private fun onShipDataDelta(packet: Packet) = worldScope.launch {
        val buf = packet.data
        val numShips = buf.readInt()

        repeat(numShips) {
            val shipId = buf.readLong()

            val ship = parent.shipObjects.getValue(shipId)
            val shipDataJson = ship.shipDataChannel.decode(buf)

            ShipDataCommon.deltaMapper
                .readerForUpdating(ship.shipData)
                .readValue<ShipDataCommon>(shipDataJson)
        }
    }

    private fun onShipTransform(packet: Packet) = worldScope.launch {
        val buf = packet.data
        val tickNum = buf.readInt()
        val numShips = buf.readInt()

        repeat(numShips) {
            val shipId = buf.readLong()
            val latest = latestReceived[shipId] ?: Int.MIN_VALUE
            if (latest >= tickNum) {
                buf.skipBytes(13 * 4)
            } else {
                val shipData = parent.shipObjects.getValue(shipId).shipData

                val rotation = buf.readQuatfAsDouble()
                val position = buf.readVec3fAsDouble()
                val linearVelocity = buf.readVec3fAsDouble()
                val angularVelocity = buf.readVec3fAsDouble()

                shipData.prevTickShipTransform = shipData.shipTransform
                shipData.shipTransform = shipData.prevTickShipTransform.copy(
                    shipCoordinatesToWorldCoordinatesRotation = rotation,
                    shipPositionInShipCoordinates = position
                )

                shipData.physicsData.linearVelocity.set(linearVelocity)
                shipData.physicsData.angularVelocity.set(angularVelocity)

                latestReceived[shipId] = tickNum
            }
        }
    }
}
