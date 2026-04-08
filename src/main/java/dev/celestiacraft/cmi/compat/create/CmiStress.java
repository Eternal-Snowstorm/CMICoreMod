package dev.celestiacraft.cmi.compat.create;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import dev.celestiacraft.cmi.Cmi;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

public class CmiStress extends ConfigBase {
	private static final Object2DoubleMap<ResourceLocation> DEFAULT_IMPACTS = new Object2DoubleOpenHashMap<>();
	private static final Object2DoubleMap<ResourceLocation> DEFAULT_CAPACITIES = new Object2DoubleOpenHashMap<>();

	protected final Map<ResourceLocation, ForgeConfigSpec.ConfigValue<Double>> capacities = new HashMap<>();
	protected final Map<ResourceLocation, ForgeConfigSpec.ConfigValue<Double>> impacts = new HashMap<>();

	@Override
	public void registerAll(ForgeConfigSpec.Builder builder) {
		builder.comment(".", Comments.su, Comments.impact)
				.push("impact");
		DEFAULT_IMPACTS.forEach((id, value) -> {
			impacts.put(id, builder.define(id.getPath(), value));
		});
		builder.pop();

		builder.comment(".", Comments.su, Comments.capacity)
				.push("capacity");
		DEFAULT_CAPACITIES.forEach((id, value) -> {
			capacities.put(id, builder.define(id.getPath(), value));
		});
		builder.pop();
	}

	@Override
	public @NotNull String getName() {
		return "stressValues";
	}

	@Nullable
	public DoubleSupplier getImpact(Block block) {
		ForgeConfigSpec.ConfigValue<Double> value = impacts.get(CatnipServices.REGISTRIES.getKeyOrThrow(block));
		return value == null ? null : value::get;
	}

	@Nullable
	public DoubleSupplier getCapacity(Block block) {
		ForgeConfigSpec.ConfigValue<Double> value = capacities.get(CatnipServices.REGISTRIES.getKeyOrThrow(block));
		return value == null ? null : value::get;
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setNoImpact() {
		return setImpact(0);
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double value) {
		return (builder) -> {
			assertFromCreateD2D(builder);
			DEFAULT_IMPACTS.put(Cmi.loadResource(builder.getName()), value);
			return builder;
		};
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setCapacity(double value) {
		return (builder) -> {
			assertFromCreateD2D(builder);
			DEFAULT_CAPACITIES.put(Cmi.loadResource(builder.getName()), value);
			return builder;
		};
	}

	private static void assertFromCreateD2D(BlockBuilder<?, ?> builder) {
		if (!builder.getOwner().getModid().equals(Cmi.MODID)) {
			throw new IllegalStateException("Non-Create Desires 2 Dreams blocks cannot be added to Create Desires 2 Dreams config.");
		}
	}

	private static class Comments {
		static String su = "[in Stress Units]";
		static String impact = "Configure the individual stress impact of mechanical blocks. Note that this cost is doubled for every speed increase it receives.";
		static String capacity = "Configure how much stress a source can accommodate for.";
	}
}