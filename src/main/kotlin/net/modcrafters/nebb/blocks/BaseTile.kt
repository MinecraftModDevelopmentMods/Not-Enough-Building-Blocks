package net.modcrafters.nebb.blocks

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumHand
import net.minecraftforge.common.util.Constants
import net.modcrafters.nebb.parts.BlockInfo
import net.modcrafters.nebb.parts.PartInfo
import net.ndrei.teslacorelib.blocks.multipart.IBlockPart
import net.ndrei.teslacorelib.blocks.multipart.IBlockPartHitBox
import net.ndrei.teslacorelib.blocks.multipart.IBlockPartProvider
import net.ndrei.teslacorelib.utils.getHeldItem

abstract class BaseTile : TileEntity(), IBlockPartProvider {
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
                        this.getBlockInfo().setTexture(part.name, state)

                        this.markDirty()
                        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos)
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun getParts() = this.getBlockInfo().parts.toList()

    override fun onPartActivated(player: EntityPlayer, hand: EnumHand, part: IBlockPart, hitBox: IBlockPartHitBox): Boolean {
        if (part is PartInfo) {
            return this.setPartTexture(part.name, player.getHeldItem())
        }
        return false
    }

    //#region serialization

    private var loaded = false
    private var loadNBT: NBTTagCompound? = null

    override fun onLoad() {
        super.onLoad()

        this.loaded = true
        if (this.loadNBT != null) {
            this.readBlockInfo(this.loadNBT!!)
            this.loadNBT = null
        }
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        if (compound.hasKey("block", Constants.NBT.TAG_COMPOUND)) {
            this.readBlockInfo(compound.getCompoundTag("block"))
        }
    }

    protected open fun readBlockInfo(nbt: NBTTagCompound) {
        if (!this.loaded) {
            this.loadNBT = nbt
            return
        }

        this.getBlockInfo().deserializeNBT(nbt)
        if (this.world?.isRemote == true) {
            world.markBlockRangeForRenderUpdate(this.pos, this.pos)
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
