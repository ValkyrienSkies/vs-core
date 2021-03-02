package org.valkyrienskies.core.util

fun assertIsPhysicsThread() {
	assert(Thread.currentThread().name.startsWith("Physics Thread ")) { "Not called from physics thread" }
}