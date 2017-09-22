package net.modcrafters.nebb.blocks.tudors

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.model.TRSRTransformation
import net.modcrafters.nebb.blocks.BaseBlock
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.render.selfrendering.*
import javax.vecmath.Matrix4d
import javax.vecmath.Vector3d

@AutoRegisterBlock
@SelfRenderingBlock(false)
object BlockTudors : BaseBlock<TileTudors>("tudors", TileTudors::class.java), ISelfRenderingBlock {
    override fun getBakeries(layer: BlockRenderLayer?, state: IBlockState?, stack: ItemStack?, side: EnumFacing?, rand: Long, transform: TRSRTransformation): List<IBakery> {
        val bakeries = mutableListOf<IBakery>()

        val thick = 4.0

        bakeries.add(object : IBakery {
            override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat, transform: TRSRTransformation): MutableList<BakedQuad> {
                val quads = mutableListOf<BakedQuad>()

                val woolModel = Minecraft.getMinecraft().blockRendererDispatcher.blockModelShapes.getModelForState(Blocks.WOOL.defaultState)
                val plankModel = Minecraft.getMinecraft().blockRendererDispatcher.blockModelShapes.getModelForState(Blocks.PLANKS.defaultState)

                EnumFacing.VALUES.fold(RawCube(Vec3d(thick, thick, thick), Vec3d(32.0 - thick, 32.0 - thick, 32.0 - thick))) { cube, it ->
                    val woolQuads = woolModel.getQuads(Blocks.WOOL.defaultState, it, RANDOM.nextLong())
                    val texture = woolQuads.firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    cube.addFace(it).uv(0.0f, 0.0f, 16.0f, 16.0f).sprite(texture)
                }.bake(quads, vertexFormat, transform)


                EnumFacing.VALUES.fold(RawCube(Vec3d(0.0, 0.0, 0.0), Vec3d(thick, 32.0, thick)).autoUV()) { cube, it ->
                    val plankQuads = plankModel.getQuads(Blocks.PLANKS.defaultState, it, RANDOM.nextLong())
                    val texture = plankQuads.firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    cube.addFace(it).sprite(texture)
                }.bake(quads, vertexFormat, transform)

                EnumFacing.VALUES.fold(RawCube(Vec3d(32.0 - thick, 0.0, 0.0), Vec3d(32.0, 32.0, thick)).autoUV()) { cube, it ->
                    val plankQuads = plankModel.getQuads(Blocks.PLANKS.defaultState, it, RANDOM.nextLong())
                    val texture = plankQuads.firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    cube.addFace(it).sprite(texture)
                }.bake(quads, vertexFormat, transform)


                EnumFacing.VALUES.fold(RawCube(Vec3d(0.0, 0.0, 32.0 - thick), Vec3d(thick, 32.0, 32.0)).autoUV()) { cube, it ->
                    val plankQuads = plankModel.getQuads(Blocks.PLANKS.defaultState, it, RANDOM.nextLong())
                    val texture = plankQuads.firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    cube.addFace(it).sprite(texture)
                }.bake(quads, vertexFormat, transform)

                EnumFacing.VALUES.fold(RawCube(Vec3d(32.0 - thick, 0.0, 32.0 - thick), Vec3d(32.0, 32.0, 32.0)).autoUV()) { cube, it ->
                    val plankQuads = plankModel.getQuads(Blocks.PLANKS.defaultState, it, RANDOM.nextLong())
                    val texture = plankQuads.firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    cube.addFace(it).sprite(texture)
                }.bake(quads, vertexFormat, transform)


                EnumFacing.VALUES.fold(RawCube(Vec3d(thick, 32.0 - thick, 0.0), Vec3d(32.0 - thick, 32.0, thick)).autoUV()) { cube, it ->
                    val plankQuads = plankModel.getQuads(Blocks.PLANKS.defaultState, it, RANDOM.nextLong())
                    val texture = plankQuads.firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    cube.addFace(it).sprite(texture)
                }.bake(quads, vertexFormat, transform)

                EnumFacing.VALUES.fold(RawCube(Vec3d(thick, 32.0 - thick, 32.0 - thick), Vec3d(32.0 - thick, 32.0, 32.0)).autoUV()) { cube, it ->
                    val plankQuads = plankModel.getQuads(Blocks.PLANKS.defaultState, it, RANDOM.nextLong())
                    val texture = plankQuads.firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    cube.addFace(it).sprite(texture)
                }.bake(quads, vertexFormat, transform)


                EnumFacing.VALUES.fold(RawCube(Vec3d(thick, 0.0, 0.0), Vec3d(32.0 - thick, thick, thick)).autoUV()) { cube, it ->
                    val plankQuads = plankModel.getQuads(Blocks.PLANKS.defaultState, it, RANDOM.nextLong())
                    val texture = plankQuads.firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    cube.addFace(it).sprite(texture)
                }.bake(quads, vertexFormat, transform)

                EnumFacing.VALUES.fold(RawCube(Vec3d(thick, 0.0, 32.0 - thick), Vec3d(32.0 - thick, thick, 32.0)).autoUV()) { cube, it ->
                    val plankQuads = plankModel.getQuads(Blocks.PLANKS.defaultState, it, RANDOM.nextLong())
                    val texture = plankQuads.firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    cube.addFace(it).sprite(texture)
                }.bake(quads, vertexFormat, transform)


                EnumFacing.VALUES.fold(RawCube(Vec3d(0.0, 32.0 - thick, thick), Vec3d(thick, 32.0, 32.0 - thick)).autoUV()) { cube, it ->
                    val plankQuads = plankModel.getQuads(Blocks.PLANKS.defaultState, it, RANDOM.nextLong())
                    val texture = plankQuads.firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    cube.addFace(it).sprite(texture)
                }.bake(quads, vertexFormat, transform)

                EnumFacing.VALUES.fold(RawCube(Vec3d(32.0 - thick, 32.0 - thick, thick), Vec3d(32.0, 32.0, 32.0 - thick)).autoUV()) { cube, it ->
                    val plankQuads = plankModel.getQuads(Blocks.PLANKS.defaultState, it, RANDOM.nextLong())
                    val texture = plankQuads.firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    cube.addFace(it).sprite(texture)
                }.bake(quads, vertexFormat, transform)


                EnumFacing.VALUES.fold(RawCube(Vec3d(0.0, 0.0, thick), Vec3d(thick, thick, 32.0 - thick)).autoUV()) { cube, it ->
                    val plankQuads = plankModel.getQuads(Blocks.PLANKS.defaultState, it, RANDOM.nextLong())
                    val texture = plankQuads.firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    cube.addFace(it).sprite(texture)
                }.bake(quads, vertexFormat, transform)

                EnumFacing.VALUES.fold(RawCube(Vec3d(32.0 - thick, 0.0, thick), Vec3d(32.0, thick, 32.0 - thick)).autoUV()) { cube, it ->
                    val plankQuads = plankModel.getQuads(Blocks.PLANKS.defaultState, it, RANDOM.nextLong())
                    val texture = plankQuads.firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    cube.addFace(it).sprite(texture)
                }.bake(quads, vertexFormat, transform)

                fun IBakedModel.getPlankFace(it: EnumFacing) =
                    this.getQuads(Blocks.PLANKS.defaultState, it, RANDOM.nextLong())
                        .firstOrNull { q -> q.face == it }?.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite

                EnumFacing.HORIZONTALS.forEach {
                    val matrix = Matrix4d()
                    matrix.setIdentity()
                    matrix.mul(Matrix4d().also { m ->
                        m.setIdentity()
                        m.setTranslation(Vector3d(16.0, 16.0, 16.0))
                    })
                    matrix.mul(Matrix4d().also { m ->
                        m.setIdentity()
                        val angle = (if (it.axisDirection == EnumFacing.AxisDirection.POSITIVE) 1.0 else -1.0) * Math.PI / 4
                        when (it.axis) {
                            EnumFacing.Axis.X -> m.rotX(angle)
                            EnumFacing.Axis.Z -> m.rotZ(angle)
                        }
                    })

                    data class Quad(val x1: Double, val z1: Double, val x2: Double, val z2: Double)

                    val (x1, z1, x2, z2) = when (it.axis) {
                        EnumFacing.Axis.X -> when (it.axisDirection!!) {
                            EnumFacing.AxisDirection.NEGATIVE -> Quad(0.005, 16.0 - thick / 2.0, thick, 16.0 + thick / 2.0)
                            EnumFacing.AxisDirection.POSITIVE -> Quad(32.0 - thick, 16.0 - thick / 2.0, 31.995, 16.0 + thick / 2.0)
                        }
                        EnumFacing.Axis.Z -> when (it.axisDirection!!) {
                            EnumFacing.AxisDirection.NEGATIVE -> Quad(16.0 - thick / 2.0, 0.005, 16.0 + thick / 2.0, thick)
                            EnumFacing.AxisDirection.POSITIVE -> Quad(16.0 - thick / 2.0, 32.0 - thick, 16.0 + thick / 2.0, 31.995)
                        }
                        else -> Quad(0.0, 0.0, 0.0, 0.0)
                    }

                    RawCube(Vec3d(x1 - 16.0, -4.0 - 16.0, z1 - 16.0), Vec3d(x2 - 16.0, 36.0 - 16.0, z2 - 16.0))
                        .addFace(it).sprite(plankModel.getPlankFace(it)).uv(7f, 0f, 8f, 16f)
                        .addFace(it.rotateY()).sprite(plankModel.getPlankFace(it.rotateY())).uv(7f, 0f, 8f, 16f)
                        .addFace(it.rotateYCCW()).sprite(plankModel.getPlankFace(it.rotateYCCW())).uv(7f, 0f, 8f, 16f)
                        .bake(quads, vertexFormat, transform, matrix)
                }

                return quads
            }
        }.static())

        return bakeries.toList()
    }

    init {
        this.setLightOpacity(255)
    }

    override fun isFullCube(state: IBlockState?) = false
    override fun isFullBlock(state: IBlockState?) = false

    override fun getUseNeighborBrightness(state: IBlockState?) = true

    override fun doesSideBlockRendering(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = false
    override fun shouldSideBeRendered(blockState: IBlockState?, blockAccess: IBlockAccess?, pos: BlockPos?, side: EnumFacing?) = true
}
