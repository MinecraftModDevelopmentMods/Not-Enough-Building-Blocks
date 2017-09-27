package net.modcrafters.nebb.blocks.jsonblocks

import net.minecraft.item.ItemStack
import net.modcrafters.nebb.blocks.BaseTile
import net.modcrafters.nebb.parts.BlockInfo

class JsonTile : BaseTile() {
    override fun createBlockInfo(): BlockInfo {
        return JsonTile.getModel(ItemStack.EMPTY)
    }

    companion object {
        fun getModel(stack: ItemStack): BlockInfo {
            return BlockInfo.getBuilder().build()
        }
    }
}