package org.valkyrienskies.core.util.serialization

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object VSJacksonUtil {
    /**
     * Returns the default mapper for the standard Valkyrien Skies configuration for serializing
     * things, particularly [org.valkyrienskies.core.game.ShipData]
     */
    val defaultMapper = createDefaultMapper()

    fun createDefaultMapper(): ObjectMapper {
        return configure(CBORMapper())
    }

    /**
     * Configures the selected object mapper to use the standard Valkyrien Skies configuration for
     * serializing things, particularly [org.valkyrienskies.core.game.ShipData]
     *
     * @param mapper The ObjectMapper to configure
     */
    private fun configure(mapper: ObjectMapper): ObjectMapper {
        return mapper.registerModule(JOMLSerializationModule())
            .registerModule(VSSerializationModule())
            .setVisibility(
                mapper.visibilityChecker
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            )
            // Serialize Kotlin data types
            .registerKotlinModule()
    }
}
