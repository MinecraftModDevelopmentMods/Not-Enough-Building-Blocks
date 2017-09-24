package net.modcrafters.nebb.blocks.slopes

import net.modcrafters.nebb.blocks.BaseBlock
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.render.selfrendering.SelfRenderingBlock

@AutoRegisterBlock
@SelfRenderingBlock(false)
object BlockSimpleSlope : BaseBlock<TileSimpleSlope>("simple_slope", TileSimpleSlope::class.java, { TileSimpleSlope.getModel(it) })
