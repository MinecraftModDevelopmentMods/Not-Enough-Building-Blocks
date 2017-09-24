package net.modcrafters.nebb.blocks

import net.minecraft.block.material.Material
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.modcrafters.nebb.parts.PartInfo

class RayTraceInfo(val part: PartInfo, val aabb: AxisAlignedBB) {
    fun renderOutline(ev: DrawBlockHighlightEvent) {
        part.bigAABB.forEach {
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.glLineWidth(2.0f)
            GlStateManager.disableTexture2D()
            GlStateManager.disableDepth()
            GlStateManager.depthMask(false)
            val blockpos = ev.target.blockPos
            val iblockstate = ev.player.world.getBlockState(blockpos)

            if (iblockstate.getMaterial() !== Material.AIR && ev.player.world.getWorldBorder().contains(blockpos)) {
                val d0 = ev.player.lastTickPosX + (ev.player.posX - ev.player.lastTickPosX) * ev.partialTicks.toDouble()
                val d1 = ev.player.lastTickPosY + (ev.player.posY - ev.player.lastTickPosY) * ev.partialTicks.toDouble()
                val d2 = ev.player.lastTickPosZ + (ev.player.posZ - ev.player.lastTickPosZ) * ev.partialTicks.toDouble()
                RenderGlobal.drawSelectionBoundingBox(it.small(blockpos) //, 1.0 / 32.0)
//                        .grow(1.0 / 32.0)
                    .offset(-d0, -d1, -d2)
                    , 0.0f, 1.0f, 0.0f, 0.4f)
            }

            GlStateManager.enableDepth()
            GlStateManager.depthMask(true)
            GlStateManager.enableTexture2D()
            GlStateManager.disableBlend()
        }
    }
}
