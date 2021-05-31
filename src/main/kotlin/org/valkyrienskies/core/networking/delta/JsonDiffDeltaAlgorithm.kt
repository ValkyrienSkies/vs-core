package org.valkyrienskies.core.networking.delta

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonDiff
import com.flipkart.zjsonpatch.JsonPatch
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream

class JsonDiffDeltaAlgorithm(private val mapper: ObjectMapper) : DeltaAlgorithm<JsonNode> {

    override fun encode(old: JsonNode, new: JsonNode, dest: ByteBuf): ByteBuf {
        // Compute json-patch diff
        val diffJson = JsonDiff.asJson(old, new)

        // Write the diff as bytes into [dest]
        val bytes = mapper.writeValueAsBytes(diffJson)
        dest.writeInt(bytes.size)
        dest.writeBytes(bytes)

        return dest
    }

    override fun apply(old: JsonNode, delta: ByteBuf): JsonNode {
        val size = delta.readInt()
        val diffJson = mapper.readTree(ByteBufInputStream(delta, size))

        return JsonPatch.apply(old, diffJson)
    }
}
