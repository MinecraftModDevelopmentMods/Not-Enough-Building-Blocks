package net.modcrafters.nebb.blocks.temp

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.model.TRSRTransformation
import net.ndrei.teslacorelib.render.selfrendering.RawQuad
import javax.vecmath.Matrix4d

class RawLump {
    private val faces = mutableListOf<RawLumpFace>()

    fun addFace(points: Array<Vec3d>, uvs: Array<Vec2f>, sprite: TextureAtlasSprite, face: EnumFacing, color: Int, bothSides: Boolean = false) =
        addFace(RawLumpFace(points, uvs, sprite, face, points.map { color }.toTypedArray(), bothSides))

    fun addFace(points: Array<Vec3d>, uvs: Array<Vec2f>, sprite: TextureAtlasSprite, face: EnumFacing, colors: Array<Int>? = null, bothSides: Boolean = false) =
        addFace(RawLumpFace(points, uvs, sprite, face, colors, bothSides))

    fun addFace(face: RawLumpFace) =
        this.also { it.faces.add(face) }

    fun bake(quads: MutableList<BakedQuad>, format: VertexFormat, transform: TRSRTransformation, matrix: Matrix4d? = null) {
        val rawrs = mutableListOf<RawQuad>()

        this.faces.forEach { it.bake(rawrs, transform) }

        rawrs.mapTo(quads) { it.applyMatrix(matrix ?: (Matrix4d().also { it.setIdentity() })).bake(format) }
    }
}

class RawLumpFace(points: Array<Vec3d>, uvs: Array<Vec2f>, private val sprite: TextureAtlasSprite, private val face: EnumFacing, colors: Array<Int>? = null, private val bothSides: Boolean = false) {
    private val points: Array<Vec3d>
    private val uvs: Array<Vec2f>
    private val colors: Array<Int>

    init {
        if ((points.size != 3) && (points.size != 4)) {
            throw IllegalArgumentException("Only accepting faces with 3 or 4 points.")
        }

        if (points.size != uvs.size) {
            throw IllegalArgumentException("Number of points must be the same as the number of texture points.")
        }

        this.points = if (points.size == 3) { arrayOf(points[0], points[1], points[2], points[0]) } else points
        this.uvs = if (uvs.size == 3) { arrayOf(uvs[0], uvs[1], uvs[2], uvs[0]) } else uvs

        val rawColors = if ((colors != null) && (colors.size != points.size)) {
            throw IllegalArgumentException("Number of points must be the same as the number of colors.")
        }
        else if (colors != null) { colors }
        else { points.map { -1 }.toTypedArray() }
        this.colors = if (rawColors.size == 3) { arrayOf(rawColors[0], rawColors[1], rawColors[2], rawColors[0]) } else rawColors
    }

    fun bake(rawrs: MutableList<RawQuad>, transform: TRSRTransformation) {
        rawrs.add(RawQuad(
            this.points[0], this.uvs[0].x, this.uvs[0].y,
            this.points[1], this.uvs[1].x, this.uvs[1].y,
            this.points[2], this.uvs[2].x, this.uvs[2].y,
            this.points[3], this.uvs[3].x, this.uvs[3].y,
            this.face,
            this.sprite,
            this.colors[0], // TODO: implement different colors for each point
            transform)
        )
        if (this.bothSides) {
            rawrs.add(RawQuad(
                this.points[0], this.uvs[0].x, this.uvs[0].y,
                this.points[3], this.uvs[3].x, this.uvs[3].y,
                this.points[2], this.uvs[2].x, this.uvs[2].y,
                this.points[1], this.uvs[1].x, this.uvs[1].y,
                this.face.opposite,
                this.sprite,
                this.colors[0], // TODO: implement different colors for each point
                transform)
            )
        }
    }
}
