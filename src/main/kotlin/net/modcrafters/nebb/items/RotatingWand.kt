package net.modcrafters.nebb.items

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.modcrafters.nebb.MOD_ID
import net.modcrafters.nebb.NEBBMod
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.items.RegisteredItem

@AutoRegisterItem
object RotatingWand: RegisteredItem(MOD_ID, NEBBMod.creativeTab, "wand_rotate") {
    override fun onItemUseFirst(player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, hand: EnumHand): EnumActionResult {
        val state = world.getBlockState(pos)
        state.block.rotateBlock(world, pos, side)

        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand)
    }
}
