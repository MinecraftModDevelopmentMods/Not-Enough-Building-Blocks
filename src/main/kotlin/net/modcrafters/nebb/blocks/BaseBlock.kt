package net.modcrafters.nebb.blocks

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
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.IForgeRegistry
import net.modcrafters.nebb.MOD_ID
import net.modcrafters.nebb.NEBBMod
import net.modcrafters.nebb.parts.BlockInfo
import net.ndrei.teslacorelib.blocks.MultiPartBlock
import net.ndrei.teslacorelib.compatibility.IBlockColorDelegate
import net.ndrei.teslacorelib.render.selfrendering.IBakery
import net.ndrei.teslacorelib.render.selfrendering.ISelfRenderingBlock

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

    //#region RAY TRACE

//    fun rayTrace(world: World, pos: BlockPos, player: EntityPlayer): RayTraceResult? {
//        val start = player.positionVector.addVector(0.0, player.getEyeHeight().toDouble(), 0.0)
//        var reachDistance = 5.0
//        if (player is EntityPlayerMP) {
//            reachDistance = player.interactionManager.blockReachDistance
//        }
//        val end = start.add(player.lookVec.normalize().scale(reachDistance))
//        return this.rayTrace(world, pos, start, end)
//    }
//
//    override fun collisionRayTrace(state: IBlockState, world: World, pos: BlockPos, start: Vec3d, end: Vec3d): RayTraceResult? {
//        return this.rayTrace(world, pos, start, end)
//    }
//
//    fun rayTrace(world: World, pos: BlockPos, start: Vec3d, end: Vec3d): RayTraceResult? {
//        var best: RayTraceResult? = null
//        val tile = world.getTileEntity(pos) as? BaseTile
//        tile?.getBlockInfo()?.parts?.forEach { part ->
//            part.bigAABB.forEach { aabb ->
//                best = this.computeTrace(best, pos, start, end, aabb.small(), RayTraceInfo(part, aabb.small()))
//            }
//        }
//
//        return if (best == null) {
//            computeTrace(null, pos, start, end, FULL_BLOCK_AABB, null)
//        } else best
//    }
//
//    private fun computeTrace(lastBest: RayTraceResult?, pos: BlockPos, start: Vec3d, end: Vec3d,
//                             aabb: AxisAlignedBB, info: RayTraceInfo?): RayTraceResult? {
//        val next = super.rayTrace(pos, start, end, aabb) ?: return lastBest
//        next.subHit = if (info == null) -1 else 1
//        next.hitInfo = info
//        if (lastBest == null) {
//            return next
//        }
//        val distLast = lastBest.hitVec.squareDistanceTo(start)
//        val distNext = next.hitVec.squareDistanceTo(start)
//        return if (distLast > distNext) next else lastBest
//    }
//
//    @SideOnly(Side.CLIENT)
//    override fun getSelectedBoundingBox(state: IBlockState, world: World, pos: BlockPos): AxisAlignedBB {
//        val trace = Minecraft.getMinecraft().objectMouseOver
//        if (trace == null || trace.subHit < 0 || pos != trace.blockPos) {
//            return FULL_BLOCK_AABB
//        }
//        val mainId = trace.subHit
//        val info = trace.hitInfo as? RayTraceInfo
//        val aabb = if ((mainId == 1) && (info != null)) {
//            info.aabb
//        } else FULL_BLOCK_AABB
//        return aabb.grow(1 / 32.0).offset(pos)
//    }
//
//    override fun onBlockActivated(worldIn: World?, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
//        if ((worldIn != null) && (pos != null) && (playerIn != null) && !playerIn.isSneaking) {
//            val trace = rayTrace(worldIn, pos, playerIn)
//            if (trace != null) {
//                val info = if (trace.subHit == 1) trace.hitInfo as? RayTraceInfo else null
//                if (info != null) {
//                    val stack = playerIn.getHeldItem(hand)
//                    if (!stack.isEmpty) {
//                        val tile = worldIn.getTileEntity(pos) as? BaseTile
//                        if (tile != null) {
//                            return tile.setPartTexture(info.part.name, stack)
//                        }
//                    }
//                }
//            }
//        }
//
//        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
//    }

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

    companion object {
        val BLOCK_INFO = BlockInfoProperty("block_info")
    }
}
