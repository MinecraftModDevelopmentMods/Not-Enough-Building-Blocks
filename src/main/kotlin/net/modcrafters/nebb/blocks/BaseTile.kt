package net.modcrafters.nebb.blocks

import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.Constants
import net.modcrafters.nebb.parts.BlockInfo

abstract class BaseTile : TileEntity() {
    private var _blockInfo: BlockInfo? = null

    fun getBlockInfo(): BlockInfo {
        if (this._blockInfo == null) {
            this._blockInfo = this.createBlockInfo()
        }
        return this._blockInfo!!
    }

    protected fun resetBlockInfo() {
        this._blockInfo = null
    }

    abstract protected fun createBlockInfo(): BlockInfo

    fun setPartTexture(partName: String, stack: ItemStack): Boolean {
        val part = this.getBlockInfo().parts.firstOrNull { it.name == partName }
        if (part != null) {
            val item = stack.item as? ItemBlock
            if (item != null) {
                val block = item.block
                if (block != null) {
                    val state = block.getStateFromMeta(stack.itemDamage)
                    if (state != null) {
                        part.block = state
                        this.markDirty()
                        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos)
                        return true
                    }
                }
            }
        }
        return false
    }

    //#region serialization

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        if (compound.hasKey("block", Constants.NBT.TAG_COMPOUND)) {
            this.getBlockInfo().deserializeNBT(compound.getCompoundTag("block"))
            if (this.world?.isRemote == true) {
                this.world.markBlockRangeForRenderUpdate(this.pos, this.pos)
            }
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val nbt = super.writeToNBT(compound)

        nbt.setTag("block", this.getBlockInfo().serializeNBT())

        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    //#endregion
}
