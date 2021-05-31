package org.valkyrienskies.core.game.bridge

import io.netty.buffer.ByteBuf
import org.joml.Vector3d
import org.valkyrienskies.core.networking.VSNetworking
import java.util.UUID

/**
 * An interface that represents players.
 */
interface IPlayer {
    /**
     * Sets [dest] to be the current position of this [IPlayer], and then returns dest.
     */
    fun getPosition(dest: Vector3d): Vector3d

    val uuid: UUID

    fun send(packet: VSNetworking.Packet) = packet.sendToClient(this)
    fun send(type: VSNetworking.PacketType, data: ByteBuf) = type.sendToClient(data, this)
}
