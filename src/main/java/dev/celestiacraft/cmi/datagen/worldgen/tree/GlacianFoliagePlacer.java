package dev.celestiacraft.cmi.datagen.worldgen.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.celestiacraft.cmi.common.register.CmiFoliagePlacer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import org.jetbrains.annotations.NotNull;

/**
 * 薄盘状树冠
 * <p>
 * 每往上一层半径收一格, 最外圈随机镂空, 再随机往下挂一圈叶子.
 * 现实里寒带树的树冠必须是"薄而分层"的: 太厚会积住雪, 太重会折断;
 * 分层 + 边缘下垂则能让雪顺着枝条滑走, 顺便也让树冠从下面看像一把伞.
 */
public class GlacianFoliagePlacer extends FoliagePlacer {
	public static final Codec<GlacianFoliagePlacer> CODEC = RecordCodecBuilder.create((instance) -> {
		return foliagePlacerParts(instance).and(instance.group(
				Codec.intRange(1, 6)
						.fieldOf("layers")
						.forGetter((placer) -> {
							return placer.layers;
						}),
				Codec.floatRange(0.0F, 1.0F)
						.fieldOf("hole_chance")
						.forGetter((placer) -> {
							return placer.holeChance;
						}),
				Codec.floatRange(0.0F, 1.0F)
						.fieldOf("droop_chance")
						.forGetter((placer) -> {
							return placer.droopChance;
						})
		)).apply(instance, GlacianFoliagePlacer::new);
	});

	private final int layers;
	private final float holeChance;
	private final float droopChance;

	public GlacianFoliagePlacer(IntProvider radius, IntProvider offset, int layers, float holeChance, float droopChance) {
		super(radius, offset);
		this.layers = layers;
		this.holeChance = holeChance;
		this.droopChance = droopChance;
	}

	@Override
	protected @NotNull FoliagePlacerType<?> type() {
		return CmiFoliagePlacer.GLACIAN.get();
	}

	@Override
	protected void createFoliage(@NotNull LevelSimulatedReader level, FoliagePlacer.@NotNull FoliageSetter blockSetter, @NotNull RandomSource random, @NotNull TreeConfiguration config, int freeTreeHeight, FoliagePlacer.FoliageAttachment attachment, int foliageHeight, int foliageRadius, int offset) {
		BlockPos center = attachment.pos().above(offset);
		int widest = Math.max(0, foliageRadius + attachment.radiusOffset());

		for (int y = 0; y < foliageHeight; y++) {
			int range = widest - y;

			if (range < 0) {
				break;
			}

			placeLeavesRow(level, blockSetter, random, config, center, range, y, attachment.doubleTrunk());
		}

		placeDroopingRim(level, blockSetter, random, config, center, widest, attachment);
	}

	/**
	 * 最外一圈随机往下多挂一格叶子, 做出枝条末端垂下来的那一圈叶帘
	 */
	private void placeDroopingRim(LevelSimulatedReader level, FoliagePlacer.FoliageSetter blockSetter, RandomSource random, TreeConfiguration config, BlockPos center, int range, FoliagePlacer.FoliageAttachment attachment) {
		if (range <= 0) {
			return;
		}

		int extra = attachment.doubleTrunk() ? 1 : 0;

		for (int x = -range; x <= range + extra; x++) {
			for (int z = -range; z <= range + extra; z++) {
				if (Math.max(Math.abs(x), Math.abs(z)) != range) {
					continue;
				}

				if (random.nextFloat() >= droopChance) {
					continue;
				}

				tryPlaceLeaf(level, blockSetter, random, config, center.offset(x, -1, z));
			}
		}
	}

	@Override
	public int foliageHeight(@NotNull RandomSource random, int height, @NotNull TreeConfiguration config) {
		return layers;
	}

	@Override
	protected boolean shouldSkipLocation(@NotNull RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
		/*
		 * 只在最外圈镂空.
		 * MC 的树叶衰减是按"从原木出发, 在叶子之间连通的步数"算的, 超过 6 步就会掉;
		 * 在叶团内部挖洞会把外圈的叶子孤立出来, 那些叶子就会被判掉, 所以内部必须实心
		 */
		if (localX != range && localZ != range) {
			return false;
		}

		if (localX == range && localZ == range) {
			return true;
		}

		return random.nextFloat() < holeChance;
	}
}