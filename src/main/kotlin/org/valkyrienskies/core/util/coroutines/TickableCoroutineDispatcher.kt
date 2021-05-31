package org.valkyrienskies.core.util.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.CoroutineContext

class TickableCoroutineDispatcher : CoroutineDispatcher() {

    private val tasks = ConcurrentLinkedQueue<Runnable>()
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        tasks.add(block)
    }

    fun tick() {
        tasks.removeAll { it.run(); true }
    }
}
