package org.valkyrienskies.core.networking

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.valkyrienskies.core.game.bridge.IPlayer
import org.valkyrienskies.core.networking.VSNetworking.Packet
import java.util.function.IntFunction

private typealias ClientHandler = (packet: Packet) -> Unit
private typealias ServerHandler = (packet: Packet, player: IPlayer) -> Unit

object VSNetworking {
    /**
     * Valkyrien Skies UDP channel
     */
    val UDP = NetworkChannel()

    /**
     * Valkyrien Skies TCP channel
     *
     * Should be initialized by Forge or Fabric (see [NetworkChannel])
     */
    val TCP = NetworkChannel()

    init {
        registerUDP()
    }

    /**
     * Contains packets used by vs-core.
     */
    object Packets {
        /**
         * TCP Packet used as fallback when no UDP channel available
         */
        val TCP_UDP_FALLBACK = TCP.registerPacket("UDP fallback")

        val TCP_SHIP_DATA_CREATE = TCP.registerPacket("Ship data create")

        val TCP_SHIP_DATA_DELTA = TCP.registerPacket("Ship data delta update")

        val UDP_SHIP_TRANSFORM = UDP.registerPacket("Ship transform update")
    }

    fun registerUDP() {
        // For now, "UDP" just always uses TCP fallback cause lazy

        UDP.rawSendToClient = { data, player ->
            Packets.TCP_UDP_FALLBACK.sendToClient(data, player)
        }

        UDP.rawSendToServer = { data ->
            Packets.TCP_UDP_FALLBACK.sendToServer(data)
        }

        TCP.registerClientHandler(Packets.TCP_UDP_FALLBACK) { packet ->
            UDP.onReceiveClient(packet.data)
        }

        TCP.registerServerHandler(Packets.TCP_UDP_FALLBACK) { packet, player ->
            UDP.onReceiveServer(packet.data, player)
        }
    }

    data class PacketType(val channel: NetworkChannel, val id: Int, val name: String) {
        fun sendToServer(data: ByteBuf) =
            channel.sendToServer(Packet(this, data))

        fun sendToClient(data: ByteBuf, player: IPlayer) =
            channel.sendToClient(Packet(this, data), player)

        fun registerServerHandler(handler: ServerHandler) =
            channel.registerServerHandler(this, handler)

        fun registerClientHandler(handler: ClientHandler) =
            channel.registerClientHandler(this, handler)
    }

    data class Packet(val type: PacketType, val data: ByteBuf) {
        fun sendToServer() = type.sendToServer(data)
        fun sendToClient(player: IPlayer) = type.sendToClient(data, player)
    }

    /**
     * Before use: set [rawSendToServer] and [rawSendToClient], and ensure that [onReceiveServer] and [onReceiveClient]
     * are called appropriately when packets are received.
     */
    class NetworkChannel {

        private val packetTypes = ArrayList<PacketType>()
        private val serverHandlers = Int2ObjectOpenHashMap<MutableSet<ServerHandler>>()
        private val clientHandlers = Int2ObjectOpenHashMap<MutableSet<ClientHandler>>()
        private val globalServerHandlers = HashSet<ServerHandler>()
        private val globalClientHandlers = HashSet<ClientHandler>()

        /**
         * Allocate a new packet type. This should be always be called in the same order, on startup, on both server and
         * client. Otherwise packet IDs will not be correct.
         */
        fun registerPacket(name: String): PacketType {
            return PacketType(channel = this, id = packetTypes.size + 1, name)
                .also { packetTypes.add(it) }
        }

        fun registerGlobalServerHandler(handler: ServerHandler) {
            globalServerHandlers.add(handler)
        }

        fun registerGlobalClientHandler(handler: ClientHandler) {
            globalClientHandlers.add(handler)
        }

        fun registerServerHandler(packetType: PacketType, handler: ServerHandler) {
            serverHandlers.computeIfAbsent(packetType.id, IntFunction { HashSet() }).add(handler)
        }

        fun registerClientHandler(packetType: PacketType, handler: ClientHandler) {
            clientHandlers.computeIfAbsent(packetType.id, IntFunction { HashSet() }).add(handler)
        }

        /**
         * To be called by Forge or Fabric networking
         */
        fun onReceiveClient(data: ByteBuf) {
            val packet = bytesToPacket(data)
            globalClientHandlers.forEach { it(packet) }
            clientHandlers.get(packet.type.id)?.forEach { it(packet) }
        }

        /**
         * To be called by Forge or Fabric networking
         */
        fun onReceiveServer(data: ByteBuf, player: IPlayer) {
            val packet = bytesToPacket(data)
            globalServerHandlers.forEach { it(packet, player) }
            serverHandlers.get(packet.type.id)?.forEach { it(packet, player) }
        }

        private fun bytesToPacket(data: ByteBuf): Packet {
            val id = data.readInt()
            val type = packetTypes[id]
            return Packet(type, data)
        }

        private fun packetToBytes(packet: Packet): ByteBuf {
            val composite = Unpooled.compositeBuffer(2)
            val index = Unpooled.buffer(4).apply { writeInt(packet.type.id) }
            return composite.addComponents(true, index, packet.data)
        }

        fun sendToServer(packet: Packet) =
            rawSendToServer(packetToBytes(packet))

        fun sendToClient(packet: Packet, player: IPlayer) =
            rawSendToClient(packetToBytes(packet), player)

        /**
         * To be implemented by Forge or Fabric networking. Should not be called.
         */
        lateinit var rawSendToServer: (data: ByteBuf) -> Unit

        /**
         * To be implemented by Forge or Fabric networking. Should not be called.
         */
        lateinit var rawSendToClient: (data: ByteBuf, player: IPlayer) -> Unit
    }
}
