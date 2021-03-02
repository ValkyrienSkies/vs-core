package org.valkyrienskies.core.physics

import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

// The number of physics ticks to be considered in the average tick time.
const val TICK_TIME_QUEUE: Long = 100
const val MAX_LOST_TIME_NS: Long = 1_000_000_000

private var threads = 0

class ServerPhysicsWorld(
	var nsPerTick: Long = 1_000_000_000 / 100,
	val physicsEngine: PhysicsEngine
) {
	private val latestTickTimes: Queue<Long> = ConcurrentLinkedQueue()

	private val recurringTaskQueue = ConcurrentLinkedQueue<(Long) -> Unit>()
	private val taskQueue = ConcurrentLinkedQueue<(Long) -> Unit>()

	private var thread: Thread? = null
	private val threadName get() = thread?.name
	private var isRunning = false

	fun start() {
		require(!isRunning) { "This world has already been started" }
		isRunning = true

		val thread = Thread(this::run, "Physics Thread ${threads++}")
		thread.start()

		this.thread = thread
	}

	fun stop() {
		require(isRunning) { "This world hasn't been started" }
		isRunning = false
		thread = null
	}

	private fun run() {
		// Used to make up for any lost time when we tick
		var lostTickTime: Long = 0
		var endOfPhysicsTickTimeNano = System.nanoTime()
		while (isRunning) {
			// Avoid concurrency issues
			val nsPerTick = nsPerTick

			val startOfPhysicsTickTimeNano = System.nanoTime()
			// Limit the tick smoothing to just one second (1000ms), if lostTickTime becomes
			// too large then physics would move too quickly after the lag source was
			// removed.
			if (lostTickTime > MAX_LOST_TIME_NS) {
				lostTickTime %= MAX_LOST_TIME_NS
			}
			// Run the physics code
			tick(startOfPhysicsTickTimeNano - endOfPhysicsTickTimeNano)
			endOfPhysicsTickTimeNano = System.nanoTime()
			val deltaPhysicsTickTimeNano = endOfPhysicsTickTimeNano - startOfPhysicsTickTimeNano
			try {
				var sleepTime: Long = nsPerTick - deltaPhysicsTickTimeNano
				// Sending a negative sleepTime would crash the thread.
				if (sleepTime > 0) {
					// If our lostTickTime is greater than zero then we're behind a few ticks, try
					// to make up for it by skipping sleep() time.
					if (sleepTime > lostTickTime) {
						sleepTime -= lostTickTime
						lostTickTime = 0
						Thread.sleep(sleepTime / 1000000L)
					} else {
						lostTickTime -= sleepTime
					}
				} else {
					// We were late in processing this tick, add it to the lost tick time.
					lostTickTime -= sleepTime
				}
			} catch (e: InterruptedException) {
				e.printStackTrace()
			}
			val endOfTickTimeFullNano = System.nanoTime()
			val deltaTickTimeFullNano = endOfTickTimeFullNano - startOfPhysicsTickTimeNano

			// Update the average tick time here:
			latestTickTimes.add(deltaTickTimeFullNano)
			if (latestTickTimes.size > TICK_TIME_QUEUE) {
				// Remove the head of this queue.
				latestTickTimes.poll()
			}
		}

		println("This thread has been terminated")
	}

	fun addRecurringTask(task: (Long) -> Unit) {
		recurringTaskQueue += task
	}

	fun removeRecurringTask(task: (Long) -> Unit) {
		recurringTaskQueue -= task
	}

	fun addScheduledTask(task: Runnable) {
		addScheduledTask { task.run() }
	}

	fun addScheduledTask(task: (Long) -> Unit) {
		taskQueue += task
	}

	fun removeScheduledTask(task: (Long) -> Unit) {
		taskQueue -= task
	}

	private fun tick(deltaNs: Long) {
		recurringTaskQueue.forEach { it(deltaNs) }
		taskQueue.removeAll { it(deltaNs); true }

		physicsEngine.tick(deltaNs)
	}
}