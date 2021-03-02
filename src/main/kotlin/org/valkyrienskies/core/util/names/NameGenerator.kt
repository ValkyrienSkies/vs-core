package org.valkyrienskies.core.util.names

/**
 * Generates names for use of naming things. May or may not be
 * human-readable, that's implementation dependent.
 */
interface NameGenerator {
	fun generateName(): String
}