package net.modcrafters.nebb.blocks.tudors

import mcmultipart.api.slot.EnumCenterSlot
import net.minecraft.block.state.IBlockState
import net.minecraftforge.fml.common.Optional
import net.modcrafters.nebb.blocks.BaseBlock
import net.modcrafters.nebb.integrations.mcmp.MCMultiPartAddon
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy
import net.ndrei.teslacorelib.render.selfrendering.SelfRenderingBlock

@AutoRegisterBlock
@SelfRenderingBlock(false, false)
@AutoRegisterColoredThingy
object BlockTudors : BaseBlock<TileTudors>("tudors", TileTudors::class.java, { TileTudors.getModel(it) }) {

    @Optional.Method(modid = MCMultiPartAddon.MOD_ID)
    override fun getMultipartSlot(state: IBlockState) = EnumCenterSlot.CENTER

}
