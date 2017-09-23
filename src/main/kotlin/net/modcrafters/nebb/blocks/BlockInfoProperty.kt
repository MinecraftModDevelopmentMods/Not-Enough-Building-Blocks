package net.modcrafters.nebb.blocks

import net.minecraftforge.common.property.IUnlistedProperty
import net.modcrafters.nebb.parts.BlockInfo

class BlockInfoProperty(private val propName: String) : IUnlistedProperty<BlockInfo> {
    override fun isValid(value: BlockInfo?) = (value != null)
    override fun getName() = this.propName
    override fun getType() = BlockInfo::class.java

    override fun valueToString(value: BlockInfo?) = if (value == null) "" else value.getCacheKey()
}
