package net.modcrafters.nebb.blocks.jsonblocks

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.registries.IForgeRegistry
import net.modcrafters.nebb.blocks.BaseHorizontalBlock
import net.modcrafters.nebb.parts.BlockInfo

class JsonHorizontalBlock(registryName: String, override var info: BlockInfo)
    : BaseHorizontalBlock<JsonTile>(registryName, JsonTile::class.java, { JsonTile.getModel(it) }), IBlockInfoProvider {

    override fun registerItem(registry: IForgeRegistry<Item>) {
        registry.registerJsonItem(this)
    }

    override fun registerBlock(registry: IForgeRegistry<Block>) {
        registry.registerJsonBlock(this)
    }
}
