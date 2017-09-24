package net.modcrafters.nebb.parts

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable
import net.modcrafters.nebb.getSprite
import net.ndrei.teslacorelib.render.selfrendering.RawCube

open class PartInfo(val name: String, defaultBlock: IBlockState, vararg val bigAABB: BigAABB) : INBTSerializable<NBTTagCompound> {
    private var _block: IBlockState = defaultBlock
    private var _cachedKey: String? = null

    //#region rendering

    fun getCacheKey(): String {
        if (this._cachedKey.isNullOrBlank()) {
            this._cachedKey ="${this.block.block.registryName?.toString() ?: "[no registry]"}:${this.block.block.getMetaFromState(this.block)}"
        }
        return this._cachedKey!!
    }

    var block: IBlockState
        get() = this._block
        set(value) {
            this._block = value
            this._cachedKey = null
        }

    open fun bakePartQuads(quads: MutableList<BakedQuad>, partBlockModel: IBakedModel, vertexFormat: VertexFormat, transform: TRSRTransformation) {
        this.bigAABB.forEach { this.bakeAABB(quads, it, partBlockModel, vertexFormat, transform) }
    }

    protected fun bakeAABB(quads: MutableList<BakedQuad>, aabb: BigAABB, partBlockModel: IBakedModel, vertexFormat: VertexFormat, transform: TRSRTransformation) {
        EnumFacing.VALUES.fold(RawCube(aabb.from, aabb.to).autoUV()) { cube, it ->
            cube.addFace(it).sprite(partBlockModel.getSprite(this.block, it))
        }.bake(quads, vertexFormat, transform)
    }

    //#endregion

    open fun renderOutline(ev: DrawBlockHighlightEvent, offset: Vec3d) {
        this.bigAABB.forEach {
            RenderGlobal.drawSelectionBoundingBox(
                it.small().offset(offset)
                , 0.0f, 1.0f, 0.0f, 0.4f)
        }
    }

    //#region serialization

    override fun serializeNBT(): NBTTagCompound {
        val nbt = NBTTagCompound()

        nbt.setString("block", this.block.block.registryName.toString())
        nbt.setInteger("meta", this.block.block.getMetaFromState(this.block))

        return nbt
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        val blockName = if (nbt.hasKey("block", Constants.NBT.TAG_STRING)) nbt.getString("block") else null
        val block = if (blockName.isNullOrBlank()) null else Block.REGISTRY.getObject(ResourceLocation(blockName))
        if (block != null) {
            val meta = if (nbt.hasKey("meta", Constants.NBT.TAG_INT)) nbt.getInteger("meta") else 0
            this.block = block.getStateFromMeta(meta)
        }
        else {
            this.block = Blocks.AIR.defaultState
        }
    }

    //#endregion
}
