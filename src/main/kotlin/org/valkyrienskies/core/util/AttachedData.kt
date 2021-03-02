package org.valkyrienskies.core.util

import com.google.common.collect.MapMaker
import java.util.concurrent.ConcurrentMap

private val attachedMap: ConcurrentMap<Any, AttachedData> = MapMaker().weakKeys().makeMap()

val Any.attached: AttachedData
	get() = attachedMap.computeIfAbsent(this) { AttachedData() }

class AttachedData {

	val internalMap: ConcurrentMap<Class<*>, Any> by lazy {
		MapMaker().makeMap<Class<*>, Any>()
	}

	inline fun <reified T> get(): T? {
		return internalMap[T::class.java] as T?
	}

	inline fun <reified T> getValue(): T {
		return get() ?: error("Tried to get attached data where none exists")
	}

	inline fun <reified T> set(data: T): T? {
		return internalMap.put(T::class.java, data) as T?
	}

	inline fun <reified T> remove(): T? {
		return internalMap.remove(T::class.java) as T?
	}

	inline fun <reified T> computeIfAbsent(supplier: () -> T): T {
		val attached = get<T>()

		return if (attached == null) {
			val data = supplier()
			set(data)
			data
		} else {
			attached
		}
	}
}
