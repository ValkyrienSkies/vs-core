package org.valkyrienskies.core.networking

import io.netty.buffer.ByteBuf

/**
 * Custom networking code for VS to allow for compatibility with Fabric and Forge.
 * Handlers registering packets and their handlers, as well as writing packets to bytes and processing packets when they arrive.
 *
 * @param P The player object class
 */
class VSPacketRegistry<P> {

	// The ID that will be given to the next packet registered by [registerVSPacket]
	private var nextRegistryId: Int = 0

	// Maps [IVSPacket] class types to their packet id
	private val classToIdMap = HashMap<Class<*>, Int>()

	// Maps packet ids to the supplier that creates a new empty version of that packet
	private val idToSupplierMap = HashMap<Int, () -> IVSPacket>()

	// Maps [IVSPacket] class types to the handler that runs them on the client
	private val classToClientHandlerMap = HashMap<Class<*>, IVSPacketClientHandler>()

	// Maps [IVSPacket] class types to the handler that runs them on the server
	private val classToServerHandlerMap = HashMap<Class<*>, IVSPacketServerHandler<P>>()

	/**
	 * Registers a new packet type
	 * @param clazz The class of the packet type
	 * @param supplier A supplier that creates a new empty instance of the packet type. Used when converting bytes to packets.
	 * @param clientHandler An object that runs the code when this packet is received by the client
	 * @param serverHandler An object that runs the code when this packet is received by the server
	 */
	fun <T : IVSPacket> registerVSPacket(
		clazz: Class<T>,
		supplier: () -> T,
		clientHandler: IVSPacketClientHandler?,
		serverHandler: IVSPacketServerHandler<P>?
	) {
		if (classToIdMap[clazz] != null) {
			throw IllegalArgumentException("Already registered packet handlers for $clazz")
		}
		val classId = nextRegistryId++
		classToIdMap[clazz] = classId
		idToSupplierMap[classId] = supplier
		if (clientHandler != null) classToClientHandlerMap[clazz] = clientHandler
		if (serverHandler != null) classToServerHandlerMap[clazz] = serverHandler
	}

	fun writeVSPacket(vsPacket: IVSPacket, byteBuf: ByteBuf) {
		val packetId = classToIdMap[vsPacket::class.java]
		requireNotNull(packetId) { "No packetId found for $vsPacket" }

		byteBuf.writeInt(packetId)
		vsPacket.write(byteBuf)
	}

	fun readVSPacket(byteBuf: ByteBuf): IVSPacket {
		val packetId: Int = byteBuf.readInt()

		val supplier = idToSupplierMap[packetId]
		requireNotNull(supplier) { "No supplier found for packetId $packetId" }

		val vsPacket: IVSPacket = supplier()
		vsPacket.read(byteBuf)
		return vsPacket
	}

	fun handleVSPacketClient(byteBuf: ByteBuf) {
		val vsPacket = readVSPacket(byteBuf)
		handleVSPacketClient(vsPacket)
	}

	fun handleVSPacketClient(vsPacket: IVSPacket) {
		val handler = classToClientHandlerMap[vsPacket::class.java]
		requireNotNull(handler) { "No client handler found for $vsPacket" }
		handler.handlePacket(vsPacket)
	}

	fun handleVSPacketServer(byteBuf: ByteBuf, sender: P) {
		val vsPacket = readVSPacket(byteBuf)

		val handler = classToServerHandlerMap[vsPacket::class.java]
		requireNotNull(handler) { "No server handler found for $vsPacket" }

		handler.handlePacket(vsPacket, sender)
	}
}