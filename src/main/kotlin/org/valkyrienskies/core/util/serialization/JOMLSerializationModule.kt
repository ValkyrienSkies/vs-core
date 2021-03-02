package org.valkyrienskies.core.util.serialization

import com.fasterxml.jackson.databind.module.SimpleModule
import org.joml.Matrix3d
import org.joml.Matrix3dc
import org.joml.Matrix3f
import org.joml.Matrix3fc
import org.joml.Matrix4d
import org.joml.Matrix4dc
import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Quaternionf
import org.joml.Quaternionfc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f
import org.joml.Vector3fc
import org.joml.Vector3i
import org.joml.Vector3ic
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBf
import org.joml.primitives.AABBfc
import org.joml.primitives.AABBi
import org.joml.primitives.AABBic

class JOMLSerializationModule : SimpleModule() {
	init {
		addAbstractTypeMapping<Vector3ic, Vector3i>()
		addAbstractTypeMapping<Vector3fc, Vector3f>()
		addAbstractTypeMapping<Vector3dc, Vector3d>()

		addAbstractTypeMapping<Quaternionfc, Quaternionf>()
		addAbstractTypeMapping<Quaterniondc, Quaterniond>()

		addAbstractTypeMapping<Matrix4fc, Matrix4f>()
		addAbstractTypeMapping<Matrix4dc, Matrix4d>()

		addAbstractTypeMapping<Matrix3fc, Matrix3f>()
		addAbstractTypeMapping<Matrix3dc, Matrix3d>()

		addAbstractTypeMapping<AABBic, AABBi>()
		addAbstractTypeMapping<AABBfc, AABBf>()
		addAbstractTypeMapping<AABBdc, AABBd>()
	}

	private inline fun <reified A, reified B : A> addAbstractTypeMapping() {
		super.addAbstractTypeMapping(A::class.java, B::class.java)
	}
}