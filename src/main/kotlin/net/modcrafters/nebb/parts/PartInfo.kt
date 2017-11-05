package net.modcrafters.nebb.parts

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.minecraftforge.common.model.TRSRTransformation
import net.modcrafters.nebb.blocks.BaseBlock
import net.modcrafters.nebb.getSprite
import net.modcrafters.nebb.getTintIndex
import net.ndrei.teslacorelib.blocks.multipart.BlockPart
import net.ndrei.teslacorelib.blocks.multipart.OutlineRenderUtil
import net.ndrei.teslacorelib.render.selfrendering.RawCube
import javax.vecmath.Matrix4f

open class PartInfo(val name: String, val transform: Matrix4f, vararg val bigAABB: BigAABB)
    : BlockPart() {
    constructor(name: String, vararg bigAABB: BigAABB) : this(name, Matrix4f().also { it.setIdentity() }, *bigAABB)

    override val hitBoxes by lazy { bigAABB.map { it.transform(transform) }.toList() }

//    private var _block: IBlockState = defaultBlock
//    private var _cachedKey: String? = null

    //#region rendering

    fun getCacheKey(texture: PartTextureInfo) = texture.cacheKey // : String {
//        if (this._cachedKey.isNullOrBlank()) {
//            this._cachedKey ="${this.block.block.registryName?.toString() ?: "[no registry]"}:${this.block.block.getMetaFromState(this.block)}"
//        }
//        return this._cachedKey!!
//        return texture.
//    }

//    var block: IBlockState
//        get() = this._block
//        set(value) {
//            this._block = value
//            this._cachedKey = null
//        }

    open fun bakePartQuads(texture: PartTextureInfo, quads: MutableList<BakedQuad>, partBlockModel: IBakedModel, vertexFormat: VertexFormat, transform: TRSRTransformation) {
        this.bigAABB.forEach { this.bakeAABB(texture, quads, it, partBlockModel, vertexFormat, transform) }
    }

    protected fun bakeAABB(texture: PartTextureInfo, quads: MutableList<BakedQuad>, aabb: BigAABB, partBlockModel: IBakedModel, vertexFormat: VertexFormat, transform: TRSRTransformation) {
        EnumFacing.VALUES.fold(RawCube(aabb.from, aabb.to).autoUV()) { cube, it ->
            cube.addFace(it)
                .sprite(partBlockModel.getSprite(texture.block, it))
                .tint(partBlockModel.getTintIndex(texture.block, it))
        }.bake(quads, vertexFormat, transform)
    }

    //#endregion

//    open fun renderOutline(ev: DrawBlockHighlightEvent, offset: Vec3d) {
//        this.bigAABB.forEach {
//            RenderGlobal.drawSelectionBoundingBox(
//                it.small().offset(offset)
//                , 0.0f, 1.0f, 0.0f, 0.4f)
//        }
//    }
//    override val hitBoxes: List<IBlockPartHitBox> get() = this.bigAABB.toList()

    fun transformed(matrix4f: Matrix4f) =
        PartInfo(this.name, matrix4f, *this.bigAABB)

    open fun clone()= PartInfo(this.name, this.transform, *this.bigAABB)

    override val outlineDepthCheck get() = false

    override fun renderOutline(event: DrawBlockHighlightEvent) {
        val state = event.player.world.getBlockState(event.target.blockPos)
        if (state.block is BaseBlock<*>) {
            val matrix = (state.block as? BaseBlock<*>)?.getTransformMatrix(state)
            if (matrix != null) {
                OutlineRenderUtil.renderDefaultOutline(event, this.transformed(matrix))
                return
            }
        }
        super.renderOutline(event)
    }
}
