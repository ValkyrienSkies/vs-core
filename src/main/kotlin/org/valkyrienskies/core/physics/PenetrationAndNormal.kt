package org.valkyrienskies.core.physics

import org.joml.Vector3d

data class PenetrationAndNormal(
	var normal: Vector3d,
	var position: Vector3d,
	var penetration: Double
)