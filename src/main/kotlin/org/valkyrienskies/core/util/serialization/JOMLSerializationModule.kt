package org.valkyrienskies.core.util.serialization

import com.fasterxml.jackson.databind.module.SimpleModule
import org.joml.*
import org.joml.primitives.*

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