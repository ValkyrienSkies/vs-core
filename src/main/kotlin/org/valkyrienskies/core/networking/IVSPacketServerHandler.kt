package org.valkyrienskies.core.networking

import org.valkyrienskies.core.game.bridge.IPlayer

/**
 * Handles [IVSPacket]s on the server side
 */
fun interface IVSPacketServerHandler {
    fun handlePacket(vsPacket: IVSPacket, sender: IPlayer)
}
