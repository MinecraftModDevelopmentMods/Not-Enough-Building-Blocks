package net.modcrafters.nebb.blocks.jsonblocks

import net.minecraft.block.state.IBlockState
import net.modcrafters.nebb.parts.BlockInfo
import net.ndrei.teslacorelib.render.selfrendering.IProvideVariantTransform
import net.ndrei.teslacorelib.render.selfrendering.getPropertyString
import javax.vecmath.Matrix4f

interface IBlockInfoProvider {
    val info: BlockInfo

    fun getInfoClone(state: IBlockState) =
        BlockInfo.getBuilder().also {
            val block = state.block as? IProvideVariantTransform
            val transform = if (block != null) {
                block.getTransform(state.getPropertyString()).matrix
            } else Matrix4f().also { it.setIdentity() }

            this.info.parts.forEach { part ->
                val texture = info.getBlock(part.name)
                if (texture != null) {
                    it.add(part.clone(), texture)
                }
                else {
                    it.add(part.clone())
                }
            }
            it.setCacheKeyTransformer { this.info.cacheKeyTransformer(it) }
        }.build()
}
