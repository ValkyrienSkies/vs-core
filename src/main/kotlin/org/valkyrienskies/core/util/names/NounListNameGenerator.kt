package org.valkyrienskies.core.util.names

import java.util.concurrent.ThreadLocalRandom
import java.util.stream.Collectors
import kotlin.streams.toList

/**
 * Generates names from a noun list
 */
object NounListNameGenerator : NameGenerator {
	private const val NOUN_LIST_LENGTH = 6801
	private const val DEFAULT_NOUNS_PER_NAME = 3

	private val nouns: List<String> = javaClass
		.classLoader!!
		.getResourceAsStream("nounlist.txt")!!
		.bufferedReader()
		.lines()
		.toList()

	override fun generateName(): String {
		return this.generateName(DEFAULT_NOUNS_PER_NAME)
	}

	fun generateName(numberOfNouns: Int): String {
		return ThreadLocalRandom.current()
			.ints(numberOfNouns.toLong(), 0, nouns.size)
			.mapToObj { index -> nouns[index] }
			.collect(Collectors.joining("-"))
	}
}