package net.modcrafters.nebb.blocks

import net.minecraft.block.material.Material
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.modcrafters.nebb.parts.PartInfo

class RayTraceInfo(val part: PartInfo, val aabb: AxisAlignedBB) {
    fun renderOutline(ev: DrawBlockHighlightEvent) {
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.glLineWidth(2.0f)
        GlStateManager.disableTexture2D()
        GlStateManager.disableDepth()
        GlStateManager.depthMask(false)
        val blockPos = ev.target.blockPos
        val blockState = ev.player.world.getBlockState(blockPos)

        if ((blockState.getMaterial() !== Material.AIR) && ev.player.world.getWorldBorder().contains(blockPos)) {
            val dx = ev.player.lastTickPosX + (ev.player.posX - ev.player.lastTickPosX) * ev.partialTicks.toDouble()
            val dy = ev.player.lastTickPosY + (ev.player.posY - ev.player.lastTickPosY) * ev.partialTicks.toDouble()
            val dz = ev.player.lastTickPosZ + (ev.player.posZ - ev.player.lastTickPosZ) * ev.partialTicks.toDouble()

            part.renderOutline(ev, Vec3d(dx, dy, dz).subtractReverse(Vec3d(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())))
        }

        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }
}
