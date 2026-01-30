package top.nebula.cmi.compat.create;

import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.register.ModBlocks;
import top.nebula.cmi.config.CommonConfig;

import javax.annotation.Nullable;

/**
 * CMI 模组的应力值提供器，用于从配置文件读取应力值。
 * <p>
 * 实现 Create 的 {@link BlockStressValues.IStressValueProvider} 接口，
 * 使得应力值可以通过配置文件动态调整，而不是硬编码。
 *
 * <h2>使用方式</h2>
 * 在模组初始化时调用 {@link #register()} 方法注册此提供器。
 */
public class CmiStressValueProvider implements BlockStressValues.IStressValueProvider {

	/**
	 * 注册此应力值提供器到 Create 的系统中。
	 * 应在 FMLCommonSetupEvent 中调用。
	 */
	public static void register() {
		BlockStressValues.registerProvider(Cmi.MODID, new CmiStressValueProvider());
	}

	@Override
	public double getImpact(Block block) {
		if (block == ModBlocks.STEAM_HAMMER.get()) {
			return CommonConfig.STEAM_HAMMER_STRESS_IMPACT.get();
		}
		ResourceLocation blockId = RegisteredObjects.getKeyOrThrow(block);
		Double defaultImpact = BlockStressDefaults.DEFAULT_IMPACTS.get(blockId);
		return defaultImpact != null ? defaultImpact : 0;
	}

	@Override
	public double getCapacity(Block block) {
		if (block == ModBlocks.ACCELERATOR_MOTOR.get()) {
			return CommonConfig.ACCELERATOR_MOTOR_STRESS_CAPACITY.get();
		}
		ResourceLocation blockId = RegisteredObjects.getKeyOrThrow(block);
		Double defaultCapacity = BlockStressDefaults.DEFAULT_CAPACITIES.get(blockId);
		return defaultCapacity != null ? defaultCapacity : 0;
	}

	@Override
	public boolean hasImpact(Block block) {
		if (block == ModBlocks.STEAM_HAMMER.get()) {
			return true;
		}
		ResourceLocation blockId = RegisteredObjects.getKeyOrThrow(block);
		return BlockStressDefaults.DEFAULT_IMPACTS.containsKey(blockId);
	}

	@Override
	public boolean hasCapacity(Block block) {
		if (block == ModBlocks.ACCELERATOR_MOTOR.get()) {
			return true;
		}
		ResourceLocation blockId = RegisteredObjects.getKeyOrThrow(block);
		return BlockStressDefaults.DEFAULT_CAPACITIES.containsKey(blockId);
	}

	@Nullable
	@Override
	public Couple<Integer> getGeneratedRPM(Block block) {
		if (block == ModBlocks.ACCELERATOR_MOTOR.get()) {
			int maxSpeed = CommonConfig.ACCELERATOR_MOTOR_MAX_SPEED.get();
			return Couple.create(0, maxSpeed);
		}
		ResourceLocation blockId = RegisteredObjects.getKeyOrThrow(block);
		var supplier = BlockStressDefaults.GENERATOR_SPEEDS.get(blockId);
		return supplier != null ? supplier.get() : null;
	}
}
