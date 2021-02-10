package org.valkyrienskies.core.chunk_tracking

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.game.IPlayer
import org.valkyrienskies.core.game.ShipTransform

class ShipChunkTracker<P : IPlayer>(
    private val shipActiveChunksSet: IShipActiveChunksSet,
    private var chunkWatchDistance: Double,
    private var chunkUnwatchDistance: Double
) :
    IShipChunkTracker<P> {

    private val playersWatchingChunkMap: Long2ObjectMap<Set<P>> = Long2ObjectOpenHashMap()
    private val chunkWatchTasks: List<ChunkWatchTask<P>> = listOf()
    private val chunkUnwatchTasks: List<ChunkUnwatchTask<P>> = listOf()

    fun updateChunkWatchDistance(newChunkWatchDistance: Double) {
        chunkWatchDistance = newChunkWatchDistance
    }

    fun updateChunkUnwatchDistance(newChunkUnwatchDistance: Double) {
        chunkUnwatchDistance = newChunkUnwatchDistance
    }

    override fun tick(players: Iterator<P>, shipTransform: ShipTransform) {
        val newChunkWatchTasks: MutableList<ChunkWatchTask<P>> = ArrayList()
        val newChunkUnwatchTasks: MutableList<ChunkUnwatchTask<P>> = ArrayList()

        // Reuse these vector objects across iterations
        val tempVector0 = Vector3d()
        val tempVector1 = Vector3d()
        shipActiveChunksSet.iterateChunkPos { chunkX, chunkZ ->
            run {
                val chunkPosInWorldCoordinates: Vector3dc = shipTransform.shipToWorldMatrix.transformPosition(
                    tempVector0.set(
                        ((chunkX shl 4) + 8).toDouble(),
                        127.0,
                        ((chunkZ shl 4) + 8).toDouble()
                    )
                )

                val newPlayersWatching: MutableList<P> = ArrayList()
                val newPlayersUnwatching: MutableList<P> = ArrayList()

                for (player in players) {
                    val playerPositionInWorldCoordinates: Vector3dc = player.getPosition(tempVector1)
                    val displacementDistanceSq =
                        chunkPosInWorldCoordinates.distanceSquared(playerPositionInWorldCoordinates)

                    val isPlayerWatchingThisChunk = isPlayerWatchingChunk(chunkX, chunkZ, player)

                    if (displacementDistanceSq < chunkWatchDistance * chunkWatchDistance) {
                        if (!isPlayerWatchingThisChunk) {
                            // Watch this chunk
                            newPlayersWatching.add(player)
                        }
                    } else if (displacementDistanceSq > chunkUnwatchDistance * chunkUnwatchDistance) {
                        if (isPlayerWatchingThisChunk) {
                            // Unwatch this chunk
                            newPlayersUnwatching.add(player)
                        }
                    }
                }

                val chunkPosAsLong = IShipActiveChunksSet.chunkPosToLong(chunkX, chunkZ)
                if (newPlayersWatching.isNotEmpty()) {
                    val newChunkWatchTask = ChunkWatchTask<P>(chunkPosAsLong, newPlayersWatching, 0.0)
                    newChunkWatchTasks.add(newChunkWatchTask)
                }
                if (newPlayersUnwatching.isNotEmpty()) {
                    val newChunkUnwatchTask = ChunkUnwatchTask<P>(chunkPosAsLong, newPlayersWatching)
                    newChunkUnwatchTasks.add(newChunkUnwatchTask)
                }
            }
        }
    }

    private fun isPlayerWatchingChunk(chunkX: Int, chunkZ: Int, player: P): Boolean {
        val chunkPosAsLong = IShipActiveChunksSet.chunkPosToLong(chunkX, chunkZ)
        val playersWatchingChunk = playersWatchingChunkMap[chunkPosAsLong]
        if (playersWatchingChunk != null) {
            return playersWatchingChunk.contains(player)
        }
        return false
    }

    override fun getPlayersWatchingChunk(chunkX: Int, chunkZ: Int): Iterator<P> {
        val chunkPosAsLong = IShipActiveChunksSet.chunkPosToLong(chunkX, chunkZ)
        val playersWatchingChunk = playersWatchingChunkMap[chunkPosAsLong]
        if (playersWatchingChunk != null) {
            return playersWatchingChunk.iterator()
        }
        return listOf<P>().iterator()
    }

    override fun getChunkWatchTasks(): Iterator<ChunkWatchTask<P>> {
        return chunkWatchTasks.iterator()
    }

    override fun getChunkUnwatchTasks(): Iterator<ChunkUnwatchTask<P>> {
        return chunkUnwatchTasks.iterator()
    }

}