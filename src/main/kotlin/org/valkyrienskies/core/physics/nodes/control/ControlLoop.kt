package org.valkyrienskies.core.physics.nodes.control

import org.valkyrienskies.core.physics.nodes.EngineNode

interface ControlLoop {
	fun tick(deltaNs: Long)

	val engineNodes: List<EngineNode>

	fun addEngineNode(node: EngineNode)
	fun removeEngineNode(node: EngineNode)
}