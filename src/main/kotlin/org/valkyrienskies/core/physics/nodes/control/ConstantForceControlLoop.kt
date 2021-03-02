package org.valkyrienskies.core.physics.nodes.control

import kotlin.math.max
import kotlin.math.min

class ConstantForceControlLoop(multiplier: Double) : AbstractControlLoop() {

	var multiplier = multiplier
		set(v) {
			field = max(0.0, min(v, 1.0))
			changed = true
		}

	private var changed = true

	override fun tick(deltaNs: Long) {
		if (changed) {
			this.engineNodes.forEach {
				it.currentForce = it.maxForce * multiplier
			}
		}
	}
}