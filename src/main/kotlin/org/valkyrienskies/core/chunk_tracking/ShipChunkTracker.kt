package org.valkyrienskies.core.chunk_tracking

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.game.IPlayer
import org.valkyrienskies.core.game.ShipTransform

class ShipChunkTracker(
    private val shipActiveChunksSet: IShipActiveChunksSet,
    private var chunkWatchDistance: Double,
    private var chunkUnwatchDistance: Double
) :
    IShipChunkTracker {

    private val playersWatchingChunkMap: Long2ObjectMap<Set<IPlayer>> = Long2ObjectOpenHashMap()
    private val chunkWatchTasks: List<ChunkWatchTask> = listOf()
    private val chunkUnwatchTasks: List<ChunkUnwatchTask> = listOf()

    fun updateChunkWatchDistance(newChunkWatchDistance: Double) {
        chunkWatchDistance = newChunkWatchDistance
    }

    fun updateChunkUnwatchDistance(newChunkUnwatchDistance: Double) {
        chunkUnwatchDistance = newChunkUnwatchDistance
    }

    override fun tick(players: Iterator<IPlayer>, removedPlayers: Iterator<IPlayer>, shipTransform: ShipTransform) {
        val newChunkWatchTasks: MutableList<ChunkWatchTask> = ArrayList()
        val newChunkUnwatchTasks: MutableList<ChunkUnwatchTask> = ArrayList()

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

                val newPlayersWatching: MutableList<IPlayer> = ArrayList()
                val newPlayersUnwatching: MutableList<IPlayer> = ArrayList()

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
                    val newChunkWatchTask = ChunkWatchTask(chunkPosAsLong, newPlayersWatching, 0.0)
                    newChunkWatchTasks.add(newChunkWatchTask)
                }
                if (newPlayersUnwatching.isNotEmpty()) {
                    val newChunkUnwatchTask = ChunkUnwatchTask(chunkPosAsLong, newPlayersWatching)
                    newChunkUnwatchTasks.add(newChunkUnwatchTask)
                }
            }
        }
    }

    private fun isPlayerWatchingChunk(chunkX: Int, chunkZ: Int, player: IPlayer): Boolean {
        val chunkPosAsLong = IShipActiveChunksSet.chunkPosToLong(chunkX, chunkZ)
        val playersWatchingChunk = playersWatchingChunkMap[chunkPosAsLong]
        if (playersWatchingChunk != null) {
            return playersWatchingChunk.contains(player)
        }
        return false
    }

    override fun getPlayersWatchingChunk(chunkX: Int, chunkZ: Int): Iterator<IPlayer> {
        val chunkPosAsLong = IShipActiveChunksSet.chunkPosToLong(chunkX, chunkZ)
        val playersWatchingChunk = playersWatchingChunkMap[chunkPosAsLong]
        if (playersWatchingChunk != null) {
            return playersWatchingChunk.iterator()
        }
        return listOf<IPlayer>().iterator()
    }

    override fun getChunkWatchTasks(): Iterator<ChunkWatchTask> {
        return chunkWatchTasks.iterator()
    }

    override fun getChunkUnwatchTasks(): Iterator<ChunkUnwatchTask> {
        return chunkUnwatchTasks.iterator()
    }

}