package org.valkyrienskies.core.util.serialization

import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector

object VSAnnotationIntrospector : JacksonAnnotationIntrospector() {

	override fun hasIgnoreMarker(m: AnnotatedMember): Boolean {
		return if (m.hasAnnotation(VSPacketIgnore::class.java)) true else super.hasIgnoreMarker(m)
	}
}