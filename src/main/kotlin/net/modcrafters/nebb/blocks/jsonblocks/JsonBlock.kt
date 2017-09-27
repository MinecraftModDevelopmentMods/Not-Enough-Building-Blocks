package net.modcrafters.nebb.blocks.jsonblocks

import net.modcrafters.nebb.blocks.BaseOrientedBlock

class JsonBlock(registryName: String): BaseOrientedBlock<JsonTile>(registryName, JsonTile::class.java, { JsonTile.getModel(it) })