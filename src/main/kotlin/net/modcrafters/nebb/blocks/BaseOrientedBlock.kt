package net.modcrafters.nebb.blocks

import mcmultipart.api.slot.EnumFaceSlot
import mcmultipart.api.slot.IPartSlot
import net.minecraft.block.properties.PropertyEnum
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
import net.minecraftforge.fml.common.Optional
import net.modcrafters.nebb.integrations.mcmp.MCMultiPartAddon
import net.modcrafters.nebb.parts.BlockInfo
import net.ndrei.teslacorelib.render.selfrendering.IProvideVariantTransform
import net.ndrei.teslacorelib.render.selfrendering.SelfRenderingBlocksRegistry

abstract class BaseOrientedBlock<T: BaseTile>(registryName: String, tileClass: Class<T>, itemModelCreator: (ItemStack) -> BlockInfo)
    : BaseBlock<T>(registryName, tileClass, itemModelCreator), IProvideVariantTransform {

    //#region block state

    override fun createBlockState(): BlockStateContainer =
        ExtendedBlockState(this,
            arrayOf(BaseOrientedBlock.FACING),
            (super.createBlockState() as? ExtendedBlockState)?.unlistedProperties?.toTypedArray() ?: arrayOf())

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing = EnumFacing.getFront(meta and 7)
        return this.defaultState.withProperty(BaseOrientedBlock.FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState) =
        state.getValue(BaseOrientedBlock.FACING).index

    override fun getStateForPlacement(world: World?, pos: BlockPos?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float,
                                      meta: Int, placer: EntityLivingBase?, hand: EnumHand?): IBlockState {
        val state = this.defaultState
        if ((world != null) && (pos != null)) {
            return state.withProperty(BaseOrientedBlock.FACING, facing?.opposite ?: EnumFacing.NORTH)
        }
        return state
    }

    @Optional.Method(modid = MCMultiPartAddon.MOD_ID)
    override fun getMultipartSlot(state: IBlockState): IPartSlot =
        EnumFaceSlot.fromFace(
            state.getValue(BaseOrientedBlock.FACING)
        )

    override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean {
        var state = world.getBlockState(pos)
        if (state.block === this) {
            val tileEntity = world.getTileEntity(pos)
            state = state.withProperty(BaseOrientedBlock.FACING,
                EnumFacing.getFront(state.getValue(BaseOrientedBlock.FACING).index + 1))
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
            "facing=north" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(90, 180)
            "facing=south" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(90, 0)
            "facing=east" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(90, 270)
            "facing=west" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(90, 90)
            "facing=up" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(180, 0)
            "facing=down" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(0, 0) // <-- DEFAULT
            else -> TRSRTransformation.identity()
        }
    }

    companion object {
        val FACING = PropertyEnum.create("facing", EnumFacing::class.java)
    }
}
