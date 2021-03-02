package org.valkyrienskies.core.physics.bullet.compound

fun <T> bulletArrayToList(
	getSize: () -> Int,
	getElement: (Int) -> T
): List<T> {
	return object : AbstractList<T>() {
		override val size: Int get() = getSize()
		override fun get(index: Int): T = getElement(index)
	}
}