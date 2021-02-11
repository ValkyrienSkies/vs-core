package org.valkyrienskies.core.util.serialization

import com.fasterxml.jackson.databind.module.SimpleModule
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet
import org.valkyrienskies.core.chunk_tracking.ShipActiveChunksSet
import org.valkyrienskies.core.datastructures.IBlockPosSet
import org.valkyrienskies.core.datastructures.IBlockPosSetAABB
import org.valkyrienskies.core.datastructures.SmallBlockPosSet
import org.valkyrienskies.core.datastructures.SmallBlockPosSetAABB

class VSSerializationModule: SimpleModule() {
    init {
        super.addAbstractTypeMapping(IBlockPosSet::class.java, SmallBlockPosSet::class.java)
        super.addAbstractTypeMapping(IBlockPosSetAABB::class.java, SmallBlockPosSetAABB::class.java)
        super.addAbstractTypeMapping(IShipActiveChunksSet::class.java, ShipActiveChunksSet::class.java)
    }
}