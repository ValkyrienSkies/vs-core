package org.valkyrienskies.core.physics.nodes.control

import org.joml.Vector3dc

data class VelocityTarget(
	var linearVelocity: Vector3dc,
	var angularVelocity: Vector3dc
)