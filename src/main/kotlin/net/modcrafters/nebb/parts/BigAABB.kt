package net.modcrafters.nebb.parts

import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.ndrei.teslacorelib.blocks.multipart.IBlockPartHitBox
import net.ndrei.teslacorelib.render.selfrendering.div
import net.ndrei.teslacorelib.render.selfrendering.times
import net.ndrei.teslacorelib.render.selfrendering.toVec3d
import net.ndrei.teslacorelib.render.selfrendering.toVector3f
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

class BigAABB(val x1: Double, val y1: Double, val z1: Double, val x2: Double, val y2: Double, val z2: Double)
    : IBlockPartHitBox {

    override val aabb: AxisAlignedBB get() = this.small()

    fun small() =
        AxisAlignedBB(x1 / SCALE, y1 / SCALE, z1 / SCALE, x2 / SCALE, y2 / SCALE, z2 / SCALE)

    fun small(offset: BlockPos) =
        small().offset(offset)

    fun small(offset: BlockPos, grow: Double) =
        small(offset).grow(grow)

    companion object {
        const val SCALE = 32.0

        fun withSize(x: Double, y: Double, z: Double, width: Double, height: Double, depth: Double) =
            BigAABB(x, y, z, x + width, y + height, z + depth)

        fun fromAABB(aabb: AxisAlignedBB) =
            BigAABB(aabb.minX * SCALE, aabb.minY * SCALE, aabb.minZ * SCALE,
                aabb.maxX * SCALE, aabb.maxY * SCALE, aabb.maxZ * SCALE)
    }

    val from = Vec3d(this.x1, this.y1, this.z1)
    val to = Vec3d(this.x2, this.y2, this.z2)

    fun transform(matrix: Matrix4f): BigAABB {
        val from = this.from.toVector3f() / SCALE.toFloat()
        val to = this.to.toVector3f() / SCALE.toFloat()

        matrix.transform(from)
        matrix.transform(to)

        val min = Vector3f(
            Math.min(from.x, to.x),
            Math.min(from.y, to.y),
            Math.min(from.z, to.z)
        )
        val max = Vector3f(
            Math.max(from.x, to.x),
            Math.max(from.y, to.y),
            Math.max(from.z, to.z)
        )

        while(min.x < 0) { min.x += 1; max.x += 1 }
        while(min.y < 0) { min.y += 1; max.y += 1 }
        while(min.z < 0) { min.z += 1; max.z += 1 }

        while(max.x > 1) { min.x -= 1; max.x -= 1 }
        while(max.y > 1) { min.y -= 1; max.y -= 1 }
        while(max.z > 1) { min.z -= 1; max.z -= 1 }

        val p1 = (min * SCALE.toFloat()).toVec3d()
        val p2 = (max * SCALE.toFloat()).toVec3d()
        
        return BigAABB(
            Math.min(p1.x, p2.x),
            Math.min(p1.y, p2.y),
            Math.min(p1.z, p2.z),
            Math.max(p1.x, p2.x),
            Math.max(p1.y, p2.y),
            Math.max(p1.z, p2.z)
        )
    }
}
