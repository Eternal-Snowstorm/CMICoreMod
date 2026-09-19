package dev.celestiacraft.cmi.datagen.worldgen.tree;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.celestiacraft.cmi.common.register.CmiTreeDecorator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
* 在树冠最上层压一层积雪, 冰星树的招牌外观
* <p>
* 只处理每根 (x, z) 柱子上最高的那片叶子, 并且要求它上方是空气,
* 否则会在树冠内部塞满雪块.
*/
public class SnowCapDecorator extends TreeDecorator {
	public static final Codec<SnowCapDecorator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				BlockStateProvider.CODEC.fieldOf("provider").forGetter((decorator) -> decorator.provider),
				Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter((decorator) -> decorator.chance)
		).apply(instance, SnowCapDecorator::new);
	});

	private final BlockStateProvider provider;
	private final float chance;

	public SnowCapDecorator(BlockStateProvider provider, float chance) {
		this.provider = provider;
		this.chance = chance;
	}

	@Override
	protected @NotNull TreeDecoratorType<?> type() {
		return CmiTreeDecorator.SNOW_CAP.get();
	}

	@Override
	public void place(TreeDecorator.Context context) {
		List<BlockPos> leaves = context.leaves();
		Set<BlockPos> columns = Sets.newHashSet();
		List<BlockPos> caps = Lists.newArrayList();

		for (int index = leaves.size() - 1; index >= 0; index--) {
			BlockPos pos = leaves.get(index);
			BlockPos column = new BlockPos(pos.getX(), 0, pos.getZ());

			if (!columns.add(column)) {
				continue;
			}

			if (context.isAir(pos.above())) {
				caps.add(pos);
			}
		}

		for (BlockPos pos : caps) {
			if (context.random().nextFloat() >= chance) {
				continue;
			}

			context.setBlock(pos.above(), provider.getState(context.random(), pos.above()));
		}
	}
}
