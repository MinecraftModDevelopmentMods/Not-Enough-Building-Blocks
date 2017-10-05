package net.modcrafters.nebb.blocks.tudors

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.util.Constants
import net.modcrafters.nebb.blocks.BaseTile
import net.modcrafters.nebb.getSprite
import net.modcrafters.nebb.parts.BigAABB
import net.modcrafters.nebb.parts.BlockInfo
import net.modcrafters.nebb.parts.PartInfo
import net.modcrafters.nebb.parts.PartTextureInfo
import net.ndrei.teslacorelib.render.selfrendering.RawCube
import javax.vecmath.Matrix4d
import javax.vecmath.Vector3d

class TileTudors : BaseTile() {
    private var width = 4.0

    override fun createBlockInfo(): BlockInfo {
        return TileTudors.getModel(width, mapOf())
    }

    companion object {
        const val PART_CENTER = "center"
        const val PART_FRAME = "frame"
        
        fun getModel(stack: ItemStack): BlockInfo {
            return getModel(4.0, mapOf(
                PART_CENTER to Blocks.WOOL.defaultState,
                PART_FRAME to Blocks.PLANKS.defaultState
            ))
        }
        
        private fun getModel(width: Double, textureMap: Map<String, IBlockState>): BlockInfo {
            val builder = BlockInfo.getBuilder()

            builder.add(PartInfo(PART_CENTER,
                BigAABB.withSize(width, width, width, 32 - width * 2, 32 - width * 2, 32 - width * 2)
            ), textureMap.getOrDefault(PART_CENTER, Blocks.WOOL.defaultState))

            builder.add(object: PartInfo(PART_FRAME,
                // vertical
                BigAABB.withSize(0.0, 0.0, 0.0, width, 32.0, width),
                BigAABB.withSize(32.0 - width, 0.0, 0.0, width, 32.0, width),
                BigAABB.withSize(32.0 - width, 0.0, 32.0 - width, width, 32.0, width),
                BigAABB.withSize(0.0, 0.0, 32.0 - width, width, 32.0, width),

                // bottom
                BigAABB.withSize(width, 0.0, 0.0, 32.0 - width * 2, width, width),
                BigAABB.withSize(32.0 - width, 0.0, width, width, width, 32.0 - width * 2),
                BigAABB.withSize(width, 0.0, 32.0 - width, 32.0 - width * 2, width, width),
                BigAABB.withSize(0.0, 0.0, width, width, width, 32.0 - width * 2),

                // top
                BigAABB.withSize(width, 32.0 - width, 0.0, 32.0 - width * 2, width, width),
                BigAABB.withSize(32.0 - width, 32.0 - width, width, width, width, 32.0 - width * 2),
                BigAABB.withSize(width, 32.0 - width, 32.0 - width, 32.0 - width * 2, width, width),
                BigAABB.withSize(0.0, 32.0 - width, width, width, width, 32.0 - width * 2)
            ) {
                override fun bakePartQuads(texture: PartTextureInfo, quads: MutableList<BakedQuad>, partBlockModel: IBakedModel, vertexFormat: VertexFormat, transform: TRSRTransformation) {
                    super.bakePartQuads(texture, quads, partBlockModel, vertexFormat, transform)

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
                                EnumFacing.AxisDirection.NEGATIVE -> Quad(0.005, 16.0 - width / 2.0, width, 16.0 + width / 2.0)
                                EnumFacing.AxisDirection.POSITIVE -> Quad(32.0 - width, 16.0 - width / 2.0, 31.995, 16.0 + width / 2.0)
                            }
                            EnumFacing.Axis.Z -> when (it.axisDirection!!) {
                                EnumFacing.AxisDirection.NEGATIVE -> Quad(16.0 - width / 2.0, 0.005, 16.0 + width / 2.0, width)
                                EnumFacing.AxisDirection.POSITIVE -> Quad(16.0 - width / 2.0, 32.0 - width, 16.0 + width / 2.0, 31.995)
                            }
                            else -> Quad(0.0, 0.0, 0.0, 0.0)
                        }

                        RawCube(Vec3d(x1 - 16.0, -4.0 - 16.0, z1 - 16.0), Vec3d(x2 - 16.0, 36.0 - 16.0, z2 - 16.0))
                            .addFace(it).sprite(partBlockModel.getSprite(texture.block, it)).uv(7f, 0f, 8f, 16f)
                            .addFace(it.rotateY()).sprite(partBlockModel.getSprite(texture.block, it.rotateY())).uv(7f, 0f, 8f, 16f)
                            .addFace(it.rotateYCCW()).sprite(partBlockModel.getSprite(texture.block, it.rotateYCCW())).uv(7f, 0f, 8f, 16f)
                            .bake(quads, vertexFormat, transform, matrix)
                    }
                }
            },
                textureMap.getOrDefault(PART_FRAME, Blocks.PLANKS.defaultState))

            builder.setCacheKeyTransformer { "$width$it" }

            return builder.build()
        }
    }

    //#region serialization

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        if (compound.hasKey("width", Constants.NBT.TAG_DOUBLE)) {
            this.width = compound.getDouble("width")
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val nbt = super.writeToNBT(compound)

        nbt.setDouble("width", this.width)

        return nbt
    }

    //#endregion
}
