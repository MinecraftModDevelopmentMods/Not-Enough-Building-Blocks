package net.modcrafters.nebb.blocks.slopes

import net.modcrafters.nebb.blocks.BaseFlippableBlock
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy
import net.ndrei.teslacorelib.render.selfrendering.SelfRenderingBlock

@AutoRegisterBlock
@SelfRenderingBlock(false, false)
@AutoRegisterColoredThingy
object BlockSimpleSlope : BaseFlippableBlock<TileSimpleSlope>("simple_slope", TileSimpleSlope::class.java, { TileSimpleSlope.getModel(it) })
