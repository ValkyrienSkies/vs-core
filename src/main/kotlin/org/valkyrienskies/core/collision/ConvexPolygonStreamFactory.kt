package org.valkyrienskies.core.collision

import org.joml.Matrix4dc
import org.joml.primitives.AABBdc
import java.util.stream.Stream

object ConvexPolygonStreamFactory {

    /**
     * @return a [Stream] that doesn't create any new objects, it only uses the objects passed in through the arguments. The object returned by the stream will always be [transformedCuboidPolygon], but with its value set to the next value of [aabbStream] transformed by [transform].
     */
    fun createStream(
        aabbStream: Stream<AABBdc>, transform: Matrix4dc, transformedCuboidPolygon: TransformedCuboidPolygon
    ): Stream<ConvexPolygonc> {
        return aabbStream.map {
            transformedCuboidPolygon.setFromAABB(it, transform)
        }
    }
}
