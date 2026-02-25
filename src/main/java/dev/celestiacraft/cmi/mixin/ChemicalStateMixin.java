package dev.celestiacraft.cmi.mixin;

import com.teammoeg.immersiveindustry.content.chemical_reactor.ChemicalState;
import com.teammoeg.immersiveindustry.util.CapabilityFacing;
import com.teammoeg.immersiveindustry.util.CapabilityProcessor;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 ImmersiveIndustry 反应釜流体输出口 bug。
 * ps:傻逼反应釜，写牛魔呢，外面接口和GUI对应的乱七八糟的我去牛魔的。😡
 * Bug：ChemicalState 构造函数中，输出口的 capability 绑定了 inTank（输入罐）而非 outTank（输出罐）。
 * 导致管道从输出口抽取时，抽到的是原料流体而不是产物流体。
 * tryOutput 的主动推送逻辑用的是正确的 outTank，所以只有怼着有缓存的容器才能正常输出。
 *
 * 修复：在构造函数尾部，用 outTank 重新注册输出口的 drainOnly capability，覆盖错误的 inTank 注册。
 */
@Mixin(value = ChemicalState.class, remap = false)
public class ChemicalStateMixin {

	@Inject(method = "<init>", at = @At("TAIL"))
	private void cmi$fixOutputFluidCapability(IInitialMultiblockContext<ChemicalState> capabilitySource, CallbackInfo ci) {
		ChemicalStateAccessor self = (ChemicalStateAccessor) this;
		FluidTank[] outTank = self.cmi$getOutTank();
		CapabilityProcessor capabilities = self.cmi$getCapabilities();
		int num = 0;
		for (CapabilityFacing i : ChemicalLogicAccessor.cmi$getOut()) {
			ArrayFluidHandler todrain = ArrayFluidHandler.drainOnly(outTank[num++], capabilitySource.getMarkDirtyRunnable());
			capabilities.addCapability(ForgeCapabilities.FLUID_HANDLER, i, todrain);
		}
	}
}
