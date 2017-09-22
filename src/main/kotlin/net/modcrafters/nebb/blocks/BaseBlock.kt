package net.modcrafters.nebb.blocks

import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.modcrafters.nebb.MOD_ID
import net.modcrafters.nebb.NEBBMod
import net.ndrei.teslacorelib.blocks.RegisteredBlock

abstract class BaseBlock<T: TileEntity>(registryName: String, private val tileClass: Class<T>)
    : RegisteredBlock(MOD_ID, NEBBMod.creativeTab, registryName, Material.ROCK), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? = this.tileClass.newInstance()
}