package net.modcrafters.nebb.parts

import com.google.common.cache.CacheBuilder
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable
import net.modcrafters.nebb.NEBBMod
import net.ndrei.teslacorelib.render.selfrendering.IBakery
import net.ndrei.teslacorelib.render.selfrendering.combine
import net.ndrei.teslacorelib.render.selfrendering.static
import java.util.concurrent.TimeUnit

open class BlockInfo private constructor(val parts: Array<PartInfo>, private val cacheKeyTransformer: (String) -> String) : INBTSerializable<NBTTagCompound> {
    fun getCacheKey() =
        this.cacheKeyTransformer(this.parts.fold("") { str, it -> str + "::${it.getCacheKey()}" })

    //#region builder

    class Builder {
        private val parts = mutableListOf<PartInfo>()
        private var cacheKeyTransformer: (String) -> String = { it }

        fun add(part: PartInfo): Builder {
            this.parts.add(part)
            return this
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
            val part = it.serializeNBT()
            nbt.setTag(it.name, part)
        }

        return nbt
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.parts.forEach {
            if (nbt.hasKey(it.name, Constants.NBT.TAG_COMPOUND)) {
                it.deserializeNBT(nbt.getCompoundTag(it.name))
            } else {
                it.block = Blocks.AIR.defaultState
            }
        }
    }

    fun getBlock(partName: String) =
        this.parts.firstOrNull { it.name == partName }?.block

    //#endregion

    fun getBakery(): IBakery {
        return object : IBakery {
            override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat, transform: TRSRTransformation): MutableList<BakedQuad> {
//                NEBBMod.logger.info("Getting model for: ${this@BlockInfo.getCacheKey()}")
                return bakeries.get(this@BlockInfo.getCacheKey(), {
                    NEBBMod.logger.info("Creating model for: ${this@BlockInfo.getCacheKey()}")
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
                val model = Minecraft.getMinecraft().blockRendererDispatcher.blockModelShapes.getModelForState(part.block)

                part.bakePartQuads(quads, model, vertexFormat, transform)

                return quads
            }
        }

    companion object {
        fun getBuilder() = Builder()

        private val bakeries = CacheBuilder.newBuilder().expireAfterAccess(42, TimeUnit.SECONDS).build<String, IBakery>()
    }
}
