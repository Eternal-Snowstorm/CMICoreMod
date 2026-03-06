package dev.celestiacraft.cmi.mixin;

import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import dev.celestiacraft.cmi.api.create.IFanProcessingTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;

@Mixin(CastingBlockEntity.class)
public class CastingBlockEntityMixin implements IFanProcessingTarget {
	@Shadow(remap = false)
	private int timer;

	@Shadow(remap = false)
	private int coolingTime = -1;

	@Shadow(remap = false)
	private ICastingRecipe currentRecipe;

	@Unique
	private double cmi$remainder;

	@Unique
	private void cmi$process(int times) {
		if (timer + times < 0) {
			timer = 0;
		} else if (timer + times > coolingTime) {
			timer = coolingTime;
		} else if (timer > 0) {
			timer += times;
		}
	}

	@Unique
	public boolean cmi$canProcess(FanProcessingType type) {
		return currentRecipe != null;
	}

	/**
	 * factor 是影响因子
	 * <p>
	 * 提升 = (转速 / 64) 的平方根 * 因子 * 100 %
	 * <p>
	 * 64 转速就是 2 倍速, 256 转速就是 3 倍速
	 * <p>
	 * 因此如果想要提高速度只需要将 factor 调大就行
	 *
	 * @param type  风扇处理类型(具体查看：{@linkplain AllFanProcessingTypes})
	 * @param speed 生成气流的风扇速度(0.0f至最大风扇速度)
	 */
	@Unique
	public void cmi$process(FanProcessingType type, float speed) {
		double factor = 1.0F;
		double tick = Math.sqrt(Math.abs(speed) / 64.0f) * factor;

		int sign = tick < 0 ? -1 : 1;

		tick = Math.abs(tick);

		// 整数部分
		int integer = (int) tick;
		// 小数部分
		double remainer = tick - integer;

		// 累加小数部分
		cmi$remainder += remainer;

		// 累计进度超过1, 额外处理1次
		if (cmi$remainder > 1) {
			cmi$remainder--;
			integer++;
		}

		cmi$process(sign * integer);
	}
}