package org.valkyrienskies.core.networking

/**
 * Sends packets to clients
 * @param P The player object class
 */
interface IVSPacketToClientSender<P> {
    fun sendToClient(vsPacket: IVSPacket, player: P)
}