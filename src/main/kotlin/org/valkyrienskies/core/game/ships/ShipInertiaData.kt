package org.valkyrienskies.core.game.ships

import org.joml.Matrix3d
import org.joml.Matrix3dc
import org.joml.Vector3d
import org.joml.Vector3dc
import kotlin.math.abs

/**
 * This class keeps track of a ships mass, center of mass, and moment of inertia given the block changes within the ship.
 */
data class ShipInertiaData(
	private val centerOfMassInShipSpace: Vector3d,
	private var shipMass: Double,
	private val momentOfInertiaTensor: Matrix3d
) {

	internal fun onSetBlock(posX: Int, posY: Int, posZ: Int, oldBlockMass: Double, newBlockMass: Double) {
		val deltaBlockMass = newBlockMass - oldBlockMass
		if (abs(deltaBlockMass) < EPSILON) {
			return // No change in mass
		}

		// Add point masses at each of the 8 points we place mass at
		val addedMassAtEachPoint = deltaBlockMass / 8.0

		addMassAt(posX + INERTIA_OFFSET, posY + INERTIA_OFFSET, posZ + INERTIA_OFFSET, addedMassAtEachPoint)
		addMassAt(posX + INERTIA_OFFSET, posY + INERTIA_OFFSET, posZ - INERTIA_OFFSET, addedMassAtEachPoint)
		addMassAt(posX + INERTIA_OFFSET, posY - INERTIA_OFFSET, posZ + INERTIA_OFFSET, addedMassAtEachPoint)
		addMassAt(posX + INERTIA_OFFSET, posY - INERTIA_OFFSET, posZ - INERTIA_OFFSET, addedMassAtEachPoint)
		addMassAt(posX - INERTIA_OFFSET, posY + INERTIA_OFFSET, posZ + INERTIA_OFFSET, addedMassAtEachPoint)
		addMassAt(posX - INERTIA_OFFSET, posY + INERTIA_OFFSET, posZ - INERTIA_OFFSET, addedMassAtEachPoint)
		addMassAt(posX - INERTIA_OFFSET, posY - INERTIA_OFFSET, posZ + INERTIA_OFFSET, addedMassAtEachPoint)
		addMassAt(posX - INERTIA_OFFSET, posY - INERTIA_OFFSET, posZ - INERTIA_OFFSET, addedMassAtEachPoint)
	}

	/**
	 * Updates the center of mass and rotation inertia tensor matrix of the ShipInertiaData, using the rigid body
	 * inertia tensor equations.
	 *
	 * Reference [http://www.kwon3d.com/theory/moi/triten.html] equations 13 & 14.
	 */
	private fun addMassAt(x: Double, y: Double, z: Double, addedMass: Double) {
		// Put the moment of inertia tensor into a double array
		val gameMoITensor = DoubleArray(9)
		val transposed: Matrix3d = getMomentOfInertiaTensor().transpose(Matrix3d())
		transposed.get(gameMoITensor)

		val gameTickMass: Double = getShipMass()
		val prevCenterOfMass = Vector3d(getCenterOfMassInShipSpace())
		if (gameTickMass > EPSILON) {
			val newCenterOfMass: Vector3d = getCenterOfMassInShipSpace().mul(gameTickMass, Vector3d())
			newCenterOfMass.add(x * addedMass, y * addedMass, z * addedMass)
			newCenterOfMass.mul(1.0 / (gameTickMass + addedMass))
			centerOfMassInShipSpace.set(newCenterOfMass)
		} else {
			centerOfMassInShipSpace.set(x, y, z)
			momentOfInertiaTensor.zero()
		}

		// This code is pretty awful in hindsight, but it gets the job done.
		val cmShiftX: Double = prevCenterOfMass.x - getCenterOfMassInShipSpace().x()
		val cmShiftY: Double = prevCenterOfMass.y - getCenterOfMassInShipSpace().y()
		val cmShiftZ: Double = prevCenterOfMass.z - getCenterOfMassInShipSpace().z()
		val rx: Double = x - getCenterOfMassInShipSpace().x()
		val ry: Double = y - getCenterOfMassInShipSpace().y()
		val rz: Double = z - getCenterOfMassInShipSpace().z()
		gameMoITensor[0] =
			gameMoITensor[0] + (cmShiftY * cmShiftY + cmShiftZ * cmShiftZ) * gameTickMass + (ry * ry + rz * rz) * addedMass
		gameMoITensor[1] = gameMoITensor[1] - cmShiftX * cmShiftY * gameTickMass - rx * ry * addedMass
		gameMoITensor[2] = gameMoITensor[2] - cmShiftX * cmShiftZ * gameTickMass - rx * rz * addedMass
		gameMoITensor[3] = gameMoITensor[1]
		gameMoITensor[4] =
			gameMoITensor[4] + (cmShiftX * cmShiftX + cmShiftZ * cmShiftZ) * gameTickMass + (rx * rx + rz * rz) * addedMass
		gameMoITensor[5] = gameMoITensor[5] - cmShiftY * cmShiftZ * gameTickMass - ry * rz * addedMass
		gameMoITensor[6] = gameMoITensor[2]
		gameMoITensor[7] = gameMoITensor[5]
		gameMoITensor[8] =
			gameMoITensor[8] + (cmShiftX * cmShiftX + cmShiftY * cmShiftY) * gameTickMass + (rx * rx + ry * ry) * addedMass
		momentOfInertiaTensor.set(gameMoITensor).transpose()

		// Do this to avoid a mass of zero, which runs the risk of dividing by zero and
		// crashing the program.
		if (getShipMass() + addedMass > EPSILON) {
			shipMass += addedMass
		}
	}

	// region Getters
	fun getCenterOfMassInShipSpace(): Vector3dc {
		return centerOfMassInShipSpace
	}

	fun getShipMass(): Double {
		return shipMass
	}

	fun getMomentOfInertiaTensor(): Matrix3dc {
		return momentOfInertiaTensor
	}
	// endregion

	companion object {
		internal fun newEmptyShipInertiaData(): ShipInertiaData {
			return ShipInertiaData(Vector3d(), 0.0, Matrix3d())
		}

		/**
		 * Consider 2 doubles to be equal if the distance between them is less than this.
		 */
		private const val EPSILON = 1.0e-6

		/**
		 * We define the inertia of a cube as being 8 point masses within a cube.
		 *
		 * The positions of these points are the corners of a different cube with radius [INERTIA_OFFSET].
		 */
		private const val INERTIA_OFFSET = 0.4
	}
}