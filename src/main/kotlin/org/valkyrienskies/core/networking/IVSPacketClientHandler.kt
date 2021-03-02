package org.valkyrienskies.core.networking

/**
 * Handles [IVSPacket]s on the client side
 */
fun interface IVSPacketClientHandler {
	fun handlePacket(vsPacket: IVSPacket)
}