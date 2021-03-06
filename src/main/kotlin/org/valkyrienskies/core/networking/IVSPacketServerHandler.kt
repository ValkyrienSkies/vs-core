package org.valkyrienskies.core.networking

/**
 * Handles [IVSPacket]s on the server side
 * @param P The player object class
 */
fun interface IVSPacketServerHandler<P> {
    fun handlePacket(vsPacket: IVSPacket, sender: P)
}
