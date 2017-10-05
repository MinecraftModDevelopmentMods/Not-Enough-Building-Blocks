package net.modcrafters.nebb.parts

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable

class PartTextureInfo(var block: IBlockState): INBTSerializable<NBTTagCompound> {
    constructor(nbt: NBTTagCompound): this(PartTextureInfo.getBlockState(nbt))

    val cacheKey = "${block.block.registryName?.toString() ?: "[no registry]"}:${block.block.getMetaFromState(block)}"

    //#region serialization

    override fun serializeNBT(): NBTTagCompound {
        val nbt = NBTTagCompound()

        nbt.setString("block", this.block.block.registryName.toString())
        nbt.setInteger("meta", this.block.block.getMetaFromState(this.block))

        return nbt
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.block = PartTextureInfo.getBlockState(nbt)
    }

    //#endregion

    companion object {
        private fun getBlockState(nbt: NBTTagCompound): IBlockState {
            val blockName = if (nbt.hasKey("block", Constants.NBT.TAG_STRING)) nbt.getString("block") else null
            val block = if (blockName.isNullOrBlank()) null else Block.REGISTRY.getObject(ResourceLocation(blockName))
            return if (block != null) {
                val meta = if (nbt.hasKey("meta", Constants.NBT.TAG_INT)) nbt.getInteger("meta") else 0
                block.getStateFromMeta(meta)
            } else {
                Blocks.AIR.defaultState
            }
        }

        val DEFAULT by lazy { PartTextureInfo(Blocks.STONE.defaultState) }
    }
}
