package org.valkyrienskies.core.networking.channel

import io.netty.buffer.ByteBuf

abstract class AbstractVSNetworkChannel : VSNetworkChannel {

    private var receiveHandler: (data: ByteBuf) -> Unit = {}

    abstract override fun send(data: ByteBuf)

    protected fun receive(data: ByteBuf) =
        receiveHandler(data)

    override fun onReceive(handler: (data: ByteBuf) -> Unit) {
        this.receiveHandler = handler
    }
}
