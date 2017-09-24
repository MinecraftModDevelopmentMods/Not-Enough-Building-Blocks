package net.modcrafters.nebb.blocks.slopes

import net.modcrafters.nebb.blocks.BaseOrientedBlock
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.render.selfrendering.SelfRenderingBlock

@AutoRegisterBlock
@SelfRenderingBlock(false)
object BlockSimpleSlope : BaseOrientedBlock<TileSimpleSlope>("simple_slope", TileSimpleSlope::class.java, { TileSimpleSlope.getModel(it) })
