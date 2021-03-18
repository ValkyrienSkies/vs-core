package org.valkyrienskies.core.util

import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import org.joml.Matrix3dc
import org.joml.Matrix4d
import org.joml.Matrix4dc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.joml.Vector3ic

// region JOML

// Vector3ic
operator fun Vector3ic.component1() = x
operator fun Vector3ic.component2() = y
operator fun Vector3ic.component3() = z

val Vector3ic.x get() = x()
val Vector3ic.y get() = y()
val Vector3ic.z get() = z()

fun Vector3ic.multiplyTerms() = x * y * z

// Vector3dc
fun Vector3dc.toGDX() = Vector3() set this
infix fun Vector3.set(v: Vector3dc): Vector3 = also {
    x = v.x.toFloat()
    y = v.y.toFloat()
    z = v.z.toFloat()
}

operator fun Vector3dc.component1() = x
operator fun Vector3dc.component2() = y
operator fun Vector3dc.component3() = z

val Vector3dc.x get() = x()
val Vector3dc.y get() = y()
val Vector3dc.z get() = z()

fun Vector3dc.multiplyTerms() = x * y * z
fun Vector3dc.addTerms() = x + y + z
fun Vector3dc.horizontalLengthSq() = x * x + z * z

// Matrix4dc
fun Matrix4dc.toGDX() = Matrix4() set this
infix fun Matrix4d.set(m: Matrix4) = also { set(m.values) }

// Matrix3dc

fun Matrix3dc.toGDX() = Matrix3() set this

// endregion

// region GDX

// Vector3
infix fun Matrix3.set(m: Matrix3dc): Matrix3 = also { m.get(values) }

infix fun Vector3d.set(v: Vector3): Vector3d = also {
    x = v.x.toDouble()
    y = v.y.toDouble()
    z = v.z.toDouble()
}

infix fun Vector3i.set(v: Vector3): Vector3i = also {
    x = v.x.toInt()
    y = v.y.toInt()
    z = v.z.toInt()
}

// Matrix4

infix fun Matrix4.set(m: Matrix4dc): Matrix4 = also { m.get(values) }
