package net.modcrafters.nebb.integrations.mcmp

import mcmultipart.api.addon.IMCMPAddon
import mcmultipart.api.addon.MCMPAddon
import mcmultipart.api.multipart.IMultipart
import mcmultipart.api.multipart.IMultipartRegistry
import mcmultipart.api.multipart.IMultipartTile
import mcmultipart.api.slot.IPartSlot
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Optional
import net.modcrafters.nebb.NEBBMod
import net.modcrafters.nebb.blocks.BaseBlock

@MCMPAddon
@Optional.Interface(iface= "mcmultipart.api.addon.IMCMPAddon", modid = MCMultiPartAddon.MOD_ID, striprefs = true)
class MCMultiPartAddon : IMCMPAddon {

    override fun registerParts(registry: IMultipartRegistry) {
        MCMultiPartAddon.registry = registry
        MCMultiPartAddon.blocksCache.forEach {
            registry.registerPartWrapper(it.block, it)
            registry.registerStackWrapper(it.block)
        }
        MCMultiPartAddon.blocksCache.clear()
    }

    companion object {
        private var registry: IMultipartRegistry? = null
        private val blocksCache = mutableListOf<MCMultiPartInfo>()

        const val MOD_ID = "mcmultipart"

        val isModLoaded by lazy { Loader.isModLoaded(MCMultiPartAddon.MOD_ID) }

        fun enqueueBlock(block: BaseBlock<*>) {
            if (MCMultiPartAddon.isModLoaded) {
                val wrapper = MCMultiPartInfo(block)
                if (MCMultiPartAddon.registry != null) {
                    MCMultiPartAddon.registry!!.registerPartWrapper(block, wrapper)
                    MCMultiPartAddon.registry!!.registerStackWrapper(block)
                }
                else
                    MCMultiPartAddon.blocksCache.add(wrapper)
            }
        }
    }

    class MCMultiPartInfo(private val block: BaseBlock<*>) : IMultipart {
        override fun getSlotFromWorld(world: IBlockAccess, pos: BlockPos, state: IBlockState): IPartSlot {
            val slot = this.block.getMultipartSlot(state)
            NEBBMod.logger.info("getSlotFromWorld:: ${pos} :: ${slot}")
            return slot
        }

        override fun getSlotForPlacement(world: World, pos: BlockPos, state: IBlockState, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, placer: EntityLivingBase): IPartSlot {
            val slot = this.block.getMultipartSlot(state)
            NEBBMod.logger.info("getSlotForPlacement:: ${pos} :: ${slot}")
            return slot
        }

        override fun getBlock() = this.block

        override fun convertToMultipartTile(tileEntity: TileEntity?): IMultipartTile {
            return IMultipartTile.wrap(tileEntity)
        }
    }
}