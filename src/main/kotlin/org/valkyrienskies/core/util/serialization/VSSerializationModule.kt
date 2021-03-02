package org.valkyrienskies.core.util.serialization

import com.fasterxml.jackson.databind.module.SimpleModule
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet
import org.valkyrienskies.core.chunk_tracking.ShipActiveChunksSet
import org.valkyrienskies.core.datastructures.IBlockPosSet
import org.valkyrienskies.core.datastructures.IBlockPosSetAABB
import org.valkyrienskies.core.datastructures.SmallBlockPosSet
import org.valkyrienskies.core.datastructures.SmallBlockPosSetAABB

class VSSerializationModule : SimpleModule() {
	init {
		addAbstractTypeMapping<IBlockPosSet, SmallBlockPosSet>()
		addAbstractTypeMapping<IBlockPosSetAABB, SmallBlockPosSetAABB>()
		addAbstractTypeMapping<IShipActiveChunksSet, ShipActiveChunksSet>()
	}

	private inline fun <reified A, reified B : A> addAbstractTypeMapping() {
		super.addAbstractTypeMapping(A::class.java, B::class.java)
	}
}