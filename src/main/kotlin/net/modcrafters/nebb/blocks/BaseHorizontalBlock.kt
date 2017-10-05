package net.modcrafters.nebb.blocks

import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.property.ExtendedBlockState
import net.modcrafters.nebb.parts.BlockInfo
import net.ndrei.teslacorelib.blocks.AxisAlignedBlock
import net.ndrei.teslacorelib.getFacingFromEntity
import net.ndrei.teslacorelib.render.selfrendering.IProvideVariantTransform
import net.ndrei.teslacorelib.render.selfrendering.SelfRenderingBlocksRegistry

abstract class BaseHorizontalBlock<T: BaseTile>(registryName: String, tileClass: Class<T>, itemModelCreator: (ItemStack) -> BlockInfo)
    : BaseBlock<T>(registryName, tileClass, itemModelCreator), IProvideVariantTransform {

    //#region block state

    override fun createBlockState(): BlockStateContainer =
        ExtendedBlockState(this,
            arrayOf(BaseHorizontalBlock.FACING),
            (super.createBlockState() as? ExtendedBlockState)?.unlistedProperties?.toTypedArray() ?: arrayOf())

    override fun getStateFromMeta(meta: Int): IBlockState {
        val facing = EnumFacing.getFront(meta and 7).let {
            if (it.axis == EnumFacing.Axis.Y) EnumFacing.NORTH else it
        }
        return this.defaultState.withProperty(BaseHorizontalBlock.FACING, facing)
    }

    override fun getMetaFromState(state: IBlockState) =
        state.getValue(BaseHorizontalBlock.FACING).index

    override fun getStateForPlacement(world: World?, pos: BlockPos?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float,
                                      meta: Int, placer: EntityLivingBase?, hand: EnumHand?): IBlockState {
        val state = this.defaultState
        if ((world != null) && (pos != null) && (placer != null)) {
            return state.withProperty(BaseHorizontalBlock.FACING, getFacingFromEntity(pos, placer))
        }
        return state
    }

    override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean {
        var state = world.getBlockState(pos)
        if (state.block === this) {
            val tileEntity = world.getTileEntity(pos)
            state = state.withProperty(BaseHorizontalBlock.FACING, state.getValue(BaseHorizontalBlock.FACING).rotateY())
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

    override fun getTransform(variant: String): TRSRTransformation {
        return when (variant) {
            "facing=north" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(0, 0)
            "facing=south" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(0, 180)
            "facing=east" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(0, 90)
            "facing=west" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(0, 270)
            else -> TRSRTransformation.identity()
        }
    }

    companion object {
        val FACING = AxisAlignedBlock.FACING
    }
}