package org.valkyrienskies.core.physics.nodes.control

import org.valkyrienskies.core.physics.nodes.EngineNode

abstract class AbstractControlLoop : ControlLoop {
	private val _engineNodes = ArrayList<EngineNode>()
	override val engineNodes: List<EngineNode> get() = _engineNodes

	override fun addEngineNode(node: EngineNode) {
		onAddEngineNode(node)
		_engineNodes += node
	}

	override fun removeEngineNode(node: EngineNode) {
		onRemoveEngineNode(node)
		_engineNodes -= node
	}

	protected open fun onAddEngineNode(node: EngineNode) {}
	protected open fun onRemoveEngineNode(node: EngineNode) {}
}