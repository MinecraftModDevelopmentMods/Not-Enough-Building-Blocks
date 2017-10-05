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
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.property.ExtendedBlockState
import net.modcrafters.nebb.parts.BlockInfo
import net.ndrei.teslacorelib.blocks.AxisAlignedBlock
import net.ndrei.teslacorelib.getFacingFromEntity
import net.ndrei.teslacorelib.render.selfrendering.IProvideVariantTransform
import net.ndrei.teslacorelib.render.selfrendering.SelfRenderingBlocksRegistry

abstract class BaseFlippableBlock<T: BaseTile>(registryName: String, tileClass: Class<T>, itemModelCreator: (ItemStack) -> BlockInfo)
    : BaseHorizontalBlock<T>(registryName, tileClass, itemModelCreator), IProvideVariantTransform {

    //#region block state

    override fun createBlockState(): BlockStateContainer =
        ExtendedBlockState(this,
            arrayOf(BaseHorizontalBlock.FACING, BaseFlippableBlock.FLIP_UP_DOWN),
            (super.createBlockState() as? ExtendedBlockState)?.unlistedProperties?.toTypedArray() ?: arrayOf())

    override fun getStateFromMeta(meta: Int) =
        super.getStateFromMeta(meta)
            .withProperty(BaseFlippableBlock.FLIP_UP_DOWN, (((meta shr 3) and 1) != 0))

    override fun getMetaFromState(state: IBlockState) =
        super.getMetaFromState(state) +
            ((if (state.getValue(BaseFlippableBlock.FLIP_UP_DOWN)) 1 else 0) shl 3)

    override fun getStateForPlacement(world: World?, pos: BlockPos?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float,
                                      meta: Int, placer: EntityLivingBase?, hand: EnumHand?): IBlockState {
        val state = this.defaultState
        if ((world != null) && (pos != null) && (placer != null)) {
            return state.withProperty(AxisAlignedBlock.FACING, getFacingFromEntity(pos, placer))
                .withProperty(FLIP_UP_DOWN, !((facing != EnumFacing.DOWN) && ((facing == EnumFacing.UP) || (hitY >= 0.5f))))
        }
        return state
    }

    //#endregion

    override fun getTransform(variant: String): TRSRTransformation {
        return when (variant) {
            "facing=north,flip_up_down=false" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(0, 0)
            "facing=south,flip_up_down=false" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(0, 180)
            "facing=east,flip_up_down=false" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(0, 90)
            "facing=west,flip_up_down=false" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(0, 270)
            "facing=north,flip_up_down=true" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(180, 270)
            "facing=south,flip_up_down=true" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(180, 90)
            "facing=east,flip_up_down=true" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(180, 180)
            "facing=west,flip_up_down=true" -> SelfRenderingBlocksRegistry.SelfRenderingModelLoader.getTransform(180, 0)
            else -> TRSRTransformation.identity()
        }
    }

    companion object {
        val FLIP_UP_DOWN = PropertyBool.create("flip_up_down")
    }
}