package net.modcrafters.nebb.blocks.jsonblocks

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.registries.IForgeRegistry

internal fun IForgeRegistry<Block>.registerJsonBlock(block: Block) {
    this.register(block)
}

internal fun IForgeRegistry<Item>.registerJsonItem(block: Block) {
    val item = JsonItem(block)
    item.registryName = block.registryName
    this.register(item)
}
