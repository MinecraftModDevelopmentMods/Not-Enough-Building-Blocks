package net.modcrafters.nebb.parts

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraftforge.common.model.TRSRTransformation
import net.modcrafters.nebb.getSprite
import net.modcrafters.nebb.getTintIndex
import net.ndrei.teslacorelib.render.selfrendering.IRawFigure

class ComplexPartInfo(name: String, vararg bigAABB: BigAABB) : PartInfo(name, *bigAABB) {
    val figures = mutableListOf<IRawFigure>()

    override fun bakePartQuads(texture: PartTextureInfo, quads: MutableList<BakedQuad>, partBlockModel: IBakedModel, vertexFormat: VertexFormat, transform: TRSRTransformation) {
        this.figures.forEach {
            // remap textures
            it.getFaces().forEach {
                it.sprite = partBlockModel.getSprite(texture.block, it.face)
                it.tintIndex = partBlockModel.getTintIndex(texture.block, it.face)
            }

            it.bake(quads, vertexFormat, transform)
        }
    }

    override fun clone() = ComplexPartInfo(this.name, *this.bigAABB).also {
        this.figures.forEach { figure ->
            it.figures.add(figure.clone())
        }
    }
}
