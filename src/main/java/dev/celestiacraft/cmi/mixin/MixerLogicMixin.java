package dev.celestiacraft.cmi.mixin;

import blusunrize.immersiveengineering.api.crafting.MixerRecipe;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.mixer.MixerLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.mixer.MixerLogic.State;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.MultiFluidTank;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

/**
 * 修复 IE 搅拌机的神必流体输出 bug。
 * <p>
 * Bugs：MultiFluidTank 使用 List<FluidStack> 存储多种流体，
 * 新流体总是 append 到末尾。当输入流体被完全消耗后重新泵入时，
 * 它会被插入到产物流体之后，破坏了输出逻辑依赖的列表顺序。你会感觉刚抽进去的流体又被抽出来了。
 * <p>
 * 具体表现：
 * outputAll=false（仅输出底部流体）：getFluid() 取最后一个元素，
 * 拿到的是重新泵入的输入流体而非产物
 * outputAll=true（输出所有流体）：从 index 0 开始迭代，
 * 先输出的流体填满目标容器后，后续流体无法再输出
 * <p>
 * 对于IE的神必bug，只能替换 outputFluids 逻辑，使其感知活跃配方。
 * 只输出不匹配任何活跃配方输入的流体（即产物流体）。
 * 当没有活跃配方时，回退到原始行为输出所有流体。
 */
@Mixin(value = MixerLogic.class, remap = false)
public class MixerLogicMixin {
	// 缓存logic上下文
	@Unique
	private IMultiblockContext<State> cmi$currentContext;

	@Inject(method = "tickServer", at = @At("HEAD"))
	private void cmi$captureContext(IMultiblockContext<State> context, CallbackInfo ci) {
		this.cmi$currentContext = context;
	}

	/**
	 * 拦截 tickServer 中对 outputFluids 的调用，替换为配方的输出逻辑。
	 */
	@Redirect(
			method = "tickServer",
			at = @At(
					value = "INVOKE",
					target = "Lblusunrize/immersiveengineering/common/blocks/multiblocks/logic/mixer/MixerLogic;outputFluids(Lblusunrize/immersiveengineering/common/blocks/multiblocks/logic/mixer/MixerLogic$State;Z)Z"
			)
	)
	private boolean cmi$fixedOutputFluids(MixerLogic instance, State state, boolean foundRecipe) {
		int fluidTypes = state.tank.getFluidTypes();
		if (fluidTypes <= 0)
			return false;
		IFluidHandler output = ((MixerStateAccessor) state).cmi$getOutputRef().getNullable();
		if (output == null)
			return false;
		Level level = cmi$currentContext.getLevel().getRawLevel();
		List<MultiblockProcess<MixerRecipe, ProcessContextInMachine<MixerRecipe>>> queue = state.processor.getQueue();
		if (!state.outputAll) {
			return cmi$outputSingleFluid(state, output, queue, level, foundRecipe);
		} else {
			return cmi$outputAllFluids(state, output, queue, level, foundRecipe);
		}
	}

	/**
	 * 仅输出产物模式（outputAll=false）
	 * 找到第一个不匹配任何活跃配方输入的流体并输出。
	 */
	@Unique
	private static boolean cmi$outputSingleFluid(
			State state, IFluidHandler output,
			List<MultiblockProcess<MixerRecipe, ProcessContextInMachine<MixerRecipe>>> queue,
			Level level, boolean foundRecipe
	) {
		FluidStack toOutput = null;
		for (FluidStack fs : state.tank.fluids) {
			if (!cmi$isRecipeInput(fs, queue, level)) {
				toOutput = fs;
				break;
			}
		}

		// 如果没有找到产物流体
		if (toOutput == null) {
			// 有活跃配方时不输出（原料留给配方用）
			if (foundRecipe)
				return false;
			// 没有活跃配方时回退到原始行为
			toOutput = state.tank.getFluid();
		}

		if (toOutput.isEmpty())
			return false;

		int maxAmount = Math.min(toOutput.getAmount(), FluidType.BUCKET_VOLUME);
		FluidStack out = Utils.copyFluidStackWithAmount(toOutput, maxAmount, false);
		int drained = output.fill(out, FluidAction.EXECUTE);
		state.tank.drain(new FluidStack(toOutput, drained), FluidAction.EXECUTE);
		return drained > 0;
	}

	/**
	 * 输出所有模式（outputAll=true）
	 * 优先输出产物流体。当没有活跃配方时也输出原料流体。
	 * jb的给我力竭了，再看IE这个💩我是傻逼。
	 */
	@Unique
	private static boolean cmi$outputAllFluids(
			State state, IFluidHandler output,
			List<MultiblockProcess<MixerRecipe, ProcessContextInMachine<MixerRecipe>>> queue,
			Level level, boolean foundRecipe
	) {
		int totalOut = 0;
		// 优先级1 -> 只输出产物流体（不匹配任何活跃配方输入的流体）.
		Iterator<FluidStack> it = state.tank.fluids.iterator();
		while (it.hasNext()) {
			FluidStack fs = it.next();
			if (fs == null || cmi$isRecipeInput(fs, queue, level))
				continue;
			int maxAmount = Math.min(fs.getAmount(), FluidType.BUCKET_VOLUME - totalOut);
			FluidStack out = Utils.copyFluidStackWithAmount(fs, maxAmount, false);
			int drained = output.fill(out, FluidAction.EXECUTE);
			MultiFluidTank.drain(drained, fs, it, FluidAction.EXECUTE);
			totalOut += drained;
			if (totalOut >= FluidType.BUCKET_VOLUME)
				break;
		}
		// 优先级2 -> 如果还有空间且没有活跃配方，也输出原料流体。
		if (totalOut < FluidType.BUCKET_VOLUME && !foundRecipe) {
			it = state.tank.fluids.iterator();
			while (it.hasNext()) {
				FluidStack fs = it.next();
				if (fs == null)
					continue;
				int maxAmount = Math.min(fs.getAmount(), FluidType.BUCKET_VOLUME - totalOut);
				FluidStack out = Utils.copyFluidStackWithAmount(fs, maxAmount, false);
				int drained = output.fill(out, FluidAction.EXECUTE);
				MultiFluidTank.drain(drained, fs, it, FluidAction.EXECUTE);
				totalOut += drained;
				if (totalOut >= FluidType.BUCKET_VOLUME)
					break;
			}
		}
		return totalOut > 0;
	}

	/**
	 * 便利匹配配方
	 */
	@Unique
	private static boolean cmi$isRecipeInput(
			FluidStack fluid,
			List<MultiblockProcess<MixerRecipe, ProcessContextInMachine<MixerRecipe>>> queue,
			Level level
	) {
		for (MultiblockProcess<MixerRecipe, ?> process : queue) {
			MixerRecipe recipe = MixerRecipe.RECIPES.getById(level, process.getRecipeId());
			if (recipe != null && recipe.fluidInput.testIgnoringAmount(fluid)) {
				return true;
			}
		}
		return false;
	}
}