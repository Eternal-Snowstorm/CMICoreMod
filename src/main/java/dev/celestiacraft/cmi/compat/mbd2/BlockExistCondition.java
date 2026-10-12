package dev.celestiacraft.cmi.compat.mbd2;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.mbd2.api.machine.IMultiController;
import com.lowdragmc.mbd2.api.recipe.MBDRecipe;
import com.lowdragmc.mbd2.api.recipe.RecipeCondition;
import com.lowdragmc.mbd2.api.recipe.RecipeLogic;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;

/**
 * 当已成型结构中存在 >= 1 个指定方块时满足
 * 同类型条件之间以 AND 组合 (isOr = false)
 */
@LDLRegister(name = "block_exist", registry = "mbd2:recipe_condition")
public class BlockExistCondition extends RecipeCondition {

	@Configurable(name = "config.recipe.condition.block.blocks", collapse = false)
	private Block[] blocks;

	public BlockExistCondition() {
		this(new Block[0]);
	}

	public BlockExistCondition(Block... blocks) {
		this.blocks = blocks;
	}

	@Override
	public String getType() {
		return "block_exist";
	}

	@Override
	public Component getTooltips() {
		var blockNames = Component.empty();
		for (int i = 0; i < blocks.length; i++) {
			blockNames.append(blocks[i].getName());
			if (i < blocks.length - 1) {
				blockNames.append(Component.literal(" || "));
			}
		}
		return Component.translatable(
				"recipe.condition.block_exist.tooltip", blockNames);
	}

	@Override
	public boolean test(@Nonnull MBDRecipe recipe, @Nonnull RecipeLogic recipeLogic) {
		if (recipeLogic.machine instanceof IMultiController controller) {
			var level = controller.getLevel();
			for (var pos : controller.getMultiblockState().getCache()) {
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
}