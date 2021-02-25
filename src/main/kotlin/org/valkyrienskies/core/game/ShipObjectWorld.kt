package org.valkyrienskies.core.game

import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.chunk_tracking.ChunkUnwatchTask
import org.valkyrienskies.core.chunk_tracking.ChunkWatchTask
import org.valkyrienskies.core.util.names.NounListNameGenerator
import java.util.*
import kotlin.collections.HashMap

/**
 * Manages all the [ShipObject]s in a world.
 */
class ShipObjectWorld(
    val queryableShipData: QueryableShipData,
    val chunkAllocator: ChunkAllocator
) {

    val uuidToShipObjectMap = HashMap<UUID, ShipObject>()

    private var lastPlayersSet: Set<IPlayer> = setOf()

    fun tickShips() {
        // For now, just make a [ShipObject] for every [ShipData]
        for (shipData in queryableShipData) {
            val shipID = shipData.shipUUID
            uuidToShipObjectMap.computeIfAbsent(shipID) { ShipObject(shipData) }
        }
    }

    /**
     * If the chunk at [chunkX], [chunkZ] is a ship chunk, then this returns the [IPlayer]s that are watching that ship chunk.
     *
     * If the chunk at [chunkX], [chunkZ] is not a ship chunk, then this returns nothing.
     */
    fun getIPlayersWatchingShipChunk(chunkX: Int, chunkZ: Int): Iterator<IPlayer> {
        // Check if this chunk potentially belongs to a ship
        if (chunkAllocator.isChunkInShipyard(chunkX, chunkZ)) {
            // Then look for the shipData that owns this chunk
            val shipDataManagingPos = queryableShipData.getShipDataFromChunkPos(chunkX, chunkZ)
            if (shipDataManagingPos != null) {
                // Then check if there exists a ShipObject for this ShipData
                val shipObjectManagingPos = uuidToShipObjectMap[shipDataManagingPos.shipUUID]
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
    fun tickShipChunkLoading(currentPlayers: Iterable<IPlayer>): Pair<Spliterator<ChunkWatchTask>, Spliterator<ChunkUnwatchTask>> {
        val removedPlayers = lastPlayersSet - currentPlayers
        lastPlayersSet = currentPlayers.toHashSet()

        val chunkWatchTasksSorted = TreeSet<ChunkWatchTask>()
        val chunkUnwatchTasksSorted = TreeSet<ChunkUnwatchTask>()

        for (shipObject in uuidToShipObjectMap.values) {
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

        val newShipData = ShipData.newEmptyShipData(
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

    fun onSetBlock(
        posX: Int,
        posY: Int,
        posZ: Int,
        blockType: VSBlockType,
        oldBlockMass: Double,
        newBlockMass: Double
    ) {
        // If there is a ShipData at this position, then tell it about the block update
        queryableShipData.getShipDataFromChunkPos(posX shr 4, posZ shr 4)
            ?.onSetBlock(posX, posY, posZ, blockType, oldBlockMass, newBlockMass)

        // TODO: Update the physics voxel world here
        // voxelWorld.onSetBlock(posX, posY, posZ, blockType)
    }

}