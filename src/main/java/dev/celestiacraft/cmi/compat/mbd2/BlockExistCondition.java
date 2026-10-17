package dev.celestiacraft.cmi.compat.mbd2;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.mbd2.api.machine.IMultiController;
import com.lowdragmc.mbd2.api.recipe.MBDRecipe;
import com.lowdragmc.mbd2.api.recipe.RecipeCondition;
import com.lowdragmc.mbd2.api.recipe.RecipeLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.ArrayUtils;

import dev.celestiacraft.cmi.Cmi;

import javax.annotation.Nonnull;

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

	@Override
	public String getType() {
		// 必须与 CmiRecipeCondition 注册时使用的 key 完全一致
		return Cmi.loadResource("block_exist").toString();
	}

	@Override
	public Component getTooltips() {
		Component blockNames = Component.empty();
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
		// 借用原版方块条件的图标, 防止因 namespace 的冒号导致 ResourceLocation 崩溃
		return new ResourceTexture("mbd2:textures/gui/condition/block.png"); 
	}
}