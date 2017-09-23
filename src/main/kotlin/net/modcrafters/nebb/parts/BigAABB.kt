package net.modcrafters.nebb.parts

import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class BigAABB(val x1: Double, val y1: Double, val z1: Double, val x2: Double, val y2: Double, val z2: Double) {
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
    }

    val from = Vec3d(this.x1, this.y1, this.z1)
    val to = Vec3d(this.x2, this.y2, this.z2)
}
