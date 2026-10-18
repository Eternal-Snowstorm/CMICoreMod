package dev.celestiacraft.cmi.compat.mbd2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.mbd2.api.machine.IMultiController;
import com.lowdragmc.mbd2.api.recipe.MBDRecipe;
import com.lowdragmc.mbd2.api.recipe.RecipeCondition;
import com.lowdragmc.mbd2.api.recipe.RecipeLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import dev.celestiacraft.cmi.Cmi;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * 当已成型结构中存在 >= 1 个指定方块时满足
 * 同类型条件之间以 AND 组合 (isOr = false)
 */
public class BlockExistCondition extends RecipeCondition {

	@Configurable(name = "config.recipe.condition.block.blocks", collapse = false)
	private Block[] blocks = new Block[0];

	public BlockExistCondition() {
		this(new Block[0]);
	}

	public BlockExistCondition(Block ...blocks) {
		this.blocks = blocks != null ? blocks : new Block[0];
	}

	public static BlockExistCondition of(Block... blocks) {
		return new BlockExistCondition(blocks);
	}

	public static BlockExistCondition of(String... blockIds) {
		Block[] parsedBlocks = java.util.Arrays.stream(blockIds)
				.map(ResourceLocation::tryParse)
				.filter(java.util.Objects::nonNull)
				.map(ForgeRegistries.BLOCKS::getValue)
				.filter(java.util.Objects::nonNull)
				.toArray(Block[]::new);
		return new BlockExistCondition(parsedBlocks);
	}	

	@Override
	public String getType() {
		return Cmi.loadResource("block_exist").toString();
	}

	@Override
	public Component getTooltips() {
		MutableComponent blockNames = Component.empty();
		for (int i = 0; i < blocks.length; i++) {
			blockNames.append(blocks[i].getName());
			if (i < blocks.length - 1) {
				blockNames.append(Component.literal(" || "));
			}
		}
		return Component.translatable(
				"recipe.condition.block_exist.tooltip", 
				blockNames
		);
	}

	@Override
	public boolean test(@Nonnull MBDRecipe recipe, @Nonnull RecipeLogic recipeLogic) {
		if (recipeLogic.machine instanceof IMultiController controller) {
			Level level = controller.getLevel();
			for (BlockPos pos : controller.getMultiblockState().getCache()) {
				if (ArrayUtils.contains(blocks, level.getBlockState(pos).getBlock())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isOr() {
		return false;
	}

	@Override
	public IGuiTexture getIcon() {
		return new ResourceTexture("mbd2:textures/gui/condition/block.png"); 
	}

	@Nonnull
	@Override
	public JsonObject serialize() {
		JsonObject config = super.serialize();
		JsonArray array = new JsonArray();
		for (Block block : blocks) {
			ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
			if (key != null) {
				array.add(key.toString());
			}
		}
		config.add("blocks", array);
		return config;
	}

	@Override
	public RecipeCondition deserialize(@Nonnull JsonObject config) {
		super.deserialize(config);
		JsonArray array = GsonHelper.getAsJsonArray(config, "blocks", new JsonArray());
		blocks = new Block[array.size()];
		for (int i = 0; i < array.size(); i++) {
			String key = array.get(i).getAsString();
			blocks[i] = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(key));
			if (blocks[i] == null) {
				blocks[i] = Blocks.AIR;
			}
		}
		return this;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf) {
		super.toNetwork(buf);
		buf.writeVarInt(blocks.length);
		for (Block block : blocks) {
			buf.writeResourceLocation(Optional.ofNullable(ForgeRegistries.BLOCKS.getKey(block)).orElse(new ResourceLocation("minecraft:air")));
		}
	}

	@Override
	public RecipeCondition fromNetwork(FriendlyByteBuf buf) {
		super.fromNetwork(buf);
		int size = buf.readVarInt();
		blocks = new Block[size];
		for (int i = 0; i < size; i++) {
			blocks[i] = ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation());
		}
		return this;
	}

	@Override
	public CompoundTag toNBT() {
		CompoundTag tag = super.toNBT();
		ListTag array = new ListTag();
		for (Block block : blocks) {
			ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
			if (key != null) {
				array.add(StringTag.valueOf(key.toString()));
			}
		}
		tag.put("blocks", array);
		return tag;
	}

	@Override
	public RecipeCondition fromNBT(CompoundTag tag) {
		super.fromNBT(tag);
		ListTag array = tag.getList("blocks", Tag.TAG_STRING);
		blocks = new Block[array.size()];
		for (int i = 0; i < array.size(); i++) {
			String key = array.getString(i);
			blocks[i] = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(key));
			if (blocks[i] == null) {
				blocks[i] = Blocks.AIR;
			}
		}
		return this;
	}
}