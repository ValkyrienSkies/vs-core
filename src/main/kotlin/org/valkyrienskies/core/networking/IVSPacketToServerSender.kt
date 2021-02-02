package org.valkyrienskies.core.networking

/**
 * Sends packets to the server.
 */
interface IVSPacketToServerSender {
    fun sendToServer(vsPacket: IVSPacket)
}