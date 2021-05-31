package org.valkyrienskies.core

import org.joml.Matrix3d
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.primitives.AABBd
import org.valkyrienskies.core.chunk_tracking.ShipActiveChunksSet
import org.valkyrienskies.core.datastructures.IBlockPosSet
import org.valkyrienskies.core.datastructures.SmallBlockPosSet
import org.valkyrienskies.core.datastructures.SmallBlockPosSetAABB
import org.valkyrienskies.core.game.ChunkClaim
import org.valkyrienskies.core.game.ships.MutableQueryableShipDataServer
import org.valkyrienskies.core.game.ships.QueryableShipDataImpl
import org.valkyrienskies.core.game.ships.ShipData
import org.valkyrienskies.core.game.ships.ShipInertiaData
import org.valkyrienskies.core.game.ships.ShipPhysicsData
import org.valkyrienskies.core.game.ships.ShipTransform
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.random.asJavaRandom

/**
 * This singleton generates random objects for unit tests.
 */
internal object VSRandomUtils {

    /**
     * Seeded random for tests
     */
    val defaultRandom = Random(-945798382)

    /**
     * Shares the same seed and state as defaultRandom but it's java.util.Random
     * instead of kotlin.random.Random
     */
    val defaultRandomJava = defaultRandom.asJavaRandom()

    /**
     * Use this instead of random.nextDouble() to avoid overflow errors
     */
    @Suppress("WeakerAccess")
    fun randomDoubleNotCloseToLimit(random: Random = defaultRandom): Double {
        return random.nextDouble(-1000000.0, 1000000.0)
    }

    /**
     * Use this instead of random.nextInt() to avoid overflow errors
     */
    @Suppress("WeakerAccess")
    fun randomIntegerNotCloseToLimit(random: Random = defaultRandom): Int {
        return random.nextInt(-1000000, 1000000)
    }

    @Suppress("WeakerAccess")
    fun randomVector3d(random: Random = defaultRandom): Vector3d {
        return Vector3d(
            randomDoubleNotCloseToLimit(random),
            randomDoubleNotCloseToLimit(random),
            randomDoubleNotCloseToLimit(random)
        )
    }

    /**
     * Generates a random unit quaternion with a uniform distribution.
     */
    @Suppress("WeakerAccess")
    fun randomQuaterniond(random: Random = defaultRandom): Quaterniond {
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
    fun randomMatrix3d(random: Random = defaultRandom): Matrix3d {
        return Matrix3d().set(randomQuaterniond(random))
    }

    @Suppress("WeakerAccess")
    fun randomString(random: Random = defaultRandom, length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    @Suppress("WeakerAccess")
    fun randomChunkClaim(random: Random = defaultRandom): ChunkClaim {
        return ChunkClaim.getClaim(randomIntegerNotCloseToLimit(random), randomIntegerNotCloseToLimit(random))
    }

    @Suppress("WeakerAccess")
    fun randomShipPhysicsData(random: Random = defaultRandom): ShipPhysicsData {
        return ShipPhysicsData(randomVector3d(random), randomVector3d(random))
    }

    @Suppress("WeakerAccess")
    fun randomShipInertiaData(random: Random = defaultRandom): ShipInertiaData {
        return ShipInertiaData(randomVector3d(random), randomDoubleNotCloseToLimit(random), randomMatrix3d(random))
    }

    @Suppress("WeakerAccess")
    fun randomShipTransform(random: Random = defaultRandom): ShipTransform {
        val scalingMaxMagnitude = 10.0
        val randomScaling = Vector3d(
            random.nextDouble(-scalingMaxMagnitude, scalingMaxMagnitude),
            random.nextDouble(-scalingMaxMagnitude, scalingMaxMagnitude),
            random.nextDouble(-scalingMaxMagnitude, scalingMaxMagnitude)
        )
        return ShipTransform(randomVector3d(random), randomVector3d(random), randomQuaterniond(random), randomScaling)
    }

    @Suppress("WeakerAccess")
    fun randomAABBd(random: Random = defaultRandom): AABBd {
        return AABBd(
            randomDoubleNotCloseToLimit(random),
            randomDoubleNotCloseToLimit(random),
            randomDoubleNotCloseToLimit(random),
            randomDoubleNotCloseToLimit(random),
            randomDoubleNotCloseToLimit(random),
            randomDoubleNotCloseToLimit(random)
        ).correctBounds()
    }

    @Suppress("WeakerAccess")
    fun randomBlockPosSetAABB(random: Random = defaultRandom, size: Int): SmallBlockPosSetAABB {
        val centerX = randomIntegerNotCloseToLimit(random)
        val centerZ = randomIntegerNotCloseToLimit(random)
        val blockPosSet = SmallBlockPosSetAABB(centerX, 0, centerZ, 4096, 4096, 4096)
        fillBlockPosSet(random, blockPosSet, centerX, centerZ, size)
        return blockPosSet
    }

    @Suppress("WeakerAccess")
    fun randomBlockPosSet(random: Random = defaultRandom, size: Int): SmallBlockPosSet {
        val centerX = randomIntegerNotCloseToLimit(random)
        val centerZ = randomIntegerNotCloseToLimit(random)
        val blockPosSet = SmallBlockPosSet(centerX, centerZ)
        fillBlockPosSet(random, blockPosSet, centerX, centerZ, size)
        return blockPosSet
    }

    private fun fillBlockPosSet(
        random: Random = defaultRandom,
        blockPosSet: IBlockPosSet,
        centerX: Int,
        centerZ: Int,
        size: Int
    ) {
        for (i in 1 until size) {
            val x = random.nextInt(-2048, 2047) + centerX
            val y = random.nextInt(0, 255)
            val z = random.nextInt(-2048, 2047) + centerZ
            blockPosSet.add(x, y, z)
        }
    }

    @Suppress("WeakerAccess")
    fun randomShipData(random: Random = defaultRandom): ShipData {
        return ShipData(
            id = UUID.randomUUID(),
            name = randomString(random, random.nextInt(10)),
            chunkClaim = randomChunkClaim(random),
            physicsData = randomShipPhysicsData(random),
            inertiaData = randomShipInertiaData(random),
            shipTransform = randomShipTransform(random),
            prevTickShipTransform = randomShipTransform(random),
            shipAABB = randomAABBd(random),
            shipActiveChunksSet = randomShipActiveChunkSet(random, random.nextInt(100))
        )
    }

    @Suppress("WeakerAccess")
    fun randomQueryableShipData(random: Random = defaultRandom, size: Int): MutableQueryableShipDataServer {
        val queryableShipData = QueryableShipDataImpl<ShipData>()
        for (i in 1..size) {
            queryableShipData.addShipData(randomShipData(random))
        }
        return queryableShipData
    }

    @Suppress("WeakerAccess")
    fun randomShipActiveChunkSet(random: Random = defaultRandom, size: Int): ShipActiveChunksSet {
        val shipActiveChunkSet = ShipActiveChunksSet.create()
        for (i in 1..size) {
            shipActiveChunkSet.addChunkPos(randomIntegerNotCloseToLimit(random), randomIntegerNotCloseToLimit(random))
        }
        return shipActiveChunkSet
    }
}
