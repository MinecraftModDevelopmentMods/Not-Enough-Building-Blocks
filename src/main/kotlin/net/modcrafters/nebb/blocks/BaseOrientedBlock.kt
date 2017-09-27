package net.modcrafters.nebb.blocks

import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.property.ExtendedBlockState
import net.modcrafters.nebb.parts.BlockInfo
import net.ndrei.teslacorelib.blocks.AxisAlignedBlock
import net.ndrei.teslacorelib.getFacingFromEntity

abstract class BaseOrientedBlock<T: BaseTile>(registryName: String, tileClass: Class<T>, itemModelCreator: (ItemStack) -> BlockInfo, private val flipUpDown: Boolean = false)
    : BaseBlock<T>(registryName, tileClass, itemModelCreator) {

    //#region block state

    override fun createBlockState(): BlockStateContainer =
        ExtendedBlockState(this,
            arrayOf(AxisAlignedBlock.FACING, BaseOrientedBlock.FLIP_UP_DOWN), // else arrayOf(AxisAlignedBlock.FACING),
            (super.createBlockState() as? ExtendedBlockState)?.unlistedProperties?.toTypedArray() ?: arrayOf())

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing = EnumFacing.getFront(meta).let {
            if (it.axis == EnumFacing.Axis.Y) EnumFacing.NORTH else it
        }
        val flip = (this.flipUpDown && ((meta shr 3) != 0))
        return this.defaultState.withProperty(AxisAlignedBlock.FACING, enumfacing).let {
            if (this.flipUpDown) it.withProperty(BaseOrientedBlock.FLIP_UP_DOWN, flip) else it
        }
    }

    override fun getMetaFromState(state: IBlockState) =
        ((if (this.flipUpDown && state.getValue(BaseOrientedBlock.FLIP_UP_DOWN)) 1 else 0) shl 3) +
            state.getValue(AxisAlignedBlock.FACING).index

    override fun getStateForPlacement(world: World?, pos: BlockPos?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float,
                                      meta: Int, placer: EntityLivingBase?, hand: EnumHand?): IBlockState {
        val state = this.defaultState
        if ((world != null) && (pos != null) && (placer != null)) {
            return state.withProperty(AxisAlignedBlock.FACING, /* placer.horizontalFacing.opposite).let { / */ getFacingFromEntity(pos, placer)).let {
                if (!this.flipUpDown) it
                else it.withProperty(FLIP_UP_DOWN, !((facing != EnumFacing.DOWN) && ((facing == EnumFacing.UP) || (hitY >= 0.5f))))
            }
        }
        return state
    }

    override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean {
        var state = world.getBlockState(pos)
        if (state.block === this) {
            val tileEntity = world.getTileEntity(pos)
            state = state.withProperty(AxisAlignedBlock.FACING, state.getValue(AxisAlignedBlock.FACING).rotateY())
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
        val FLIP_UP_DOWN = PropertyBool.create("flip_up_down")
    }
}
