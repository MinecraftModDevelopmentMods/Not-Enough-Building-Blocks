package net.modcrafters.nebb.blocks

import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.IForgeRegistry
import net.modcrafters.nebb.MOD_ID
import net.modcrafters.nebb.NEBBMod
import net.ndrei.teslacorelib.blocks.RegisteredBlock

abstract class BaseBlock<T: BaseTile>(registryName: String, private val tileClass: Class<T>)
    : RegisteredBlock(MOD_ID, NEBBMod.creativeTab, registryName, Material.ROCK), ITileEntityProvider {

    override fun createBlockState(): BlockStateContainer {
        return ExtendedBlockState(this, arrayOf(), arrayOf(BLOCK_INFO))
    }

    override fun getExtendedState(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): IBlockState {
        if ((state is IExtendedBlockState) && (world != null) && (pos != null)) {
            val te = world.getTileEntity(pos)
            if (te is BaseTile) {
                return state.withProperty(BLOCK_INFO, te.getBlockInfo())
            }
        }
        return super.getExtendedState(state, world, pos)
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? = this.tileClass.newInstance()

    override fun registerBlock(registry: IForgeRegistry<Block>) {
        super.registerBlock(registry)
        GameRegistry.registerTileEntity(this.tileClass, this.registryName!!.toString() + "_tile")
    }

    companion object {
        @JvmStatic
        val BLOCK_INFO = BlockInfoProperty("block_info")
    }
}
