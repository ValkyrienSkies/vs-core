package org.valkyrienskies.core.physics.nodes.control

import org.joml.Vector3dc
import org.valkyrienskies.core.physics.RigidBody
import org.valkyrienskies.core.util.x
import org.valkyrienskies.core.util.y
import org.valkyrienskies.core.util.z
import java.util.ArrayDeque
import java.util.Deque
import kotlin.math.abs
import kotlin.math.sin

/**
 * I have no idea if this actually works, just copied instructions from
 * the wikipedia page and did not test it yet
 *
 * https://en.wikipedia.org/wiki/PID_controller#Control_loop_example
 */
class PIDVelocityControlLoop(
	private val target: VelocityTarget,
	private val rigidBody: RigidBody<*>,
	/**
	 * Activation function so that not all engines get the same multiplier
	 */
	private val activate: ((Double) -> Double) = { sin(it * 0.5) + 0.5 }
) : AbstractControlLoop() {

	/**
	 * A Deque of (Time, Error)
	 */
	private val errorHistory: Deque<IntegralTerm> = ArrayDeque()
	private val lastTimeNs get() = errorHistory.peekLast()?.timeNs ?: 0
	private val firstTimeNs get() = errorHistory.peekFirst()?.timeNs ?: 0

	override fun tick(deltaNs: Long) {
		// P, I, D coefficients respectively
		val kp = 1
		val ki = 1
		val kd = 1

		// this error function is just some random crap I came up with
		val error = absoluteDifference(rigidBody.angularVelocity, target.angularVelocity) +
			absoluteDifference(rigidBody.linearVelocity, target.linearVelocity)
		val time = lastTimeNs + deltaNs

		errorHistory.push(IntegralTerm(time, error))
		val output = (kp * error) + (ki * integrateError()) + kd * derivativeError()

		engineNodes.forEach { it.currentForce = it.maxForce * activate(output) }
	}

	private fun derivativeError(): Double {
		if (errorHistory.size < 2) return 0.0
		val iter = errorHistory.descendingIterator()
		val t1 = iter.next()
		val t2 = iter.next()

		return (t1.error - t2.error) / (t1.timeNs - t2.timeNs)
	}

	private fun integrateError() =
		errorHistory.fold(0.0) { acc, term -> acc + (term.error * (term.timeNs - firstTimeNs)) }

	data class IntegralTerm(val timeNs: Long, val error: Double)
}

private fun absoluteDifference(v1: Vector3dc, v2: Vector3dc): Double {
	return abs(v1.x - v2.x) + abs(v1.y - v2.y) + abs(v1.z - v2.z)
}