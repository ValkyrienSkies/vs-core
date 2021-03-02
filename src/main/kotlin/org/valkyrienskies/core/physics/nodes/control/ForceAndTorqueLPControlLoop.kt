package org.valkyrienskies.core.physics.nodes.control

import org.apache.commons.math3.optim.MaxIter
import org.apache.commons.math3.optim.PointValuePair
import org.apache.commons.math3.optim.linear.LinearConstraint
import org.apache.commons.math3.optim.linear.LinearConstraintSet
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction
import org.apache.commons.math3.optim.linear.Relationship.GEQ
import org.apache.commons.math3.optim.linear.Relationship.LEQ
import org.apache.commons.math3.optim.linear.SimplexSolver
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.physics.RigidBody
import org.valkyrienskies.core.physics.nodes.EngineNode
import org.valkyrienskies.core.util.x
import org.valkyrienskies.core.util.y
import org.valkyrienskies.core.util.z
import java.util.stream.DoubleStream

/**
 * Solving the jet select problem with the simplex method.
 * Takes some engine nodes and produces the most combination that gets closest
 * target force and torque.
 *
 * In other words, minimizes the difference between the ideal force and torque and the actual
 */
class ForceAndTorqueLPControlLoop(
	/**
	 * Do not modify outside of physics thread
	 */
	var targetTorque: Vector3dc,
	/**
	 * Do not modify outside of physics thread
	 */
	var targetForce: Vector3dc,
	private val rigidBody: RigidBody<*>,
	/**
	 * Do not modify outside of physics thread
	 */
	var maxIterations: Int = 100
) : AbstractControlLoop() {

	// Additional reading:
	// Absolute values in linear programs: http://lpsolve.sourceforge.net/5.1/absolute.htm
	// Resultant force: https://en.wikipedia.org/wiki/Resultant_force
	override fun tick(deltaNs: Long) {
		// Number of engine nodes (n)

		// Using the associated torque algorithm and the net force,
		// we want minimize the difference between the ideal force and torque and the actual
		// I'm going to do this by minimizing this equation:
		// (T_i = ideal torque, T = actual torque, F_i = ideal force, F = actual force)
		// abs(T_i_x - T_x) + abs(T_i_y - T_y) + abs(T_i_z - T_z) +
		// abs(F_i_x - F_x) + abs(F_i_y - F_y) + abs(F_i_z - F_z)

		// Actually, inside of each of these absolute value is
		// F_i_x - (x1 * F_x_1 + x2 * F_x_2 + x3 * F_x_3)
		// where x1, x2, x3, etc. are the variables being changed by the optimizer (engine output)

		// Absolute value (see above) is represented as a constraint like so
		// and replaced with t1...t6 in objective equation:

		// F_i_x - (x1 * F_x1 + x2 * F_x2 + x3 * F_x3) <= t1
		// (x1 * F_x1 + x2 * F_x2 + x3 * F_x3) - F_i_x <= t1
		// or
		// t1 + (x1 * F_x1 + x2 * F_x2 + x3 * F_x3) >= F_i_x
		// -t1 + (x1 * F_x1 + x2 * F_x2 + x3 * F_x3) <= F_i_x

		// We use the simplex method to minimize it. xi refers to the variable being optimized (engine output)

		// F = sum of ( xi * Fi )
		// R_i = position of engine
		// R = center of mass
		// T = sum of ( (Ri - R) * xi * Fi )

		// The first 6 coefficients are t1, t2, ..., t6; they all equal 1
		// The next n coefficients (x1, x2, ..., xn) are the force multipliers for each engine
		val constraints = ArrayList<LinearConstraint>()

		// Let's generate the absolute value constraints for F_x
		// => (1 * t1 + 0 * t2 + 0 * t3 + 0 * t4 + 0 * t5 + 0 * t6) + (x1 * F_x1 + x2 * F_x2 + x3 * F_x3) >= F_i_x
		// => (-1 * t1 + 0 * t2 + 0 * t3 + 0 * t4 + 0 * t5 + 0 * t6) + (x1 * F_x1 + x2 * F_x2 + x3 * F_x3) <= F_i_x
		addAbsoluteValueConstraints(0, { it.direction.x * it.maxForce }, targetForce.x, constraints)
		// Let's generate the absolute value constraints for F_y
		addAbsoluteValueConstraints(1, { it.direction.y * it.maxForce }, targetForce.y, constraints)
		// Let's generate the absolute value constraints for F_z
		addAbsoluteValueConstraints(2, { it.direction.z * it.maxForce }, targetForce.z, constraints)

		// Let's generate the absolute value constraints for T_x
		// => (0 * t1 + 0 * t2 + 0 * t3 + 1 * t4 + 0 * t5 + 0 * t6) +
		// (x1 * (R1 - R) * F_x1 + x2 * (R2 - R) * F_x2)) <= T_i_x
		addAbsoluteValueConstraints(3, { engine -> getTorque(engine) { it.x } }, targetTorque.x, constraints)
		// Let's generate the absolute value constraints for T_y
		addAbsoluteValueConstraints(4, { engine -> getTorque(engine) { it.y } }, targetTorque.y, constraints)
		// Let's generate the absolute value constraints for T_z
		addAbsoluteValueConstraints(5, { engine -> getTorque(engine) { it.z } }, targetTorque.z, constraints)

		// Let's generate the constraints for each engine coefficient, e.g. 0 <= x <= 1
		engineNodes.forEachIndexed { i, _ ->
			val coefficients = DoubleArray(6 + engineNodes.size)
			coefficients[i + 6] = 1.0

			constraints += LinearConstraint(coefficients, GEQ, 0.0)
			constraints += LinearConstraint(coefficients, LEQ, 1.0)
		}

		// The objective is t1 + t2 + t3 + t4 + t5 + t6 + (0 * everything else)
		val objective = DoubleArray(6 + engineNodes.size)
		objective.fill(1.0, 0, 6)

		val function = LinearObjectiveFunction(objective, 0.0)
		val solver = SimplexSolver()

		val solution = solver.optimize(
			MaxIter(maxIterations),
			function,
			LinearConstraintSet(constraints),
			GoalType.MINIMIZE
		)

		engineNodes.forEachIndexed { i, engine ->
			engine.currentForce = solution.pointRef[i + 6] * engine.maxForce
		}

		printDebugInfo(solution)
	}

	private fun printDebugInfo(solution: PointValuePair) {
		println("Engine nodes: ${engineNodes.map { it.currentForce }}")
		println("Error: ${solution.value}")

		val tmp = Vector3d()
		val actualForce = engineNodes.fold(Vector3d()) { acc, node ->
			acc.add(node.direction.mul(node.currentForce, tmp))
		}
		println("Expected force: $targetForce")
		println("Actual force: $actualForce")
	}

	private inline fun getTorque(engine: EngineNode, component: (Vector3dc) -> Double): Double {
		// Point of application, R = center of mass
		val r = rigidBody.inertiaData.centerOfMass

		// Return (Ri - R) * Fi * Fm
		return (component(engine.position) - component(r)) * component(engine.direction) * engine.maxForce
	}

	private fun addAbsoluteValueConstraints(
		offset: Int,
		componentExtractor: (EngineNode) -> Double,
		ideal: Double,
		constraints: MutableCollection<LinearConstraint>
	) {
		val coefficients = DoubleStream.concat(
			DoubleStream.of(*DoubleArray(6)),
			engineNodes.stream().mapToDouble(componentExtractor)
		).toArray()

		// Linear constraint copies the array
		coefficients[offset] = 1.0
		constraints += LinearConstraint(coefficients, GEQ, ideal)

		coefficients[offset] = -1.0
		constraints += LinearConstraint(coefficients, LEQ, ideal)
	}
}