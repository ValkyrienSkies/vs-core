package org.valkyrienskies.core.physics.nodes.control

import org.valkyrienskies.core.physics.RigidBody
import org.valkyrienskies.core.physics.ServerPhysicsWorld

class RigidBodyEngineForceApplier(
	private val body: RigidBody<*>,
	private val controlLoop: AbstractControlLoop,
	private val physicsWorld: ServerPhysicsWorld
) {
	private var isStarted = false

	private val task = { deltaNs: Long ->
		controlLoop.tick(deltaNs)
		controlLoop.engineNodes.forEach { engine ->
			physicsWorld.physicsEngine.applyForce(body, engine.direction, engine.position)
		}
	}

	fun start() {
		require(!isStarted) { "Already started" }
		physicsWorld.addRecurringTask(task)
	}

	fun stop() {
		require(isStarted) { "Not started" }
		physicsWorld.removeRecurringTask(task)
	}
}