package org.valkyrienskies.core.game

import org.joml.*

/**
 * The [ShipTransform] class is responsible for transforming position and direction vectors between ship coordinates and world coordinates.
 *
 * The process of transforming a position transformation from ship coordinates to world coordinates is defined as the following:
 *
 * First it is translated by -[shipPositionInShipCoordinates],
 * then it is scaled by [shipCoordinatesToWorldCoordinatesScaling],
 * after that it is rotated by [shipCoordinatesToWorldCoordinatesRotation],
 * finally it is translated by [shipPositionInWorldCoordinates].
 */
data class ShipTransform(val shipPositionInWorldCoordinates: Vector3dc, val shipPositionInShipCoordinates: Vector3dc, val shipCoordinatesToWorldCoordinatesRotation: Quaterniondc, val shipCoordinatesToWorldCoordinatesScaling: Vector3dc) {

    /**
     * Transforms positions and directions from ship coordinates to world coordinates
     */
    val shipToWorldMatrix: Matrix4dc
    /**
     * Transforms positions and directions from world coordinates to ships coordinates
     */
    val worldToShipMatrix: Matrix4dc

    init {
        shipToWorldMatrix = Matrix4d()
            .translate(shipPositionInWorldCoordinates)
            .rotate(shipCoordinatesToWorldCoordinatesRotation)
            .scale(shipCoordinatesToWorldCoordinatesScaling)
            .translate(-shipPositionInShipCoordinates.x(), -shipPositionInShipCoordinates.y(), -shipPositionInShipCoordinates.z())
        worldToShipMatrix = shipToWorldMatrix.invert(Matrix4d())
    }

    companion object {
        // The quaternion that represents no rotation
        private val ZERO_ROTATION: Quaterniondc = Quaterniond()
        // The vector that represents no scaling
        private val UNIT_SCALING: Vector3dc = Vector3d(1.0, 1.0, 1.0)

        fun newShipTransformFromCoordinates(centerCoordinateInWorld: Vector3dc, centerCoordinateInShip: Vector3dc): ShipTransform {
            return newShipTransformFromCoordinatesAndRotation(centerCoordinateInWorld, centerCoordinateInShip, ZERO_ROTATION)
        }

        fun newShipTransformFromCoordinatesAndRotation(centerCoordinateInWorld: Vector3dc, centerCoordinateInShip: Vector3dc, shipRotation: Quaterniondc): ShipTransform {
            return ShipTransform(centerCoordinateInWorld, centerCoordinateInShip, shipRotation, UNIT_SCALING)
        }

        fun newShipTransformFromCoordinatesAndRotationAndScaling(centerCoordinateInWorld: Vector3dc, centerCoordinateInShip: Vector3dc, shipRotation: Quaterniondc, shipScaling: Vector3dc): ShipTransform {
            return ShipTransform(centerCoordinateInWorld, centerCoordinateInShip, shipRotation, shipScaling)
        }
    }

    fun transformDirectionNoScalingFromShipToWorld(directionInShip: Vector3dc, dest: Vector3d): Vector3d {
        return shipCoordinatesToWorldCoordinatesRotation.transform(directionInShip, dest)
    }

    fun transformDirectionNoScalingFromWorldToShip(directionInWorld: Vector3dc, dest: Vector3d): Vector3d {
        return shipCoordinatesToWorldCoordinatesRotation.transformInverse(directionInWorld, dest)
    }

}