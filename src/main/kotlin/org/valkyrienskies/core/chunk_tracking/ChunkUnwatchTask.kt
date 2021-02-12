package org.valkyrienskies.core.chunk_tracking

import org.valkyrienskies.core.game.IPlayer
import kotlin.math.sign

/**
 * This task says that the chunk at [chunkPos] should no longer be watched by [playersNeedUnwatching].
 */
class ChunkUnwatchTask(
    private val chunkPos: Long,
    val playersNeedUnwatching: Iterable<IPlayer>,
    val distanceSqToClosestPlayer: Double,
    private val onExecute: () -> Unit
) : Comparable<ChunkUnwatchTask> {
    private var hasBeenExecuted = false

    fun getChunkX(): Int = IShipActiveChunksSet.longToChunkX(chunkPos)
    fun getChunkZ(): Int = IShipActiveChunksSet.longToChunkZ(chunkPos)

    /**
     * Call this after the chunk at [chunkPos] has been unwatched by [playersNeedUnwatching] to update the [ShipChunkTracker].
     */
    fun onExecuteChunkUnwatchTask() {
        require(hasBeenExecuted.not()) {
            "This ChunkUnwatchTask has already been executed!"
        }
        onExecute()
        hasBeenExecuted = hasBeenExecuted.not()
    }

    override fun compareTo(other: ChunkUnwatchTask): Int {
        return (distanceSqToClosestPlayer - other.distanceSqToClosestPlayer).sign.toInt()
    }
}
