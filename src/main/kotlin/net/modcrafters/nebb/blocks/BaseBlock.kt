package net.modcrafters.nebb.blocks

import mcmultipart.api.slot.IPartSlot
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.IForgeRegistry
import net.modcrafters.nebb.MOD_ID
import net.modcrafters.nebb.NEBBMod
import net.modcrafters.nebb.integrations.mcmp.MCMultiPartAddon
import net.modcrafters.nebb.parts.BigAABB
import net.modcrafters.nebb.parts.BlockInfo
import net.ndrei.teslacorelib.blocks.MultiPartBlock
import net.ndrei.teslacorelib.compatibility.IBlockColorDelegate
import net.ndrei.teslacorelib.render.selfrendering.IBakery
import net.ndrei.teslacorelib.render.selfrendering.IProvideVariantTransform
import net.ndrei.teslacorelib.render.selfrendering.ISelfRenderingBlock
import net.ndrei.teslacorelib.render.selfrendering.getPropertyString
import javax.vecmath.Matrix4f

abstract class BaseBlock<T: BaseTile>(registryName: String, private val tileClass: Class<T>, private val itemModelCreator: (ItemStack) -> BlockInfo)
    : MultiPartBlock(MOD_ID, NEBBMod.creativeTab, registryName, Material.ROCK), ITileEntityProvider, ISelfRenderingBlock, IBlockColorDelegate {

    init {
        this.setLightOpacity(255)
    }

    //#region BLOCK STATE & TILE ENTITY

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

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? = this.tileClass.newInstance()

    override fun registerBlock(registry: IForgeRegistry<Block>) {
        super.registerBlock(registry)
        GameRegistry.registerTileEntity(this.tileClass, this.registryName!!.toString() + "_tile")
    }

    //#endregion

    //#region RENDERING

    override fun getBakeries(layer: BlockRenderLayer?, state: IBlockState?, stack: ItemStack?, side: EnumFacing?, rand: Long, transform: TRSRTransformation): List<IBakery> {
        val bakeries = mutableListOf<IBakery>()

        bakeries.add(object : IBakery {
            override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat, transform: TRSRTransformation): MutableList<BakedQuad> {
                val quads = mutableListOf<BakedQuad>()
                val blockInfo = (state as? IExtendedBlockState)?.getValue(BLOCK_INFO)
                    .let {
                        if ((it == null) && (stack != null)) {
                            // item stack rendering
                            this@BaseBlock.itemModelCreator(stack)
                        }
                        else {
                            // block state rendering
                            it
                        }
                    }
                blockInfo?.getBakery()?.getQuads(state, stack, side, vertexFormat, transform)?.mapTo(quads) { it }
                return quads
            }
        })

        return bakeries.toList()
    }

    override fun isFullCube(state: IBlockState?) = false
    override fun isFullBlock(state: IBlockState?) = false

     override fun getUseNeighborBrightness(state: IBlockState?) = true

    override fun doesSideBlockRendering(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = false
    override fun shouldSideBeRendered(blockState: IBlockState?, blockAccess: IBlockAccess?, pos: BlockPos?, side: EnumFacing?) = true

    override fun getBlockFaceShape(worldIn: IBlockAccess?, state: IBlockState?, pos: BlockPos?, face: EnumFacing?): BlockFaceShape {
        // return super.getBlockFaceShape(worldIn, state, pos, face)
        return BlockFaceShape.UNDEFINED
    }

    override fun getBlockLayer() = BlockRenderLayer.CUTOUT_MIPPED

    //#endregion

    override fun colorMultiplier(state: IBlockState, worldIn: IBlockAccess?, pos: BlockPos?, tintIndex: Int): Int {
        val te = (if (pos != null) worldIn?.getTileEntity(pos) else null) as? BaseTile
        if (te != null) {
            val info = te.getBlockInfo()
            val texture = info.getBlock(info.parts.first().name) // TODO: maybe look through all until you get a valid one
            if (texture != null) {
                return Minecraft.getMinecraft().blockColors.colorMultiplier(texture, worldIn, pos, tintIndex)
            }
        }
        return -1
    }

    override fun transformCollisionAABB(aabb: AxisAlignedBB, state: IBlockState): AxisAlignedBB {
        val matrix = this.getTransformMatrix(state)
        return BigAABB.fromAABB(aabb).transform(matrix).aabb
//        val from = aabb.min.toVector3f()
//        val to = aabb.max.toVector3f()
//        matrix.transform(from)
//        matrix.transform(to)
//        return AxisAlignedBB(from.toVec3d(), to.toVec3d())
    }

    fun getTransformMatrix(state: IBlockState)=
        if (this is IProvideVariantTransform) {
            this.getTransform(state.getPropertyString()).matrix
        }
        else Matrix4f().also { it.setIdentity() }

    @Optional.Method(modid = MCMultiPartAddon.MOD_ID)
    abstract fun getMultipartSlot(state: IBlockState): IPartSlot

    companion object {
        val BLOCK_INFO = BlockInfoProperty("block_info")
    }
}
