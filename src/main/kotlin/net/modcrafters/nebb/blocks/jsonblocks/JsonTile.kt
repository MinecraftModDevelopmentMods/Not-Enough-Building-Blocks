package net.modcrafters.nebb.blocks.jsonblocks

import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.modcrafters.nebb.blocks.BaseTile
import net.modcrafters.nebb.parts.BlockInfo
import java.io.InvalidClassException

class JsonTile : BaseTile() {
    override fun setWorldCreate(worldIn: World?) {
        this.setWorld(worldIn)
    }

    override fun createBlockInfo(): BlockInfo {
        val block = (this.blockType ?: this.world.getBlockState(this.pos).block)
            as? JsonOrientedBlock ?: throw InvalidClassException("JsonTile not linked to a JsonOrientedBlock!")
        return block.getInfoClone(this.world.getBlockState(this.pos))
    }

    companion object {
        fun getModel(stack: ItemStack): BlockInfo {
            val item = stack.item as? JsonItem ?: throw InvalidClassException("JsonTile not linked to a JsonItem!")
            return item.getInfoClone(item.block.defaultState)
        }
    }
}
