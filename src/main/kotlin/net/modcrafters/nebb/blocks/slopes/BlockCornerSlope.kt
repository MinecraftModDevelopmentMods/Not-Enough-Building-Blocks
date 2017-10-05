package net.modcrafters.nebb.blocks.slopes

import net.modcrafters.nebb.blocks.BaseFlippableBlock
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.render.selfrendering.SelfRenderingBlock

@AutoRegisterBlock
@SelfRenderingBlock(false, false)
object BlockCornerSlope : BaseFlippableBlock<TileCornerSlope>("corner_slope", TileCornerSlope::class.java, { TileCornerSlope.getModel(it) })
