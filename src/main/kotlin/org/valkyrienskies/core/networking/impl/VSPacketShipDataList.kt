package org.valkyrienskies.core.networking.impl

import com.fasterxml.jackson.module.kotlin.readValue
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import org.valkyrienskies.core.game.ShipData
import org.valkyrienskies.core.networking.IVSPacket
import org.valkyrienskies.core.util.serialization.VSJacksonUtil
import java.io.InputStream
import java.io.OutputStream

class VSPacketShipDataList private constructor() : IVSPacket {

    lateinit var shipDataList: List<ShipData>
        private set

    override fun write(byteBuf: ByteBuf) {
        val byteBufAsOutputStream: OutputStream = ByteBufOutputStream(byteBuf)
        VSJacksonUtil.defaultMapper.writeValue(byteBufAsOutputStream, shipDataList)
    }

    override fun read(byteBuf: ByteBuf) {
        val byteBufAsInputStream: InputStream = ByteBufInputStream(byteBuf)
        shipDataList = VSJacksonUtil.defaultMapper.readValue(byteBufAsInputStream)
    }

    companion object {
        fun createVSPacketShipDataList(shipDataCollection: Iterator<ShipData>): VSPacketShipDataList {
            val packet = VSPacketShipDataList()
            packet.shipDataList = shipDataCollection.asSequence().toList()
            return packet
        }

        fun createEmptyVSPacketShipDataList(): VSPacketShipDataList {
            return VSPacketShipDataList()
        }
    }
}