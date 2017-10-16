package net.modcrafters.nebb.blocks.jsonblocks

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.IForgeRegistry
import net.modcrafters.nebb.MOD_ID
import net.modcrafters.nebb.NEBBMod
import net.modcrafters.nebb.blocks.BaseBlock
import net.modcrafters.nebb.parts.BigAABB
import net.modcrafters.nebb.parts.BlockInfo
import net.modcrafters.nebb.parts.ComplexPartInfo
import net.modcrafters.nebb.parts.PartInfo
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingyHandler
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.config.getMember
import net.ndrei.teslacorelib.render.selfrendering.RawLump
import net.ndrei.teslacorelib.render.selfrendering.SelfRenderingBlocksRegistry
import org.apache.commons.io.FilenameUtils
import java.nio.file.Files
import java.nio.file.Path

@RegistryHandler
object JsonRegistry: IRegistryHandler {
    private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    private val jsonBlocks = mutableMapOf<ResourceLocation, BaseBlock<*>>()

    override fun registerBlocks(asm: ASMDataTable, registry: IForgeRegistry<Block>) {
        CraftingHelper.findFiles(Loader.instance().activeModContainer(),
            "assets/$MOD_ID/block-recipes", { path -> true },
            { root, file ->
                val relative = root.relativize(file).toString()
                if (("json" == FilenameUtils.getExtension(file.toString())) && !relative.startsWith("_")) {
                    val name = FilenameUtils.removeExtension(relative).replace("\\\\".toRegex(), "/")
                    val key = ResourceLocation(MOD_ID, name)

                    loadRecipeResource(key, file)
                }
                true
            }, true, true)

        this.jsonBlocks.values.forEach { it.registerBlock(registry) }
        GameRegistry.registerTileEntity(JsonTile::class.java, ResourceLocation(MOD_ID,"jsonblock_tile").toString())
    }

    override fun registerItems(asm: ASMDataTable, registry: IForgeRegistry<Item>) {
        this.jsonBlocks.values.forEach { it.registerItem(registry) }
    }

    override fun registerRenderers(asm: ASMDataTable) {
        this.jsonBlocks.values.forEach {
            it.registerRenderer()
            SelfRenderingBlocksRegistry.addBlock(it)
        }
    }

    override fun postInit(asm: ASMDataTable) {
        if (TeslaCoreLib.isClientSide) {
            this.jsonBlocks.values.forEach {
                AutoRegisterColoredThingyHandler.handler(it, asm, Loader.instance().activeModContainer())
            }
        }
    }

    private fun loadRecipeResource(key: ResourceLocation, file: Path): Boolean {
        Files.newBufferedReader(file).use {
            try {
                val json = JsonUtils.fromJson(GSON, it, JsonObject::class.java) ?: return false
                val parts = if (json.has("parts")) json.get("parts") else null
                if ((parts == null) || !parts.isJsonObject)
                    throw JsonParseException("'parts' object not found in root.")

                val builder = BlockInfo.getBuilder()
                parts.asJsonObject.entrySet().forEach {
                    val partName = it.key
                    if (!it.value.isJsonObject)
                        throw JsonParseException("Part: '$partName' must be an object.")

                    val hitBoxes = if (it.value.asJsonObject.has("hit"))
                        this.loadAABBs(it.value.asJsonObject.get("hit"))
                    else listOf(AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))
                    if (hitBoxes.isEmpty())
                        throw JsonParseException("Part: '$partName' has no hit boxes.")

                    if (it.value.asJsonObject.has("lump")) {
                        builder.add(ComplexPartInfo(partName, *hitBoxes.map {
                            BigAABB.fromAABB(it)
                        }.toTypedArray()).loadLump(it.value.asJsonObject.get("lump")))
                    }
                    else {
                        builder.add(PartInfo(partName, *hitBoxes.map {
                            BigAABB.fromAABB(it)
                        }.toTypedArray()))
                    }
                }

                builder.setCacheKeyTransformer { "$key$it" }

                val block: BaseBlock<*> = when (JsonUtils.getString(json, "type", "basic")) {
                    "oriented" -> JsonOrientedBlock(key.resourcePath, builder.build())
                    "horizontal" -> JsonHorizontalBlock(key.resourcePath, builder.build())
                    "flippable" -> JsonFlippableBlock(key.resourcePath, builder.build())
                    else -> JsonBaseBlock(key.resourcePath, builder.build())
                }

                this.jsonBlocks[block.registryName!!] = block
                return true
            }
            catch (t: Throwable) {
                NEBBMod.logger.warn("Error parsing block recipe file '$file'.", t)
                return false
            }
        }
    }

    private fun loadAABBs(json: JsonElement): List<AxisAlignedBB> {
        val result = mutableListOf<AxisAlignedBB>()

        if (json.isJsonArray) {
            val array = json.asJsonArray
            if (array.size() != 6)
                throw JsonParseException("'$json' could not be parsed into 6 doubles.")

            val coords = array
                .map { if (it.isJsonPrimitive) it.asDouble else throw JsonParseException("'$json' could not be converted to float.") }
            result.add(AxisAlignedBB(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]))
        }
        else if (json.isJsonPrimitive) {
            val coords = json.asString.split(',').mapNotNull {
                it.toDoubleOrNull()
            }
            if (coords.size != 6)
                throw JsonParseException("'$json' could not be parsed into 6 doubles.")

            result.add(AxisAlignedBB(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]))
        }

        return result.toList()
    }

    private fun ComplexPartInfo.loadLump(json: JsonElement)= this.also {
        if (!json.isJsonArray)
            throw JsonParseException("'lump' must be an array.")

        val lump = RawLump()

        json.asJsonArray.filterIsInstance<JsonObject>().forEach {
            val rawFacing = JsonUtils.getString(it, "face") ?: throw JsonParseException("'face' not specified.")
            val facing = EnumFacing.byName(rawFacing) ?: throw JsonParseException("'face' is not a valid facing string")

            val points = mutableListOf<Vec3d>()
            it.getMember("points", true)?.asJsonArray?.forEach {
                if (it.isJsonArray) {
                    val (x, y, z) = it.asJsonArray.map { it.asDouble }
                    points.add(Vec3d(x, y, z))
                }
                else if (it.isJsonPrimitive) {
                    val str = it.asString
                    val sign = when (str[0]) {
                        '-' -> -1
                        '+' -> 1
                        else -> throw JsonParseException("'$str' is not a valid offset string")
                    }
                    val axis = EnumFacing.Axis.byName(str.last().toString())
                    val value = sign * (str.substring(1, str.length - 2).toDoubleOrNull() ?: throw JsonParseException("'$str' is not a valid offset string"))
                    points.add((points.lastOrNull() ?: Vec3d.ZERO).addVector(
                        if (axis == EnumFacing.Axis.X) value else 0.0,
                        if (axis == EnumFacing.Axis.Y) value else 0.0,
                        if (axis == EnumFacing.Axis.Z) value else 0.0
                    ))
                }
            }

            val uvs = it.getMember("texture", false)?.asJsonArray?.map {
                val (u, v) = it.asJsonArray.map { it.asFloat }
                Vec2f(u, v)
            }?.toTypedArray() ?: arrayOf(Vec2f(0.0f, 16.0f), Vec2f(16.0f, 16.0f), Vec2f(16.0f, 0.0f), Vec2f(0.0f, 0.0f))

            lump.addFace(points.toTypedArray(), uvs, null, facing)
        }

        it.figures.add(lump)
    }
}
