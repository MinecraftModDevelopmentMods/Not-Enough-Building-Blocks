package net.modcrafters.nebb

import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.modcrafters.nebb.blocks.RayTraceInfo
import net.ndrei.teslacorelib.BaseProxy
import net.ndrei.teslacorelib.gui.IGuiTexture

open class CommonProxy(side: Side) : BaseProxy(side)

@Suppress("unused")
class ServerProxy : CommonProxy(Side.SERVER)

@Suppress("unused")
class ClientProxy : CommonProxy(Side.CLIENT) {
    @SubscribeEvent
    fun drawOutlineHook(ev: DrawBlockHighlightEvent) {
        if ((ev.target.typeOfHit == RayTraceResult.Type.BLOCK) && (ev.target.subHit == 1)) {
            val hitInfo = ev.target?.hitInfo as? RayTraceInfo
            if (hitInfo != null) {
                hitInfo.renderOutline(ev)
                ev.isCanceled = true
            }
        }
    }

    @SubscribeEvent
    fun onTextureStitch(ev: TextureStitchEvent) {
        ev.map.registerSprite(NebbTextures.MESH.resource)
    }
}

enum class NebbTextures(path: String) : IGuiTexture {
    MESH("textures/blocks/mesh.png");

    override val resource = ResourceLocation(MOD_ID, path)
}