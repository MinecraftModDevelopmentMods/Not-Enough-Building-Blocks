package net.modcrafters.nebb.blocks.jsonblocks

import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.modcrafters.nebb.parts.BlockInfo
import java.io.InvalidClassException

class JsonItem(block: Block) : ItemBlock(block), IBlockInfoProvider {
    override val info: BlockInfo
        get() = (this.block as? IBlockInfoProvider)?.info
            ?: throw InvalidClassException("JsonItem linked to the wrong block type.")
}
