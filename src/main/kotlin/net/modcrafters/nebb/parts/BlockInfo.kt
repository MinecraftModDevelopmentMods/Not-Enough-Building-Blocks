package net.modcrafters.nebb.parts

import com.google.common.cache.CacheBuilder
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable
import net.ndrei.teslacorelib.render.selfrendering.IBakery
import net.ndrei.teslacorelib.render.selfrendering.combine
import net.ndrei.teslacorelib.render.selfrendering.getPropertyString
import net.ndrei.teslacorelib.render.selfrendering.static
import java.util.concurrent.TimeUnit

open class BlockInfo private constructor(val parts: Array<PartInfo>, val cacheKeyTransformer: (String) -> String) : INBTSerializable<NBTTagCompound> {
    private val textures = mutableMapOf<String, PartTextureInfo>()

    fun getCacheKey() =
        this.cacheKeyTransformer(this.parts.fold("") { str, it -> str + "::${it.getCacheKey(this.textures.getOrDefault(it.name, PartTextureInfo.DEFAULT))}" })

    //#region builder

    class Builder {
        private val parts = mutableListOf<PartInfo>()
        private var cacheKeyTransformer: (String) -> String = { it }
        private val textures = mutableMapOf<String, PartTextureInfo>()

        fun add(part: PartInfo): Builder {
            this.parts.add(part)
            return this
        }

        fun add(part: PartInfo, block: IBlockState) =
            this.add(part, PartTextureInfo(block))

        fun add(part: PartInfo, texture: PartTextureInfo): Builder {
            this.textures[part.name] = texture
            return this.add(part)
        }

        fun setCacheKeyTransformer(transfomer: (String) -> String) {
            this.cacheKeyTransformer = transfomer
        }

        fun build() =
            BlockInfo(this.parts.toTypedArray(), this.cacheKeyTransformer)
    }

    //#endregion
    //#region serialization

    override fun serializeNBT(): NBTTagCompound {
        val nbt = NBTTagCompound()

        this.parts.forEach {
            if (this.textures.containsKey(it.name)) {
                val part = this.textures[it.name]?.serializeNBT()
                if (part != null) {
                    nbt.setTag(it.name, part)
                }
            }
        }

        return nbt
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.textures.clear()
        this.parts.forEach {
            if (nbt.hasKey(it.name, Constants.NBT.TAG_COMPOUND)) {
                this.textures[it.name] = PartTextureInfo(nbt.getCompoundTag(it.name))
            }
        }
    }

    fun getBlock(partName: String) = this.textures[partName]?.block

    //#endregion

    fun setTexture(partName: String, block: IBlockState) {
        this.setTexture(partName, PartTextureInfo(block))
    }

    fun setTexture(partName: String, texture: PartTextureInfo) {
        this.textures[partName] = texture
    }

    fun getBakery(): IBakery {
        return object : IBakery {
            override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat, transform: TRSRTransformation): MutableList<BakedQuad> {
//                NEBBMod.logger.info("Getting model for: ${this@BlockInfo.getCacheKey()}")
                val cacheKey = this@BlockInfo.getCacheKey() + "::${if (state != null) state.getPropertyString() else "[no state]"}"
                return bakeries.get(cacheKey, {
                    // NEBBMod.logger.info("Creating model for: ${cacheKey}")
                    this@BlockInfo.parts
                        .map { getBakery(it) }
                        .combine()
                        .static()
                }).getQuads(state, stack, side, vertexFormat, transform)
            }
        }
    }

    private fun getBakery(part: PartInfo) =
        object : IBakery {
            override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat, transform: TRSRTransformation): MutableList<BakedQuad> {
                val quads = mutableListOf<BakedQuad>()

                val texture = this@BlockInfo.textures.getOrDefault(part.name, PartTextureInfo.DEFAULT)
                val model = Minecraft.getMinecraft().blockRendererDispatcher.blockModelShapes.getModelForState(texture.block)

                part.bakePartQuads(texture, quads, model, vertexFormat, transform)

                return quads
            }
        }

    companion object {
        fun getBuilder() = Builder()

        private val bakeries = CacheBuilder.newBuilder().expireAfterAccess(42, TimeUnit.SECONDS).build<String, IBakery>()
    }
}
