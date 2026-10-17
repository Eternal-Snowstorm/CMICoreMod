package dev.celestiacraft.cmi.compat.mbd2;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
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
// TODO 注册 MBD2 配方条件：ldlib 1.0.52.a 的 @LDLRegister 只有 name/group/modID/priority，
//      没有 registry()，且 group() 无默认值 —— 原写法 @LDLRegister(name = "block_exist", registry = "mbd2:recipe_condition")
//      会让 javac 在注解处理阶段报错，进而中止后续 AP 轮次、Lombok 不生成任何成员（全模块 79 个级联错误）。
//      正确入口是 com.lowdragmc.mbd2.api.registry.MBDRegistries.RECIPE_CONDITIONS
//      (MBDRegistry.String<Class<? extends RecipeCondition>>)，需在模组初始化时注册。
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