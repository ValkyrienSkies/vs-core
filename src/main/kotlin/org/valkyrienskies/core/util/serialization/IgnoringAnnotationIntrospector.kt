package org.valkyrienskies.core.util.serialization

import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import org.valkyrienskies.core.util.mapToArray
import kotlin.reflect.KClass

/**
 * Will ignore fields annotated with specified annotations
 */
class IgnoringAnnotationIntrospector(
    /**
     * Fields with these annotations are ignored
     */
    private vararg val annotationsToIgnore: Class<out Annotation>
) : JacksonAnnotationIntrospector() {

    constructor(vararg annotationsToIgnore: KClass<out Annotation>) :
        this(*annotationsToIgnore.mapToArray { it.java })

    override fun hasIgnoreMarker(m: AnnotatedMember): Boolean {
        return m.hasOneOf(annotationsToIgnore) || super.hasIgnoreMarker(m)
    }
}
