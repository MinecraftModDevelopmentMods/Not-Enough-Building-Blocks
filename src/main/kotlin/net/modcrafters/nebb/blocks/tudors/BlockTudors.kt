package net.modcrafters.nebb.blocks.tudors

import net.modcrafters.nebb.blocks.BaseBlock
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy
import net.ndrei.teslacorelib.render.selfrendering.SelfRenderingBlock

@AutoRegisterBlock
@SelfRenderingBlock(false, false)
@AutoRegisterColoredThingy
object BlockTudors : BaseBlock<TileTudors>("tudors", TileTudors::class.java, { TileTudors.getModel(it) })
