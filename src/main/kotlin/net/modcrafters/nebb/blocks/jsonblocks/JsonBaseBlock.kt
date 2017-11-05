package net.modcrafters.nebb.blocks.jsonblocks

import mcmultipart.api.slot.EnumCenterSlot
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.registries.IForgeRegistry
import net.modcrafters.nebb.blocks.BaseBlock
import net.modcrafters.nebb.integrations.mcmp.MCMultiPartAddon
import net.modcrafters.nebb.parts.BlockInfo

class JsonBaseBlock(registryName: String, override val info: BlockInfo)
    : BaseBlock<JsonTile>(registryName, JsonTile::class.java, { JsonTile.getModel(it) }), IBlockInfoProvider {

    override fun registerItem(registry: IForgeRegistry<Item>) {
        registry.registerJsonItem(this)
    }

    override fun registerBlock(registry: IForgeRegistry<Block>) {
        registry.registerJsonBlock(this)
    }

    @Optional.Method(modid = MCMultiPartAddon.MOD_ID)
    override fun getMultipartSlot(state: IBlockState) = EnumCenterSlot.CENTER
}