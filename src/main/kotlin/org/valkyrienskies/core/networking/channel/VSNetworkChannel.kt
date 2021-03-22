package org.valkyrienskies.core.networking.channel

import io.netty.buffer.ByteBuf

interface VSNetworkChannel {

    fun send(data: ByteBuf)

    fun onReceive(handler: (data: ByteBuf) -> Unit)
}
