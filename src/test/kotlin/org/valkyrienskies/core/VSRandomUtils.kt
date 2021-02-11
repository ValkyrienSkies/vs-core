package org.valkyrienskies.core

import org.joml.Matrix3d
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.primitives.AABBd
import org.valkyrienskies.core.chunk_tracking.ShipActiveChunksSet
import org.valkyrienskies.core.datastructures.IBlockPosSet
import org.valkyrienskies.core.datastructures.SmallBlockPosSet
import org.valkyrienskies.core.datastructures.SmallBlockPosSetAABB
import org.valkyrienskies.core.game.*
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * This singleton generates random objects for unit tests.
 */
internal object VSRandomUtils {

    /**
     * Use this instead of random.nextDouble() to avoid overflow errors
     */
    @Suppress("WeakerAccess")
    fun randomDoubleNotCloseToLimit(random: Random = Random.Default): Double {
        return random.nextDouble(-1000000.0, 1000000.0)
    }

    /**
     * Use this instead of random.nextInt() to avoid overflow errors
     */
    @Suppress("WeakerAccess")
    fun randomIntegerNotCloseToLimit(random: Random = Random.Default): Int {
        return random.nextInt(-1000000, 1000000)
    }

    @Suppress("WeakerAccess")
    fun randomVector3d(random: Random = Random.Default): Vector3d {
        return Vector3d(randomDoubleNotCloseToLimit(random), randomDoubleNotCloseToLimit(random), randomDoubleNotCloseToLimit(random))
    }

    /**
     * Generates a random unit quaternion with a uniform distribution.
     */
    @Suppress("WeakerAccess")
    fun randomQuaterniond(random: Random = Random.Default): Quaterniond {
        // First generate a random unit vector
        // We use the gaussian distribution to make the random unit vector distribution uniform
        val gaussianRandomGenerator = ThreadLocalRandom.current()
        var randX = gaussianRandomGenerator.nextGaussian()
        var randY = gaussianRandomGenerator.nextGaussian()
        var randZ = gaussianRandomGenerator.nextGaussian()
        val normalizationConstant = sqrt(randX * randX + randY * randY + randZ * randZ)

        // Edge case
        if (normalizationConstant < 1.0e-6) {
            return Quaterniond()
        }

        // Then normalize these to form a unit vector
        randX /= normalizationConstant
        randY /= normalizationConstant
        randZ /= normalizationConstant

        // Then generate a random rotation degree
        val randomDegrees = random.nextDouble(360.0)

        // Finally generate a quaternion from the random axis and random angle
        return Quaterniond().fromAxisAngleDeg(randX, randY, randZ, randomDegrees)
    }

    @Suppress("WeakerAccess")
    fun randomMatrix3d(random: Random = Random.Default): Matrix3d {
        return Matrix3d().set(randomQuaterniond(random))
    }

    @Suppress("WeakerAccess")
    fun randomString(random: Random = Random.Default, length: Int): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map{random.nextInt(0, charPool.size)}
            .map(charPool::get)
            .joinToString("")
    }

    @Suppress("WeakerAccess")
    fun randomChunkClaim(random: Random = Random.Default): ChunkClaim {
        return ChunkClaim.getClaim(randomIntegerNotCloseToLimit(random), randomIntegerNotCloseToLimit(random))
    }

    @Suppress("WeakerAccess")
    fun randomShipPhysicsData(random: Random = Random.Default): ShipPhysicsData {
        return ShipPhysicsData(randomVector3d(random), randomVector3d(random))
    }

    @Suppress("WeakerAccess")
    fun randomShipInertiaData(random: Random = Random.Default): ShipInertiaData {
        return ShipInertiaData(randomVector3d(random), randomDoubleNotCloseToLimit(random), randomMatrix3d(random))
    }

    @Suppress("WeakerAccess")
    fun randomShipTransform(random: Random = Random.Default): ShipTransform {
        val scalingMaxMagnitude = 10.0
        val randomScaling = Vector3d(random.nextDouble(-scalingMaxMagnitude, scalingMaxMagnitude), random.nextDouble(-scalingMaxMagnitude, scalingMaxMagnitude), random.nextDouble(-scalingMaxMagnitude, scalingMaxMagnitude))
        return ShipTransform(randomVector3d(random), randomVector3d(random), randomQuaterniond(random), randomScaling)
    }

    @Suppress("WeakerAccess")
    fun randomAABBd(random: Random = Random.Default): AABBd {
        return AABBd(randomDoubleNotCloseToLimit(random), randomDoubleNotCloseToLimit(random), randomDoubleNotCloseToLimit(random), randomDoubleNotCloseToLimit(random), randomDoubleNotCloseToLimit(random), randomDoubleNotCloseToLimit(random)).correctBounds()
    }

    @Suppress("WeakerAccess")
    fun randomBlockPosSetAABB(random: Random = Random.Default, size: Int): SmallBlockPosSetAABB {
        val centerX = randomIntegerNotCloseToLimit(random)
        val centerZ = randomIntegerNotCloseToLimit(random)
        val blockPosSet = SmallBlockPosSetAABB(centerX, 0, centerZ, 4096, 4096, 4096)
        fillBlockPosSet(random, blockPosSet, centerX, centerZ, size)
        return blockPosSet
    }

    @Suppress("WeakerAccess")
    fun randomBlockPosSet(random: Random = Random.Default, size: Int): SmallBlockPosSet {
        val centerX = randomIntegerNotCloseToLimit(random)
        val centerZ = randomIntegerNotCloseToLimit(random)
        val blockPosSet = SmallBlockPosSet(centerX, centerZ)
        fillBlockPosSet(random, blockPosSet, centerX, centerZ, size)
        return blockPosSet
    }

    private fun fillBlockPosSet(random: Random = Random.Default, blockPosSet: IBlockPosSet, centerX: Int, centerZ: Int, size: Int) {
        for (i in 1 until size) {
            val x = random.nextInt(-2048, 2047) + centerX
            val y = random.nextInt(0, 255)
            val z = random.nextInt(-2048, 2047) + centerZ
            blockPosSet.add(x, y, z)
        }
    }

    @Suppress("WeakerAccess")
    fun randomShipData(random: Random = Random.Default): ShipData {
        return ShipData(
            shipUUID = UUID.randomUUID(),
            name = randomString(random, random.nextInt(10)),
            chunkClaim = randomChunkClaim(random),
            physicsData = randomShipPhysicsData(random),
            inertiaData = randomShipInertiaData(random),
            shipTransform = randomShipTransform(random),
            prevTickShipTransform = randomShipTransform(random),
            shipAABB = randomAABBd(random),
            blockPositionSet = randomBlockPosSetAABB(random, random.nextInt(100)),
            shipActiveChunksSet = randomShipActiveChunkSet(random, random.nextInt(100))
        )
    }

    @Suppress("WeakerAccess")
    fun randomQueryableShipData(random: Random = Random.Default, size: Int): QueryableShipData {
        val queryableShipData = QueryableShipData()
        for (i in 1 .. size) {
            queryableShipData.addShipData(randomShipData(random))
        }
        return queryableShipData
    }

    @Suppress("WeakerAccess")
    fun randomShipActiveChunkSet(random: Random = Random.Default, size: Int): ShipActiveChunksSet {
        val shipActiveChunkSet = ShipActiveChunksSet.createNewShipActiveChunkSet()
        for (i in 1 .. size) {
            shipActiveChunkSet.addChunkPos(randomIntegerNotCloseToLimit(random), randomIntegerNotCloseToLimit(random))
        }
        return shipActiveChunkSet
    }

}