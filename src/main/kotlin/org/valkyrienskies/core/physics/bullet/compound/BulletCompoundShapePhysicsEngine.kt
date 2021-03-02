package org.valkyrienskies.core.physics.bullet.compound

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.*
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo
import com.google.common.primitives.Floats
import org.joml.Vector3dc
import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.physics.*
import org.valkyrienskies.core.util.*
import java.nio.ByteBuffer

/**
 * Implementation of PhysicsEngine using Bullet compound shapes and boxes.
 * Not efficient at all, just for POC
 */
class BulletCompoundShapePhysicsEngine : PhysicsEngine {

	companion object {
		fun iterateNonemptyVoxels(buffer: ByteBuffer, dimensions: Vector3ic, func: (Int, Int, Int) -> Unit) {
			iterateVoxels(buffer, dimensions) { x, y, z, exists ->
				if (exists) func(x, y, z)
			}
		}

		fun iterateVoxels(buffer: ByteBuffer, dimensions: Vector3ic, func: (Int, Int, Int, Boolean) -> Unit) {
			val temp = Vector3i()
			buffer.iterateBits { b, i ->
				if (i > dimensions.multiplyTerms()) {
					return@iterateBits
				}
				val (x, y, z) = unwrapIndex(i, dimensions, temp)

				func(x, y, z, b)
			}
		}
	}

	private val _rigidBodies = HashSet<RigidBody<*>>()
	override val rigidBodies: Set<RigidBody<*>> get() = _rigidBodies

	private val requestingUpdate = HashSet<RigidBody<*>>()

	private val bulletWorld: btDynamicsWorld
	private val constraintSolver: btConstraintSolver
	private val collisionConfig: btCollisionConfiguration
	private val collisionDispatcher: btCollisionDispatcher
	private val broadphase: btDbvtBroadphase

	private val unitBox = btBoxShape(Vector3(0.5f, 0.5f, 0.5f))

	private val temps = VectorTemps()

	init {
		collisionConfig = btDefaultCollisionConfiguration()
		broadphase = btDbvtBroadphase()

		constraintSolver = btSequentialImpulseConstraintSolver()
		collisionDispatcher = btCollisionDispatcher(collisionConfig)
		bulletWorld = btDiscreteDynamicsWorld(
			collisionDispatcher, broadphase,
			constraintSolver, collisionConfig
		)

		bulletWorld.gravity = Vector3(0f, -9.8f, 0f)
		bulletWorld.forceUpdateAllAabbs = false

		btGImpactCollisionAlgorithm.registerAlgorithm(collisionDispatcher)
	}

	override fun applyForce(body: RigidBody<*>, force: Vector3dc, position: Vector3dc) {
		getBullet(body).applyForce(force.toGDX(), position.toGDX())
	}

	override fun addCentralForce(body: RigidBody<*>, force: Vector3dc) {
		getBullet(body).applyCentralForce(force.toGDX())
	}

	override fun requestUpdate(body: RigidBody<*>) {
		requestingUpdate += body
	}

	private fun getBullet(body: RigidBody<*>): btRigidBody {
		return when (body.shape) {
			is VoxelShape -> body.attached.getValue<VoxelRigidBodyData>().bulletBody
			is CuboidShape -> error("Not supported yet")
		}
	}

	override fun addRigidBody(body: RigidBody<*>) {
		_rigidBodies += body

		@Suppress("UNCHECKED_CAST")
		when (body.shape) {
			is VoxelShape -> addVoxelRigidBody(body as RigidBody<VoxelShape>)
			is CuboidShape -> error("Not supported yet")
		}
	}

	override fun getPenetrationAndNormal(
		shape1: CollisionShape,
		shape2: CollisionShape,
		t: PenetrationAndNormal
	): PenetrationAndNormal? {
		val btObj1 = btGhostObject()
		btObj1.collisionShape = createCollisionShape(shape1)
		val btObj2 = btGhostObject()
		btObj2.collisionShape = createCollisionShape(shape2)

		val manifold = collisionDispatcher.getNewManifold(btObj1, btObj1)
		return manifold.contactPoints.stream()
			.min { o1, o2 -> Floats.compare(o1.distance, o2.distance) }
			.filter { it.distance <= 0 }
			.map {
				val normal = temps.v3GDX[0]
				it.getNormalWorldOnB(normal)

				t.normal set normal

				val position = temps.v3GDX[0]
				it.getPositionWorldOnB(position)

				t.position set position

				t.penetration = -(it.distance.toDouble())
				t
			}
			.orElse(null)
	}

	private fun createCollisionShape(shape: CollisionShape): btCollisionShape {
		return when (shape) {
			is VoxelShape -> createVoxelShape(shape)
			is CuboidShape -> error("")
		}
	}

	private fun createVoxelShape(shape: VoxelShape): btCompoundShape {
		val compoundShape = btCompoundShape()

		iterateNonemptyVoxels(shape.buffer, shape.dimensions) { x, y, z ->
			compoundShape.addChildShape(
				Matrix4().setTranslation(x.toFloat(), y.toFloat(), z.toFloat()),
				unitBox
			)
		}

		return compoundShape
	}

	private fun addVoxelRigidBody(body: RigidBody<VoxelShape>) {
		val mass = body.inertiaData.mass.toFloat()

		val compoundShape = createVoxelShape(body.shape)

		val bulletBody = btRigidBody(
			btRigidBodyConstructionInfo(
				mass, null, compoundShape, compoundShape.getLocalInertia(mass, Vector3())
			)
		)

		val data = VoxelRigidBodyData(bulletBody, compoundShape)
		body.attached.set(data)

		bulletWorld.addRigidBody(bulletBody)
	}

	override fun removeRigidBody(body: RigidBody<*>) {
		@Suppress("UNCHECKED_CAST")
		when (body.shape) {
			is VoxelShape -> removeVoxelRigidBody(body as RigidBody<VoxelShape>)
			is CuboidShape -> error("Not supported")
		}
	}

	private fun removeVoxelRigidBody(body: RigidBody<VoxelShape>) {
		val data = body.attached.getValue<VoxelRigidBodyData>()

		bulletWorld.removeRigidBody(data.bulletBody)
		data.compoundShape.dispose()
		data.bulletBody.dispose()
	}

	private fun updateVoxelRigidBody(body: RigidBody<VoxelShape>) {
		val data = body.attached.getValue<VoxelRigidBodyData>()
		val shape = body.shape

		shape.toAdd.forEach { index ->
			val (x, y, z) = unwrapIndex(index, shape.dimensions, temps.v3i[0])
			val transform = Matrix4().setTranslation(Vector3(x.toFloat(), y.toFloat(), z.toFloat()))
			data.compoundShape.addChildShape(transform, unitBox)
		}

		// Convert toRemove to set for better time complexity
		val toRemove = shape.toRemove.toSet()
		for (i in (data.compoundShape.numChildShapes - 1) downTo 0) {
			val transform = data.compoundShape.getChildTransform(i)
			val translation = temps.v3i[0] set transform.getTranslation(temps.v3GDX[0])
			val voxelIndex = wrapIndex(translation, shape.dimensions)
			if (toRemove.contains(voxelIndex)) {
				data.compoundShape.removeChildShapeByIndex(i)
			}
		}
	}

	override fun tick(deltaNs: Long) {
		requestingUpdate.removeAll { body ->
			when (body.shape) {
				is VoxelShape -> updateVoxelRigidBody(body as RigidBody<VoxelShape>)
				is CuboidShape -> {
				}
			}

			val bulletBody = getBullet(body)
			bulletBody.centerOfMassTransform
			bulletBody.linearVelocity set body.linearVelocity
			bulletBody.angularVelocity set body.angularVelocity
			bulletBody.worldTransform set body.shape.transform
			true
		}

		bulletWorld.stepSimulation(deltaNs / 1e9f)

		rigidBodies.forEach { body ->
			val bulletBody = getBullet(body)
			body.linearVelocity set bulletBody.linearVelocity
			body.angularVelocity set bulletBody.angularVelocity
			body.shape.transform set bulletBody.worldTransform
			body._totalTorque set bulletBody.totalTorque
		}
	}

	private interface RigidBodyData {
		val bulletBody: btRigidBody
	}

	private class VoxelRigidBodyData(
		override val bulletBody: btRigidBody,
		val compoundShape: btCompoundShape
	) : RigidBodyData
}

