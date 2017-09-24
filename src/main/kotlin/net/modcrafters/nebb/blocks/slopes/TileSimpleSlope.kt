package net.modcrafters.nebb.blocks.slopes

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.model.TRSRTransformation
import net.modcrafters.nebb.blocks.BaseTile
import net.modcrafters.nebb.blocks.temp.RawLump
import net.modcrafters.nebb.blocks.tudors.TileTudors
import net.modcrafters.nebb.getSprite
import net.modcrafters.nebb.parts.BigAABB
import net.modcrafters.nebb.parts.BlockInfo
import net.modcrafters.nebb.parts.PartInfo

class TileSimpleSlope : BaseTile() {
    override fun createBlockInfo(): BlockInfo {
        return TileSimpleSlope.getModel(mapOf())
    }

    companion object {
        const val PART_SLOPE = "slope"

        fun getModel(stack: ItemStack): BlockInfo {
            return TileSimpleSlope.getModel(mapOf())
        }

        private fun getModel(textureMap: Map<String, IBlockState>): BlockInfo {
            val builder = BlockInfo.getBuilder()

            builder.add(object: PartInfo(PART_SLOPE, textureMap.getOrDefault(PART_SLOPE, Blocks.PLANKS.defaultState),
                BigAABB(0.0, 0.0, 0.0, BigAABB.SCALE, BigAABB.SCALE, BigAABB.SCALE)) {

                override fun bakePartQuads(quads: MutableList<BakedQuad>, partBlockModel: IBakedModel, vertexFormat: VertexFormat, transform: TRSRTransformation) {
                    val lump = RawLump()

                    val left = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.X)
                    lump.addFace(
                        arrayOf(
                            Vec3d(0.0, 0.0, 0.0),
                            Vec3d(0.0, 0.0, 32.0),
                            Vec3d(0.0, 32.0, 32.0)
                        ), arrayOf(
                            Vec2f(0.0f, 16.0f),
                            Vec2f(16.0f, 16.0f),
                            Vec2f(0.0f, 0.0f)
                        ),
                        partBlockModel.getSprite(this.block, left), left
                    )

                    val right = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.X)
                    lump.addFace(
                        arrayOf(
                            Vec3d(32.0, 0.0, 0.0),
                            Vec3d(32.0, 32.0, 32.0),
                            Vec3d(32.0, 0.0, 32.0)
                        ), arrayOf(
                            Vec2f(16.0f, 16.0f),
                            Vec2f(0.0f, 0.0f),
                            Vec2f(0.0f, 16.0f)
                        ),
                        partBlockModel.getSprite(this.block, right), right
                    )

                    val back = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Z)
                    lump.addFace(
                        arrayOf(
                            Vec3d(32.0, 0.0, 32.0),
                            Vec3d(32.0, 32.0, 32.0),
                            Vec3d(0.0, 32.0, 32.0),
                            Vec3d(0.0, 0.0, 32.0)
                        ), arrayOf(
                        Vec2f(0.0f, 16.0f),
                        Vec2f(0.0f, 0.0f),
                        Vec2f(16.0f, 0.0f),
                        Vec2f(16.0f, 16.0f)
                    ),
                        partBlockModel.getSprite(this.block, back), back
                    )

                    val front = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Z)
                    lump.addFace(
                        arrayOf(
                            Vec3d(0.0, 0.0, 0.0),
                            Vec3d(0.0, 32.0, 32.0),
                            Vec3d(32.0, 32.0, 32.0),
                            Vec3d(32.0, 0.0, 0.0)
                        ), arrayOf(
                        Vec2f(0.0f, 16.0f),
                        Vec2f(0.0f, 0.0f),
                        Vec2f(16.0f, 0.0f),
                        Vec2f(16.0f, 16.0f)
                    ),
                        partBlockModel.getSprite(this.block, front), front
                    )

                    val bottom = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Y)
                    lump.addFace(
                        arrayOf(
                            Vec3d(0.0, 0.0, 0.0),
                            Vec3d(0.0, 0.0, 32.0),
                            Vec3d(32.0, 0.0, 32.0),
                            Vec3d(32.0, 0.0, 0.0)
                        ), arrayOf(
                        Vec2f(0.0f, 16.0f),
                        Vec2f(0.0f, 0.0f),
                        Vec2f(16.0f, 0.0f),
                        Vec2f(16.0f, 16.0f)
                    ),
                        partBlockModel.getSprite(this.block, bottom), bottom
                    )

                    lump.bake(quads, vertexFormat, transform) // TODO: orientation based rotation matrix
                }
            })

            return builder.build()
        }
    }
}
