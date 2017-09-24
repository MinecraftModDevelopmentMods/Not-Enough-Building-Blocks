package net.modcrafters.nebb.blocks

import net.minecraft.block.BlockHorizontal
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.util.Constants
import net.modcrafters.nebb.NEBBMod
import net.modcrafters.nebb.parts.BlockInfo
import net.ndrei.teslacorelib.blocks.OrientedBlock
import net.ndrei.teslacorelib.getFacingFromEntity

abstract class BaseOrientedBlock<T: BaseTile>(registryName: String, tileClass: Class<T>, itemModelCreator: (ItemStack) -> BlockInfo)
    : BaseBlock<T>(registryName, tileClass, itemModelCreator) {

    init {
        this.defaultState = this.blockState.baseState
            .withProperty(OrientedBlock.FACING, EnumFacing.NORTH)
    }

    //#region block state

    override fun createBlockState(): BlockStateContainer =
        ExtendedBlockState(this,
            arrayOf(FACING),
            (super.createBlockState() as? ExtendedBlockState)?.unlistedProperties?.toTypedArray() ?: arrayOf())

    override fun getStateFromMeta(meta: Int): IBlockState {
        var enumfacing = EnumFacing.getFront(meta)
        if (enumfacing.axis == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH
        }
        return this.defaultState.withProperty(OrientedBlock.FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(OrientedBlock.FACING).index
    }

    override fun onBlockPlacedBy(world: World?, pos: BlockPos?, state: IBlockState?, placer: EntityLivingBase?, stack: ItemStack?) {
        world!!.setBlockState(pos!!, state!!.withProperty(OrientedBlock.FACING, getFacingFromEntity(pos, placer!!)), 2)
        if ((stack != null) && !stack.isEmpty && stack.hasTagCompound()) {
            val nbt = stack.tagCompound
            if (nbt != null && nbt.hasKey("tileentity", Constants.NBT.TAG_COMPOUND)) {
                val teNBT = nbt.getCompoundTag("tileentity")
                try {
                    val te = this.createNewTileEntity(world, 0)
                    if (te != null) {
                        te.deserializeNBT(teNBT)
                        world.setTileEntity(pos, te)
                    }
                } catch (t: Throwable) {
                    NEBBMod.logger.error(t)
                }
            }
        }
    }

    override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean {
        var state = world.getBlockState(pos)
        if (state.block === this) {
            val tileEntity = world.getTileEntity(pos)
            state = state.withProperty(OrientedBlock.FACING, state.getValue(OrientedBlock.FACING).rotateY())
            world.setBlockState(pos, state)
            if (tileEntity != null) {
                tileEntity.validate()
                world.setTileEntity(pos, tileEntity)
            }
            return true
        }
        return false
    }

    //#endregion

    companion object {
        val FACING = BlockHorizontal.FACING!!
    }
}
