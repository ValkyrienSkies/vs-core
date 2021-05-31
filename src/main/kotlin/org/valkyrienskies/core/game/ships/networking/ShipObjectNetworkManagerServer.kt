package org.valkyrienskies.core.game.ships.networking

import io.netty.buffer.Unpooled
import org.valkyrienskies.core.game.bridge.IPlayer
import org.valkyrienskies.core.game.ships.ShipObjectServer
import org.valkyrienskies.core.game.ships.ShipObjectServerWorld
import org.valkyrienskies.core.networking.VSNetworking
import org.valkyrienskies.core.util.serialization.VSJacksonUtil
import org.valkyrienskies.core.util.writeQuatAsFloat
import org.valkyrienskies.core.util.writeVec3AsFloat

class ShipObjectNetworkManagerServer(
    private val parent: ShipObjectServerWorld
) {

    private val ships get() = parent.shipObjects.values

    private lateinit var players: Iterable<IPlayer>

    fun tick() {
        this.players = parent.lastPlayers
        updateShipData()
        updateTracking()
        sendTransforms()
    }

    private fun IPlayer.getTrackedShips() =
        ships.filter { it.playersTracking.contains(this) }

    private fun updateTracking() {
        // Just track everything for now....
        players.forEach { player ->
            val shipsToTrack = ships.filter { !it.playersTracking.contains(player) }
            startTracking(player, shipsToTrack)
        }
    }

    private fun endTracking(player: IPlayer, shipsToNotTrack: Iterable<ShipObjectServer>) {
        shipsToNotTrack.forEach { ship ->
            require(ship.playersTracking.remove(player)) { "Stopped tracking untracked player" }
        }
        // do nothing for now
    }

    private fun startTracking(player: IPlayer, shipsToTrack: Iterable<ShipObjectServer>) {
        val buf = Unpooled.buffer()

        shipsToTrack.forEach { ship ->
            require(ship.playersTracking.add(player)) { "Tracked already tracked player" }
            buf.writeLong(ship.id)
            val data = VSJacksonUtil.defaultMapper.writeValueAsBytes(ship.shipData)
            buf.writeInt(data.size)
            buf.writeBytes(data)
        }

        // Send them the full ship data
        VSNetworking.Packets.TCP_SHIP_DATA_CREATE.sendToClient(buf, player)
    }

    /**
     * Send ShipData deltas to players
     */
    private fun updateShipData() {
        players.forEach { player ->
            val buf = Unpooled.buffer()
            val trackedShips = player.getTrackedShips()

            buf.writeInt(trackedShips.size)
            trackedShips.forEach { ship ->
                buf.writeLong(ship.id)
                ship.shipDataChannel.encode(ship.shipData, buf)
            }

            VSNetworking.Packets.TCP_SHIP_DATA_DELTA.sendToClient(buf, player)
        }
    }

    /**
     * Send ship transforms to players
     */
    private fun sendTransforms() {
        players.forEach { player ->
            // Ships the player is tracking
            val trackedShips = player.getTrackedShips()
            // Write ship transforms into a ByteBuf
            val buf = Unpooled.buffer()

            buf.writeInt(parent.tickNumber)
            buf.writeInt(trackedShips.size)

            trackedShips.forEach { ship ->
                val transform = ship.shipData.shipTransform
                val physicsData = ship.shipData.physicsData

                buf.writeLong(ship.id)
                buf.writeQuatAsFloat(transform.shipCoordinatesToWorldCoordinatesRotation)
                buf.writeVec3AsFloat(transform.shipPositionInWorldCoordinates)
                buf.writeVec3AsFloat(physicsData.linearVelocity)
                buf.writeVec3AsFloat(physicsData.angularVelocity)
            }

            // Send it to the player
            VSNetworking.Packets.UDP_SHIP_TRANSFORM.sendToClient(buf, player)
        }
    }
}

