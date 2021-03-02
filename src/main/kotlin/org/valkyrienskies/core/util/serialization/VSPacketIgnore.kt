package org.valkyrienskies.core.util.serialization

/**
 * This is just like [com.fasterxml.jackson.annotation.JsonIgnore], but the property is
 * specifically only ignored during VS network transmissions.
 */
@Target(
	AnnotationTarget.ANNOTATION_CLASS,
	AnnotationTarget.FUNCTION,
	AnnotationTarget.PROPERTY_GETTER,
	AnnotationTarget.PROPERTY_SETTER,
	AnnotationTarget.CONSTRUCTOR,
	AnnotationTarget.FIELD
)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class VSPacketIgnore
