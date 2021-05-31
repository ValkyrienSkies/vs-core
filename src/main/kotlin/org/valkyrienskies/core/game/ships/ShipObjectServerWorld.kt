package org.valkyrienskies.core.game.ships

import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.chunk_tracking.ChunkUnwatchTask
import org.valkyrienskies.core.chunk_tracking.ChunkWatchTask
import org.valkyrienskies.core.game.ChunkAllocator
import org.valkyrienskies.core.game.ShipId
import org.valkyrienskies.core.game.bridge.IPlayer
import org.valkyrienskies.core.game.ships.networking.ShipObjectNetworkManagerServer
import org.valkyrienskies.core.util.names.NounListNameGenerator
import java.util.Collections
import java.util.Spliterator
import java.util.TreeSet

class ShipObjectServerWorld(
    override val queryableShipData: MutableQueryableShipDataServer,
    val chunkAllocator: ChunkAllocator
) : ShipObjectWorld(queryableShipData) {

    var lastPlayers: Set<IPlayer> = setOf()
        private set

    private val shipObjectMap = HashMap<ShipId, ShipObjectServer>()
    override val shipObjects: Map<ShipId, ShipObjectServer> = shipObjectMap

    private val networkManager = ShipObjectNetworkManagerServer(this)

    /**
     * Should be run on the physics thread
     */
    override fun tickShips() {
        super.tickShips()
        networkManager.tick()
        // For now, just make a [ShipObject] for every [ShipData]
        for (shipData in queryableShipData) {
            val shipID = shipData.id
            shipObjectMap.computeIfAbsent(shipID) { ShipObjectServer(shipData) }
        }
    }

    /**
     * If the chunk at [chunkX], [chunkZ] is a ship chunk, then this returns the [IPlayer]s that are watching that ship chunk.
     *
     * If the chunk at [chunkX], [chunkZ] is not a ship chunk, then this returns nothing.
     */
    fun getIPlayersWatchingShipChunk(chunkX: Int, chunkZ: Int): Iterator<IPlayer> {
        // Check if this chunk potentially belongs to a ship
        if (ChunkAllocator.isChunkInShipyard(chunkX, chunkZ)) {
            // Then look for the shipData that owns this chunk
            val shipDataManagingPos = queryableShipData.getShipDataFromChunkPos(chunkX, chunkZ)
            if (shipDataManagingPos != null) {
                // Then check if there exists a ShipObject for this ShipData
                val shipObjectManagingPos = shipObjects[shipDataManagingPos.id]
                if (shipObjectManagingPos != null) {
                    return shipObjectManagingPos.shipChunkTracker.getPlayersWatchingChunk(chunkX, chunkZ)
                }
            }
        }
        return Collections.emptyIterator()
    }

    /**
     * Determines which ship chunks should be watched/unwatched by the players.
     *
     * It only returns the tasks, it is up to the caller to execute the tasks; however they do not have to execute all of them.
     * It is up to the caller to decide which tasks to execute, and which ones to skip.
     */
    fun tickShipChunkLoading(
        currentPlayers: Iterable<IPlayer>
    ): Pair<Spliterator<ChunkWatchTask>, Spliterator<ChunkUnwatchTask>> {
        val removedPlayers = lastPlayers - currentPlayers
        lastPlayers = currentPlayers.toHashSet()

        val chunkWatchTasksSorted = TreeSet<ChunkWatchTask>()
        val chunkUnwatchTasksSorted = TreeSet<ChunkUnwatchTask>()

        for (shipObject in shipObjects.values) {
            shipObject.shipChunkTracker.tick(
                players = currentPlayers,
                removedPlayers = removedPlayers,
                shipTransform = shipObject.shipData.shipTransform
            )

            val chunkWatchTasks = shipObject.shipChunkTracker.getChunkWatchTasks()
            val chunkUnwatchTasks = shipObject.shipChunkTracker.getChunkUnwatchTasks()

            chunkWatchTasks.forEach { chunkWatchTasksSorted.add(it) }
            chunkUnwatchTasks.forEach { chunkUnwatchTasksSorted.add(it) }
        }

        return Pair(chunkWatchTasksSorted.spliterator(), chunkUnwatchTasksSorted.spliterator())
    }

    /**
     * Creates a new [ShipData] centered at the block at [blockPosInWorldCoordinates].
     *
     * If [createShipObjectImmediately] is true then a [ShipObject] will be created immediately.
     */
    fun createNewShipAtBlock(blockPosInWorldCoordinates: Vector3ic, createShipObjectImmediately: Boolean): ShipData {
        val chunkClaim = chunkAllocator.allocateNewChunkClaim()
        val shipName = NounListNameGenerator.generateName()

        val shipCenterInWorldCoordinates: Vector3dc = Vector3d(blockPosInWorldCoordinates).add(0.5, 0.5, 0.5)
        val blockPosInShipCoordinates: Vector3ic = chunkClaim.getCenterBlockCoordinates(Vector3i())
        val shipCenterInShipCoordinates: Vector3dc = Vector3d(blockPosInShipCoordinates).add(0.5, 0.5, 0.5)

        val newShipData = ShipData.createEmpty(
            name = shipName,
            chunkClaim = chunkClaim,
            shipCenterInWorldCoordinates = shipCenterInWorldCoordinates,
            shipCenterInShipCoordinates = shipCenterInShipCoordinates
        )

        queryableShipData.addShipData(newShipData)

        if (createShipObjectImmediately) {
            TODO("Not implemented")
        }

        return newShipData
    }
}
