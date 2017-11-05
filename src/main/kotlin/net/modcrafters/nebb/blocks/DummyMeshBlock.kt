package net.modcrafters.nebb.blocks

import net.minecraft.block.material.Material
import net.modcrafters.nebb.MOD_ID
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.blocks.RegisteredBlock

@AutoRegisterBlock
object DummyMeshBlock: RegisteredBlock(MOD_ID, null, "dummy_mesh", Material.WOOD)